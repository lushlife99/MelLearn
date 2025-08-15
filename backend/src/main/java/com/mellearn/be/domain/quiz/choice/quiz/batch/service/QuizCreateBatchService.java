package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizCreateService;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizCreateBatchService {

    private static final String QUIZ_REQUEST_KEY = "quizRequest:*";
    private static final String LISTENING_QUIZ_REQUEST_KEY = "listeningQuizRequest:*";

    private final QuizCreateService quizCreateService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createQuizList() {
        createChoiceQuizList();
        createListeningQuizList();
    }

    private void createChoiceQuizList() {
        Set<String> keys = redisTemplate.keys(QUIZ_REQUEST_KEY);
        List<CompletableFuture<QuizList>> futures = keys.stream()
                .map(key -> CompletableFuture.supplyAsync(() -> {
                    QuizRequest qr = (QuizRequest) redisTemplate.opsForValue().get(key);
                    QuizList quiz = quizCreateService.createChoiceQuiz(qr);
                    redisTemplate.delete(key); // 생성 후 키 삭제
                    System.out.println(quiz);
                    return quiz;
                }))
                .toList();

        List<QuizList> choiceQuizzes = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        quizListRepository.saveAll(choiceQuizzes);
    }

    private void createListeningQuizList() {
        Set<String> keys = redisTemplate.keys(LISTENING_QUIZ_REQUEST_KEY);
        List<CompletableFuture<ListeningQuiz>> futures = keys.stream()
                .map(key -> CompletableFuture.supplyAsync(() -> {
                    QuizRequest qr = (QuizRequest) redisTemplate.opsForValue().get(key);
                    ListeningQuiz quiz = quizCreateService.createListeningQuiz(qr);
                    redisTemplate.delete(key); // 생성 후 키 삭제
                    System.out.println(quiz);
                    return quiz;
                }))
                .toList();

        List<ListeningQuiz> listeningQuizzes = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        listeningQuizRepository.saveAll(listeningQuizzes);
    }

}
