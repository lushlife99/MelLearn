package com.mellearn.be.domain.quiz.speaking.service;

import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitRequest;

import java.util.concurrent.CompletableFuture;

public interface SpeakingService {

    CompletableFuture<SpeakingSubmitDto> submit(SpeakingSubmitRequest submitRequest,
                                                String musicId, String memberId);
}
