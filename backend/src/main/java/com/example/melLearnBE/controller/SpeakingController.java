package com.example.melLearnBE.controller;

import com.example.melLearnBE.dto.model.RankingDto;
import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.request.SpeakingSubmitRequest;
import com.example.melLearnBE.dto.model.SpeakingSubmitDto;
import com.example.melLearnBE.service.RankingService;
import com.example.melLearnBE.service.SpeakingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem/speaking")
public class SpeakingController {

    private final SpeakingService speakingService;
    private final RankingService rankingService;

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SpeakingSubmitDto submit(@RequestPart("file") MultipartFile file,
                                    @RequestPart("lyricList") List<LrcLyric> lyricList,
                                    @RequestPart("musicId") String musicId,
                                    HttpServletRequest request) {

        SpeakingSubmitRequest submitRequest = SpeakingSubmitRequest.builder()
                .file(file)
                .lyricList(lyricList)
                .build();

        SpeakingSubmitDto submit = speakingService.submit(submitRequest, musicId, request);
        rankingService.updateRanking(musicId, request);
        return submit;
    }

    @GetMapping("/ranking")
    public RankingDto getRanking(@RequestParam String musicId) {
        return rankingService.getRanking(musicId);
    }

}