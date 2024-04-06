package com.example.melLearnBE.controller;

import com.example.melLearnBE.dto.request.SpeakingSubmitRequest;
import com.example.melLearnBE.service.SpeakingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem/speaking")
public class SpeakingController {

    private SpeakingService speakingService;

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void submit(@ModelAttribute SpeakingSubmitRequest speakingSubmitRequest, HttpServletRequest request) {
        speakingService.submit(speakingSubmitRequest, request);
    }
}