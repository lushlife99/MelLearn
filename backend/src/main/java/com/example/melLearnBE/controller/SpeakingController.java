package com.example.melLearnBE.controller;

import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.request.SpeakingSubmitRequest;
import com.example.melLearnBE.dto.response.AnswerSpeakingDto;
import com.example.melLearnBE.dto.response.openAI.WhisperSegment;
import com.example.melLearnBE.service.SpeakingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem/speaking")
public class SpeakingController {

    private final SpeakingService speakingService;

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnswerSpeakingDto submit(@RequestPart("file") MultipartFile file,
                                    @RequestPart("lyricList") List<LrcLyric> lyricList,
                                    @RequestPart("musicId") String musicId,
                                    HttpServletRequest request) {

        SpeakingSubmitRequest submitRequest = SpeakingSubmitRequest.builder()
                .file(file)
                .lyricList(lyricList)
                .build();
        return speakingService.submit(submitRequest, musicId, request);
    }

}