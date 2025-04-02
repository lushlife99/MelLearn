package com.example.melLearnBE.service;

import com.example.melLearnBE.adapter.LocalDateTimeTypeAdapter;
import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.openAI.ChatQuestion;
import com.example.melLearnBE.dto.request.openAI.ChatRequest;
import com.example.melLearnBE.dto.response.openAI.ChatGPTResponse;
import com.example.melLearnBE.dto.response.openAI.ListeningAnswer;
import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.model.ListeningQuiz;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.model.Quiz;
import com.example.melLearnBE.model.QuizList;
import com.example.melLearnBE.repository.ListeningQuizRepository;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.QuizRepository;
import com.example.melLearnBE.util.PromptDetailUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizCreationService {

    private final QuizListRepository quizListRepository;
    private final OpenAIService openAIService;
    private final PromptDetailUtil promptDetailUtil;
    private final QuizRepository quizRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    
    private static final String QUIZ_JSON_PREFIX = "probList";
    private static final String ANSWERLIST = "answerList";
    private static final String PROMPT_PREFIX = "prompt";
    private static final String TXT_EXTENSION = ".txt";
    private static final int MAX_RETRIES = 3;

    @Transactional
    public CompletableFuture<ListeningQuizDto> createListeningQuiz(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        ChatRequest listeningRequest = createListeningChatRequest(quizType, member, quizRequest);
        
        return processListeningQuizRequest(listeningRequest, quizRequest, member);
    }

    private ChatRequest createListeningChatRequest(QuizType quizType, Member member, QuizRequest quizRequest) {
        return ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(addLineNumbers(quizRequest.getLyric()) + "\n" + promptDetailUtil.get(member, quizRequest))
                .build();
    }

    private CompletableFuture<ListeningQuizDto> processListeningQuizRequest(ChatRequest request, QuizRequest quizRequest, Member member) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return CompletableFuture.completedFuture(createListeningQuizFromResponse(request, quizRequest, member));
            } catch (Exception e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    log.error("Quiz Create Error", e);
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ListeningQuizDto createListeningQuizFromResponse(ChatRequest request, QuizRequest quizRequest, Member member) {
        ChatGPTResponse chatGPTResponse = openAIService.requestGPT(request);
        String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        List<ListeningAnswer> answerContext = parseListeningAnswers(jsonContent);
        
        ListeningQuiz listeningQuiz = createListeningQuiz(quizRequest, member.getLevel(), answerContext);
        listeningQuizRepository.save(listeningQuiz);
        
        return new ListeningQuizDto(listeningQuiz);
    }

    private List<ListeningAnswer> parseListeningAnswers(String jsonContent) {
        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
        JsonArray probListJsonArray = jsonObject.getAsJsonArray(ANSWERLIST);
        Gson gson = createGsonWithLocalDateTimeAdapter();
        
        Type listType = new TypeToken<List<ListeningAnswer>>() {}.getType();
        return gson.fromJson(probListJsonArray, listType);
    }

    private Gson createGsonWithLocalDateTimeAdapter() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setLenient()
                .create();
    }

    private ListeningQuiz createListeningQuiz(QuizRequest quizRequest, LearningLevel level, List<ListeningAnswer> answerContext) {
        ListeningQuiz listeningQuiz = ListeningQuiz.builder()
                .level(level)
                .musicId(quizRequest.getMusicId())
                .build();
        
        blankToListeningText(quizRequest, level, answerContext, listeningQuiz);
        return listeningQuiz;
    }

    public static String addLineNumbers(String input) {
        String[] lines = input.split("\n");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < lines.length; i++) {
            result.append(i).append(". ").append(lines[i]).append("\n");
        }
        
        return result.toString();
    }

    private void blankToListeningText(QuizRequest quizRequest, LearningLevel level, List<ListeningAnswer> answerContext, ListeningQuiz listeningQuiz) {
        String lyric = quizRequest.getLyric();
        List<String> answerList = new ArrayList<>();
        int totalBlankSize = getTotalBlankSize(level);
        
        Collections.shuffle(answerContext);
        String[] lyricArray = lyric.split("\n");
        StringBuilder modifiedLyrics = new StringBuilder();
        
        processLyricLines(lyricArray, answerContext, totalBlankSize, answerList, modifiedLyrics);
        
        listeningQuiz.setBlankedText(modifiedLyrics.toString());
        listeningQuiz.setAnswerList(answerList);
    }

    private int getTotalBlankSize(LearningLevel level) {
        if (level.equals(LearningLevel.Beginner)) {
            return 10;
        } else if (level.equals(LearningLevel.Intermediate)) {
            return 20;
        } else {
            return 30;
        }
    }

    private void processLyricLines(String[] lyricArray, List<ListeningAnswer> answerContext, int totalBlankSize, 
            List<String> answerList, StringBuilder modifiedLyrics) {
        for (int i = 0; i < lyricArray.length; i++) {
            try {
                String line = lyricArray[i];
                String[] tokens = line.split(" ");
                
                processLineTokens(tokens, answerContext, i, answerList, totalBlankSize);
                
                modifiedLyrics.append(String.join(" ", tokens));
                modifiedLyrics.append("\n");
                
                if (answerList.size() >= totalBlankSize) {
                    appendRemainingLines(lyricArray, i, modifiedLyrics);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.warn("Array index out of bounds while processing lyric line", e);
            }
        }
    }

    private void processLineTokens(String[] tokens, List<ListeningAnswer> answerContext, int lineIndex, 
            List<String> answerList, int totalBlankSize) {
        for (ListeningAnswer listeningAnswer : answerContext) {
            if (listeningAnswer.getLineIndex() == lineIndex) {
                for (int j = 0; j < tokens.length; j++) {
                    if (tokens[j].contains(listeningAnswer.getAnswerWord())) {
                        tokens[j] = "__";
                        answerList.add(listeningAnswer.getAnswerWord());
                        break;
                    }
                }
                if (answerList.size() >= totalBlankSize) {
                    break;
                }
            }
        }
    }

    private void appendRemainingLines(String[] lyricArray, int currentIndex, StringBuilder modifiedLyrics) {
        for (int j = currentIndex + 1; j < lyricArray.length; j++) {
            modifiedLyrics.append(lyricArray[j]).append("\n");
        }
    }

    @Transactional
    public CompletableFuture<QuizListDto> createQuizList(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        if (isSupportedQuizType(quizType)) {
            return CompletableFuture.completedFuture(requestQuiz(quizRequest, member));
        }
        throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_TYPE);
    }

    private boolean isSupportedQuizType(QuizType quizType) {
        return quizType.equals(QuizType.READING) || 
               quizType.equals(QuizType.VOCABULARY) || 
               quizType.equals(QuizType.GRAMMAR);
    }

    private QuizListDto requestQuiz(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        ChatRequest chatRequest = createQuizChatRequest(quizType, member, quizRequest);
        
        return processQuizRequest(chatRequest, quizRequest, member);
    }

    private ChatRequest createQuizChatRequest(QuizType quizType, Member member, QuizRequest quizRequest) {
        ChatQuestion question = ChatQuestion.builder()
                .text(quizRequest.getLyric())
                .totalProblem(5)
                .lang(member.getLangType().getIso639Value())
                .level(member.getLevel().getValue())
                .build();
        
        return ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(question.toString() + "\n" + promptDetailUtil.get(member, quizRequest))
                .build();
    }

    private QuizListDto processQuizRequest(ChatRequest chatRequest, QuizRequest quizRequest, Member member) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return createQuizListFromResponse(chatRequest, quizRequest, member);
            } catch (Exception e) {
                retries++;
                log.error("Quiz creation error", e);
                if (retries >= MAX_RETRIES) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private QuizListDto createQuizListFromResponse(ChatRequest chatRequest, QuizRequest quizRequest, Member member) {
        ChatGPTResponse chatGPTResponse = getChatGPTResponse(chatRequest, quizRequest, member);
        String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        List<Quiz> quizzes = parseQuizzes(jsonContent);
        
        QuizList quizList = createAndSaveQuizList(quizzes, quizRequest, member);
        return new QuizListDto(quizList);
    }

    private ChatGPTResponse getChatGPTResponse(ChatRequest chatRequest, QuizRequest quizRequest, Member member) {
        if (quizRequest.getQuizType().equals(QuizType.GRAMMAR)) {
            return openAIService.requestFinetuningModel(chatRequest);
        } else if (member.getLangType().equals(Language.ENGLISH)) {
            return openAIService.requestGPT(chatRequest);
        } else {
            return openAIService.requestGPT4(chatRequest);
        }
    }

    private List<Quiz> parseQuizzes(String jsonContent) {
        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
        JsonArray probListJsonArray = jsonObject.getAsJsonArray(QUIZ_JSON_PREFIX);
        Type listType = new TypeToken<List<Quiz>>() {}.getType();
        Gson gson = createGsonWithLocalDateTimeAdapter();
        
        return gson.fromJson(probListJsonArray, listType);
    }

    private QuizList createAndSaveQuizList(List<Quiz> quizzes, QuizRequest quizRequest, Member member) {
        QuizList quizList = QuizList.builder()
                .quizzes(quizzes)
                .createdTime(LocalDateTime.now())
                .quizType(quizRequest.getQuizType())
                .level(member.getLevel())
                .musicId(quizRequest.getMusicId())
                .build();
        
        for (Quiz quiz : quizzes) {
            quiz.setQuizList(quizList);
        }
        
        List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes);
        QuizList savedQuizList = quizListRepository.save(quizList);
        savedQuizList.setQuizzes(savedQuizzes);
        
        return savedQuizList;
    }

    private String getPrompt(QuizType quizType, Member member) {
        String filePath = "." + File.separator + PROMPT_PREFIX + File.separator + quizType;
        try {
            return Files.readString(Paths.get(filePath + TXT_EXTENSION));
        } catch (IOException e) {
            log.error("Error reading prompt file", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
