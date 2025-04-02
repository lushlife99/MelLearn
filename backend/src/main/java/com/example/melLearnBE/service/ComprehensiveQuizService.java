package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.*;
import com.example.melLearnBE.dto.request.*;
import com.example.melLearnBE.dto.response.ComprehensiveQuizDto;
import com.example.melLearnBE.dto.response.ComprehensiveQuizSubmitDto;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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

    public ComprehensiveQuizSubmitDto submit(ComprehensiveQuizSubmitRequest quizSubmitRequest, MultipartFile speakingSubmitFile, String memberId) throws ExecutionException, InterruptedException {
        try {
            Member member = findMember(memberId);

            // 모든 비동기 작업을 병렬로 실행
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
