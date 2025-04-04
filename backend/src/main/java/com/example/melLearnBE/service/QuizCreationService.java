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
    private final Gson gson;
    
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
        
        Type listType = new TypeToken<List<ListeningAnswer>>() {}.getType();
        return gson.fromJson(probListJsonArray, listType);
    }

    private ListeningQuiz createListeningQuiz(QuizRequest quizRequest, LearningLevel level, List<ListeningAnswer> answerContext) {
        return ListeningQuiz.builder()
                .blankedText(quizRequest.getLyric())
                .musicId(quizRequest.getMusicId())
                .level(level)
                .answerList(answerContext.stream()
                        .map(ListeningAnswer::getAnswerWord)
                        .toList())
                .build();
    }

    private String getPrompt(QuizType quizType, Member member) {
        try {
            String promptPath = PROMPT_PREFIX + File.separator + quizType + File.separator + quizType + TXT_EXTENSION;
            return new String(Files.readAllBytes(Paths.get(promptPath)));
        } catch (IOException e) {
            log.error("Error reading prompt file: ", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String addLineNumbers(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(i + 1).append(". ").append(lines[i]).append("\n");
        }
        return result.toString();
    }

    @Transactional
    public CompletableFuture<QuizListDto> createQuizList(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        ChatRequest chatRequest = createChatRequest(quizType, member, quizRequest);
        
        return processQuizRequest(chatRequest, quizRequest, member);
    }

    private ChatRequest createChatRequest(QuizType quizType, Member member, QuizRequest quizRequest) {
        List<ChatQuestion> question = Collections.singletonList(
                ChatQuestion.builder()
                        .text(quizRequest.getLyric())
                        .level(member.getLevel().getValue())
                        .build()
        );
        
        return ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(question.toString() + "\n" + promptDetailUtil.get(member, quizRequest))
                .build();
    }

    private CompletableFuture<QuizListDto> processQuizRequest(ChatRequest chatRequest, QuizRequest quizRequest, Member member) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return CompletableFuture.completedFuture(createQuizListFromResponse(chatRequest, quizRequest, member));
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
        if (member.getLevel() == LearningLevel.Beginner) {
            return openAIService.requestFinetuningModel(chatRequest);
        } else {
            return openAIService.requestGPT(chatRequest);
        }
    }

    private List<Quiz> parseQuizzes(String jsonContent) {
        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
        JsonArray probListJsonArray = jsonObject.getAsJsonArray(QUIZ_JSON_PREFIX);
        
        Type listType = new TypeToken<List<Quiz>>() {}.getType();
        return gson.fromJson(probListJsonArray, listType);
    }

    private QuizList createAndSaveQuizList(List<Quiz> quizzes, QuizRequest quizRequest, Member member) {
        QuizList quizList = QuizList.builder()
                .quizType(quizRequest.getQuizType())
                .quizzes(quizzes)
                .level(member.getLevel())
                .musicId(quizRequest.getMusicId())
                .build();
        
        return quizListRepository.save(quizList);
    }
}
