package com.mellearn.be.domain.quiz.comprehensive.service;

import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizAnswerDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitRequest;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitRequest;
import com.mellearn.be.domain.quiz.speaking.service.SpeakingService;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprehensiveQuizService {

    private final QuizService quizService;
    private final SpeakingService speakingService;
    private final MemberRepository memberRepository;

    /**
     * Speaking 제외한 모의고사 제출
     */
    public ComprehensiveQuizSubmitDto submit(ComprehensiveQuizSubmitRequest quizSubmitRequest, String memberId) throws ExecutionException, InterruptedException {
        try {
            Member member = findMember(memberId);

            CompletableFuture<QuizSubmitDto> grammarSubmit = quizService.submit(
                    QuizSubmitRequest.builder()
                            .quizType(QuizType.GRAMMAR)
                            .answers(quizSubmitRequest.getGrammarSubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizSubmitDto> vocaSubmit = quizService.submit(
                    QuizSubmitRequest.builder()
                            .quizType(QuizType.VOCABULARY)
                            .answers(quizSubmitRequest.getVocabularySubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizSubmitDto> readingSubmit = quizService.submit(
                    QuizSubmitRequest.builder()
                            .quizType(QuizType.READING)
                            .answers(quizSubmitRequest.getReadingSubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<ListeningSubmitDto> listeningSubmit = quizService.listeningSubmit(
                    ListeningSubmitRequest.builder()
                            .submitWordList(quizSubmitRequest.getListeningSubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            // 모든 작업이 완료될 때까지 대기
            CompletableFuture.allOf(grammarSubmit, vocaSubmit, readingSubmit, listeningSubmit)
                    .exceptionally(throwable -> {
                        log.error("Error in comprehensive submit: {}", throwable.getMessage());
                        throw new CompletionException(throwable);
                    })
                    .join();

            // 결과 수집
            QuizSubmitDto grammarSubmitDto = grammarSubmit.get();
            QuizSubmitDto vocaSubmitDto = vocaSubmit.get();
            QuizSubmitDto readingSubmitDto = readingSubmit.get();
            ListeningSubmitDto listeningSubmitDto = listeningSubmit.get();

            return new ComprehensiveQuizSubmitDto(
                    quizSubmitRequest.getMusicId(), member.getLevel(),
                    ComprehensiveQuizAnswerDto.builder()
                            .vocabularySubmit(vocaSubmitDto)
                            .readingSubmit(readingSubmitDto)
                            .grammarSubmit(grammarSubmitDto)
                            .listeningSubmit(listeningSubmitDto)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error in comprehensive submit: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Speaking 포함한 모의고사 제출
     */

    public ComprehensiveQuizSubmitDto submit(ComprehensiveQuizSubmitRequest quizSubmitRequest, MultipartFile speakingSubmitFile, String memberId) throws ExecutionException, InterruptedException {
        try {
            Member member = findMember(memberId);

            CompletableFuture<SpeakingSubmitDto> speakingSubmit = speakingService.submit(
                    SpeakingSubmitRequest.builder()
                            .file(speakingSubmitFile)
                            .lyricList(quizSubmitRequest.getLrcLyricList())
                            .build(), quizSubmitRequest.getMusicId(), memberId);

            CompletableFuture<QuizSubmitDto> grammarSubmit = quizService.submit(
                    QuizSubmitRequest.builder()
                            .quizType(QuizType.GRAMMAR)
                            .answers(quizSubmitRequest.getGrammarSubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizSubmitDto> vocaSubmit = quizService.submit(
                    QuizSubmitRequest.builder()
                            .quizType(QuizType.VOCABULARY)
                            .answers(quizSubmitRequest.getVocabularySubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizSubmitDto> readingSubmit = quizService.submit(
                    QuizSubmitRequest.builder()
                            .quizType(QuizType.READING)
                            .answers(quizSubmitRequest.getReadingSubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<ListeningSubmitDto> listeningSubmit = quizService.listeningSubmit(
                    ListeningSubmitRequest.builder()
                            .submitWordList(quizSubmitRequest.getListeningSubmit())
                            .musicId(quizSubmitRequest.getMusicId())
                            .build(), memberId);

            // 모든 작업이 완료될 때까지 대기
            CompletableFuture.allOf(speakingSubmit, grammarSubmit, vocaSubmit, readingSubmit, listeningSubmit)
                    .exceptionally(throwable -> {
                        log.error("Error in comprehensive submit: {}", throwable.getMessage());
                        throw new CompletionException(throwable);
                    })
                    .join();

            // 결과 수집
            SpeakingSubmitDto speakingSubmitDto = speakingSubmit.get();
            QuizSubmitDto grammarSubmitDto = grammarSubmit.get();
            QuizSubmitDto vocaSubmitDto = vocaSubmit.get();
            QuizSubmitDto readingSubmitDto = readingSubmit.get();
            ListeningSubmitDto listeningSubmitDto = listeningSubmit.get();

            return new ComprehensiveQuizSubmitDto(
                    quizSubmitRequest.getMusicId(), member.getLevel(),
                    ComprehensiveQuizAnswerDto.builder()
                            .speakingSubmit(speakingSubmitDto)
                            .vocabularySubmit(vocaSubmitDto)
                            .readingSubmit(readingSubmitDto)
                            .grammarSubmit(grammarSubmitDto)
                            .listeningSubmit(listeningSubmitDto)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error in comprehensive submit: {}", e.getMessage());
            throw e;
        }
    }

    public ComprehensiveQuizDto get(QuizRequest quizRequest, String memberId) throws InterruptedException, ExecutionException {
        try {
            // 모든 비동기 작업을 병렬로 실행
            CompletableFuture<ListeningQuizDto> listeningQuiz = quizService.getListeningQuiz(
                    QuizRequest.builder()
                            .quizType(QuizType.LISTENING)
                            .lyric(quizRequest.getLyric())
                            .musicId(quizRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizListDto> readingQuiz = quizService.getQuizList(
                    QuizRequest.builder()
                            .quizType(QuizType.READING)
                            .lyric(quizRequest.getLyric())
                            .musicId(quizRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizListDto> vocaQuiz = quizService.getQuizList(
                    QuizRequest.builder()
                            .quizType(QuizType.VOCABULARY)
                            .lyric(quizRequest.getLyric())
                            .musicId(quizRequest.getMusicId())
                            .build(), memberId);

            CompletableFuture<QuizListDto> grammarQuiz = quizService.getQuizList(
                    QuizRequest.builder()
                            .quizType(QuizType.GRAMMAR)
                            .lyric(quizRequest.getLyric())
                            .musicId(quizRequest.getMusicId())
                            .build(), memberId);

            // 모든 작업이 완료될 때까지 대기
            CompletableFuture.allOf(listeningQuiz, readingQuiz, vocaQuiz, grammarQuiz)
                    .exceptionally(throwable -> {
                        log.error("Error in comprehensive get: {}", throwable.getMessage());
                        throw new CompletionException(throwable);
                    })
                    .join();

            // 결과 수집
            ListeningQuizDto listeningResult = listeningQuiz.get();
            QuizListDto readingResult = readingQuiz.get();
            QuizListDto vocaResult = vocaQuiz.get();
            QuizListDto grammarResult = grammarQuiz.get();

            return new ComprehensiveQuizDto(grammarResult, vocaResult, readingResult, listeningResult);
        } catch (Exception e) {
            log.error("Error in comprehensive get: {}", e.getMessage());
            throw e;
        }
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found with id: {}", memberId);
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }
}
