package com.example.melLearnBE.service;

import com.example.melLearnBE.adapter.LocalDateTimeTypeAdapter;
import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.dto.request.openAI.ChatQuestion;
import com.example.melLearnBE.dto.request.openAI.ChatRequest;
import com.example.melLearnBE.dto.response.openAI.ChatGPTResponse;
import com.example.melLearnBE.dto.response.openAI.GrammarQuiz;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.model.Quiz;
import com.example.melLearnBE.model.QuizList;
import com.example.melLearnBE.model.QuizSubmit;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.QuizRepository;
import com.example.melLearnBE.repository.QuizSubmitRepository;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmitRepository quizSubmitRepository;
    private final QuizListRepository quizListRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OpenAIService openAIService;


    /**
     * 동시성 고려해서 코드 업데이트 해야함.
     * 아직 안했음.
     */

    public QuizSubmitDto submit(QuizSubmitRequest submitRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        QuizList quizList = quizListRepository.findByMusicIdAndQuizTypeAndLevel(submitRequest.getMusicId(), submitRequest.getQuizType(), member.getLevel().getValue())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        calCorrectRate(submitRequest, quizList);

        QuizSubmit quizSubmit = QuizSubmit.builder()
                .quizList(quizList)
                .submitAnswerList(submitRequest.getAnswers())
                .member(member)
                .build();

        return new QuizSubmitDto(quizSubmitRepository.save(quizSubmit));
    }

    private void calCorrectRate(QuizSubmitRequest submitRequest, QuizList quizList) {
        List<Integer> submitAnswers = submitRequest.getAnswers();
        List<Quiz> quizzes = quizList.getQuizzes();
        for(int i = 0; i < 4; i++) {
            Quiz quiz = quizzes.get(i);
            quiz.setSubmitCount(quiz.getSubmitCount() + 1);
            if(quiz.getAnswer() == submitAnswers.get(i)) {
                quiz.setCorrectCount(quiz.getCorrectCount() + 1);
            }
        }
    }
    @Transactional
    public QuizListDto getQuizList(QuizRequest quizRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        Optional<QuizList> optionalQuizList = quizListRepository.findByMusicIdAndQuizTypeAndLevel(quizRequest.getMusicId(), quizRequest.getQuizType(), member.getLevel().getValue());
        if(optionalQuizList.isEmpty()) {
            return createQuizList(quizRequest, member);
        } else {
            QuizList quizList = optionalQuizList.get();
            QuizListDto quizListDto = new QuizListDto(quizList);
            return quizListDto;
        }
    }
    public QuizListDto createQuizList(QuizRequest quizRequest, Member member) {
        QuizType quizType = quizRequest.getQuizType();
        if(quizType.equals(QuizType.GRAMMAR)) {
            return createGrammarQuiz(quizRequest, member);
        } else if (quizType.equals(QuizType.READING)) {
            return createVocaQuiz(quizRequest, member);
        } else if (quizType.equals(QuizType.LISTENING)) {

            return null;
        } else if (quizType.equals(QuizType.VOCABULARY)) {
            return createVocaQuiz(quizRequest, member);
        } else throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_TYPE);

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
                .system(getPrompt(quizType))
                .question(question.toString())
                .build();
        int retries = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                ChatGPTResponse chatGPTResponse = openAIService.requestFinetuningModel(grammarRequest);
                String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                JsonArray probListJsonArray = jsonObject.getAsJsonArray("problist");
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                        .setLenient()
                        .create();

                Type listType = new TypeToken<List<GrammarQuiz>>(){}.getType();
                List<GrammarQuiz> grammarQuizs = gson.fromJson(probListJsonArray, listType);
                List<Quiz> quizzes = new ArrayList<>();
                for (GrammarQuiz grammarQuiz : grammarQuizs) {
                    quizzes.add(Quiz.builder()
                            .answer(grammarQuiz.getAnswer())
                            .comment(grammarQuiz.getComment())
                            .question(grammarQuiz.getQuestion())
                            .optionList(grammarQuiz.getSelectionList())
                            .build());
                }

                QuizList quizList = QuizList.builder()
                        .quizzes(quizzes)
                        .quizType(quizType)
                        .level(member.getLevel().getValue())
                        .createdTime(LocalDateTime.now())
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
        ChatQuestion question = ChatQuestion.builder()
                .text(quizRequest.getLyric())
                .level(member.getLevel().getValue())
                .build();

        ChatRequest vocaRequest = ChatRequest.builder()
                .system(getPrompt(quizType))
                .question(question.toString())
                .build();

        /**
         * 내일 여기부터 시작
         */
        return null;
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
                .system(getPrompt(quizType))
                .question(question.toString())
                .build();

        System.out.println(vocaRequest);
        System.out.println("vocaRequest.getQuestion() = " + vocaRequest.getQuestion());
        int retries = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                ChatGPTResponse chatGPTResponse = openAIService.requestGPT(vocaRequest);
                System.out.println(chatGPTResponse.getChoices().get(0).getMessage().getContent());
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
                        .level(member.getLevel().getValue())
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



    private String getPrompt(QuizType quizType) {
        String filePath = "." + File.separator + "prompt" + File.separator + quizType.toString() + ".txt";

        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
