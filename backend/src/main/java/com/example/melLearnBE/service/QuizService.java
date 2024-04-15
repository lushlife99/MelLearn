package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.ListeningSubmitRequest;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.*;
import com.example.melLearnBE.repository.*;
import com.example.melLearnBE.repository.querydsl.SubmitJpaRepository;
import com.example.melLearnBE.util.PromptDetailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final ListeningQuizRepository listeningQuizRepository;
    private final QuizSubmitRepository quizSubmitRepository;
    private final QuizListRepository quizListRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ListeningSubmitRepository listeningSubmitRepository;
    private final SubmitJpaRepository submitJpaRepository;
    private final QuizCreationService quizCreationService;


    /**
     * 동시성 고려해서 코드 업데이트 해야함.
     * 아직 안했음.
     */

    public Page getSubmitList(QuizType quizType, int pageNo, HttpServletRequest request) {
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


    @Async
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest quizRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        return quizCreationService.fetchOrCreateQuizList(quizRequest, member);
    }

    @Async
    public CompletableFuture<ListeningQuizDto> getListeningQuiz(QuizRequest quizRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        return quizCreationService.fetchOrCreateListeningQuizList(quizRequest, member);
    }

    @Transactional
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













}
