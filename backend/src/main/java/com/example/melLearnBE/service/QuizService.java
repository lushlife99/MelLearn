package com.example.melLearnBE.service;

import com.example.melLearnBE.adapter.LocalDateTimeTypeAdapter;
import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.ListeningSubmitRequest;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.dto.request.openAI.ChatQuestion;
import com.example.melLearnBE.dto.request.openAI.ChatRequest;
import com.example.melLearnBE.dto.response.openAI.ChatGPTResponse;
import com.example.melLearnBE.dto.response.openAI.ListeningAnswer;
import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.*;
import com.example.melLearnBE.repository.*;
import com.example.melLearnBE.repository.querydsl.SubmitJpaRepository;
import com.example.melLearnBE.util.PromptDetailUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final QuizSubmitRepository quizSubmitRepository;
    private final QuizListRepository quizListRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OpenAIService openAIService;
    private final PromptDetailUtil promptDetailUtil;
    private final ListeningSubmitRepository listeningSubmitRepository;
    private final SubmitJpaRepository submitJpaRepository;


    /**
     * 동시성 고려해서 코드 업데이트 해야함.
     * 아직 안했음.
     */

    public Page getSubmitList(QuizType quizType, int pageNo, String sortBy, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        if(quizType.equals(QuizType.LISTENING)) {
            return submitJpaRepository.findListeningSubmitWithPaging(member.getId(), pageNo, 10);
        } else if (quizType.equals(QuizType.READING) || quizType.equals(QuizType.VOCABULARY) || quizType.equals(QuizType.GRAMMAR)) {
            return submitJpaRepository.findSubmitWithPaging(member.getId(), quizType, pageNo, 10);
        } else if (quizType.equals(QuizType.SPEAKING)) {
            return submitJpaRepository.findSpeakingSubmitWithPaging(member.getId(), pageNo, 10);
        }

        throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    public QuizSubmitDto submit(QuizSubmitRequest submitRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        QuizList quizList = quizListRepository.findByMusicIdAndQuizTypeAndLevel(submitRequest.getMusicId(), submitRequest.getQuizType(), member.getLevel())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        double score = calCorrectRate(submitRequest, quizList);

        QuizSubmit quizSubmit = QuizSubmit.builder()
                .quizList(quizList)
                .submitAnswerList(submitRequest.getAnswers())
                .member(member)
                .score(score)
                .build();

        return new QuizSubmitDto(quizSubmitRepository.save(quizSubmit));
    }

    public ListeningSubmitDto listeningSubmit(ListeningSubmitRequest submitRequest, HttpServletRequest request) {

        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        ListeningQuiz listeningQuiz = listeningQuizRepository.findByMusicIdAndLevel(submitRequest.getMusicId(), member.getLevel())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        List<String> answerList = listeningQuiz.getAnswerList();
        List<String> submitWordList = submitRequest.getSubmitWordList();
        int correctCount = 0;

        if(answerList.size() != submitWordList.size()) {
            throw new CustomException(ErrorCode.REQUEST_ARRAY_SIZE_NOT_MATCHED);
        }

        for(int i = 0; i < answerList.size(); i++) {
            String answerWord = answerList.get(i);
            String submitWord = submitWordList.get(i);

            if (answerWord.equals(submitWord.trim())) {
                submitWordList.set(i, answerWord);
                correctCount++;
            }
        }

        ListeningSubmit listeningSubmit = ListeningSubmit.builder()
                .listeningQuiz(listeningQuiz)
                .submitAnswerList(submitWordList)
                .member(member)
                .score((correctCount * 100) / answerList.size())
                .build();

        return new ListeningSubmitDto(listeningSubmitRepository.save(listeningSubmit));

    }

    private double calCorrectRate(QuizSubmitRequest submitRequest, QuizList quizList) {
        List<Integer> submitAnswers = submitRequest.getAnswers();
        List<Quiz> quizzes = quizList.getQuizzes();
        int totalCorrectCount = 0;
        for(int i = 0; i < 4; i++) {
            Quiz quiz = quizzes.get(i);
            quiz.setSubmitCount(quiz.getSubmitCount() + 1);
            if(quiz.getAnswer() == submitAnswers.get(i)) {
                quiz.setCorrectCount(quiz.getCorrectCount() + 1);
                totalCorrectCount++;
            }
        }

         return totalCorrectCount * 100 / quizList.getQuizzes().size();
    }
    @Transactional
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest quizRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        Optional<QuizList> optionalQuizList = quizListRepository.findByMusicIdAndQuizTypeAndLevel(quizRequest.getMusicId(), quizRequest.getQuizType(), member.getLevel());
        if(optionalQuizList.isEmpty()) {
            return CompletableFuture.completedFuture(createQuizList(quizRequest, member));
        } else {
            QuizList quizList = optionalQuizList.get();
            QuizListDto quizListDto = new QuizListDto(quizList);
            return CompletableFuture.completedFuture(quizListDto);
        }
    }
    public QuizListDto createQuizList(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        if(quizType.equals(QuizType.GRAMMAR)) {
            return createGrammarQuiz(quizRequest, member);
        } else if (quizType.equals(QuizType.READING)) {
            return createReadingQuiz(quizRequest, member);
        } else if (quizType.equals(QuizType.VOCABULARY)) {
            return createVocaQuiz(quizRequest, member);
        } else throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_TYPE);
    }

    @Transactional
    public CompletableFuture<ListeningQuizDto> getListeningQuiz(QuizRequest quizRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        Optional<ListeningQuiz> optionalListeningQuiz = listeningQuizRepository.findByMusicIdAndLevel(quizRequest.getMusicId(), member.getLevel());
        if(optionalListeningQuiz.isEmpty()) {
            return CompletableFuture.completedFuture(createListeningQuiz(quizRequest, member));
        } else {
            return CompletableFuture.completedFuture(new ListeningQuizDto(optionalListeningQuiz.get()));
        }
    }

    private QuizListDto createReadingQuiz(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        ChatQuestion question = ChatQuestion.builder()
                .text(quizRequest.getLyric())
                .totalProblem(5)
                .lang(member.getLangType().getIso639Value())
                .level(member.getLevel().getValue())
                .build();
        ChatRequest readingRequest = ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(question.toString() + "\n" + promptDetailUtil.get(member, quizRequest))
                .build();

        int retries = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                ChatGPTResponse chatGPTResponse = openAIService.requestGPT(readingRequest);
                String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                JsonArray probListJsonArray = jsonObject.getAsJsonArray("probList");
                Type listType = new TypeToken<List<Quiz>>(){}.getType();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                        .setLenient()
                        .create();
                List<Quiz> quizzes = gson.fromJson(probListJsonArray, listType);

                QuizList quizList = QuizList.builder()
                        .quizzes(quizzes)
                        .createdTime(LocalDateTime.now())
                        .quizType(quizType)
                        .level(member.getLevel())
                        .musicId(quizRequest.getMusicId())
                        .build();

                for (Quiz quiz : quizzes) {
                    quiz.setQuizList(quizList);
                }
                List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes.stream().toList());
                QuizList savedQuizList = quizListRepository.save(quizList);
                savedQuizList.setQuizzes(savedQuizzes);

                success = true;
                return new QuizListDto(savedQuizList);
            } catch (Exception e) {
                retries++;
                e.printStackTrace();
                if (retries >= maxRetries) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    private QuizListDto createGrammarQuiz(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        ChatQuestion question = ChatQuestion.builder()
                .text(quizRequest.getLyric())
                .lang(member.getLangType().getIso639Value())
                .level(member.getLevel().getValue())
                .totalProblem(5)
                .build();

        ChatRequest grammarRequest = ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(question.toString() + "\n" + promptDetailUtil.get(member, quizRequest))
                .build();
        int retries = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                ChatGPTResponse chatGPTResponse = openAIService.requestFinetuningModel(grammarRequest);
                System.out.println(chatGPTResponse);
                String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                JsonArray probListJsonArray = jsonObject.getAsJsonArray("probList");
                Type listType = new TypeToken<List<Quiz>>(){}.getType();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                        .setLenient()
                        .create();
                List<Quiz> quizzes = gson.fromJson(probListJsonArray, listType);

                QuizList quizList = QuizList.builder()
                        .quizzes(quizzes)
                        .createdTime(LocalDateTime.now())
                        .quizType(quizType)
                        .level(member.getLevel())
                        .musicId(quizRequest.getMusicId())
                        .build();

                for (Quiz quiz : quizzes) {
                    quiz.setQuizList(quizList);
                }
                List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes.stream().toList());
                QuizList savedQuizList = quizListRepository.save(quizList);
                savedQuizList.setQuizzes(savedQuizzes);

                success = true;
                return new QuizListDto(savedQuizList);
            } catch (Exception e) {
                retries++;
                e.printStackTrace();
                if (retries >= maxRetries) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

    }

    private ListeningQuizDto createListeningQuiz(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();

        ChatRequest listeningRequest = ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(addLineNumbers(quizRequest.getLyric()) + "\n" + promptDetailUtil.get(member, quizRequest))
                .build();

        int retries = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                ChatGPTResponse chatGPTResponse = openAIService.requestGPT(listeningRequest);
                String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                JsonArray probListJsonArray = jsonObject.getAsJsonArray("answerList");
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                        .setLenient()
                        .create();

                Type listType = new TypeToken<List<ListeningAnswer>>(){}.getType();
                List<ListeningAnswer> answerContext = gson.fromJson(probListJsonArray, listType);
                ListeningQuiz listeningQuiz = ListeningQuiz.builder()
                        .level(member.getLevel())
                        .musicId(quizRequest.getMusicId())
                        .submitList(new ArrayList<>()).build();


                blankToListeningText(quizRequest, member.getLevel(), answerContext, listeningQuiz);
                listeningQuizRepository.save(listeningQuiz);

                success = true;
                return new ListeningQuizDto(listeningQuiz);
            } catch (Exception e) {
                retries++;
                e.printStackTrace();
                if (retries >= maxRetries) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private QuizListDto createVocaQuiz(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        ChatQuestion question = ChatQuestion.builder()
                .text(quizRequest.getLyric())
                .totalProblem(5)
                .lang(member.getLangType().getIso639Value())
                .level(member.getLevel().getValue())
                .build();

        ChatRequest vocaRequest = ChatRequest.builder()
                .system(getPrompt(quizType, member))
                .userInput(question.toString())
                .build();
        int retries = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                ChatGPTResponse chatGPTResponse = openAIService.requestGPT(vocaRequest);
                String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                JsonArray probListJsonArray = jsonObject.getAsJsonArray("probList");
                Type listType = new TypeToken<List<Quiz>>(){}.getType();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                        .setLenient()
                        .create();
                List<Quiz> quizzes = gson.fromJson(probListJsonArray, listType);

                QuizList quizList = QuizList.builder()
                        .quizzes(quizzes)
                        .createdTime(LocalDateTime.now())
                        .quizType(quizType)
                        .level(member.getLevel())
                        .musicId(quizRequest.getMusicId())
                        .build();

                for (Quiz quiz : quizzes) {
                    quiz.setQuizList(quizList);
                }
                List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes.stream().toList());
                QuizList savedQuizList = quizListRepository.save(quizList);
                savedQuizList.setQuizzes(savedQuizzes);

                success = true;
                return new QuizListDto(savedQuizList);
            } catch (Exception e) {
                retries++;
                e.printStackTrace();
                if (retries >= maxRetries) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }



    private String getPrompt(QuizType quizType, Member member) {

        String filePath;
        if(quizType.equals(QuizType.VOCABULARY)) {
            filePath = "." + File.separator + "prompt" + File.separator + quizType + File.separator + member.getLevel().toString() + ".txt";
        } else {
            filePath = "." + File.separator + "prompt" + File.separator + quizType + ".txt";
        }

        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
        int totalBlankSize;
        if (level.equals(LearningLevel.Beginner)) {
            totalBlankSize = 10;
        } else if (level.equals(LearningLevel.Intermediate)) {
            totalBlankSize = 20;
        } else {
            totalBlankSize = 30;
        }

        Collections.shuffle(answerContext);
        String[] lyricArray = lyric.split("\n");
        StringBuilder modifiedLyrics = new StringBuilder();


        for (int i = 0; i < lyricArray.length; i++) {
            try {
                String line = lyricArray[i];
                String[] tokens = line.split(" ");
                for (ListeningAnswer listeningAnswer : answerContext) {
                    if (listeningAnswer.getLineIndex() == i) {
                        for (int j = 0; j < tokens.length; j++) {
                            if (tokens[j].contains(listeningAnswer.getAnswerWord())) {
                                tokens[j] = "__";
                                answerList.add(listeningAnswer.getAnswerWord());
                                break;
                            }
                        }

                    }
                }

                modifiedLyrics.append(String.join(" ", tokens));
                modifiedLyrics.append("\n");

                if (answerList.size() >= totalBlankSize) {
                    for(int j = i+1; j < lyricArray.length; j++) {
                        modifiedLyrics.append(lyricArray[j] + "\n");
                    }
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }

        listeningQuiz.setBlankedText(modifiedLyrics.toString());
        listeningQuiz.setAnswerList(answerList);
    }

}
