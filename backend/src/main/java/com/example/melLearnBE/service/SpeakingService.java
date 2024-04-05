package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.request.openAI.SpeakingSubmitRequest;
import com.example.melLearnBE.repository.AnswerSpeakingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingService {

    private final AnswerSpeakingRepository answerSpeakingRepository;

    public void submit(SpeakingSubmitRequest submitRequest) {
        String lrcLyric = submitRequest.getLrcLyric();
        MultipartFile file = submitRequest.getFile();

    }
}
