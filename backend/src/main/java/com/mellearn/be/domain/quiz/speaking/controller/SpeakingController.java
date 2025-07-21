package com.mellearn.be.domain.quiz.speaking.controller;

import com.mellearn.be.domain.music.dto.LrcLyric;
import com.mellearn.be.domain.quiz.ranking.dto.RankingDto;
import com.mellearn.be.domain.quiz.ranking.service.RankingService;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitRequest;
import com.mellearn.be.domain.quiz.speaking.service.SpeakingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Speaking")
@RequestMapping("/api/problem/speaking")
@Slf4j
public class SpeakingController {

    private final SpeakingService speakingService;
    private final RankingService rankingService;

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Speaking 채점", description = "Speaking 채점")
    public SpeakingSubmitDto submit(@RequestPart("file") MultipartFile file,
                                    @RequestPart("lyricList") List<LrcLyric> lyricList,
                                    @RequestPart("musicId") String musicId,
                                    Principal principal) throws ExecutionException, InterruptedException {
        
        SpeakingSubmitRequest submitRequest = SpeakingSubmitRequest.builder()
                .file(file)
                .lyricList(lyricList)
                .build();

        SpeakingSubmitDto submit = speakingService.submit(submitRequest, musicId, principal.getName()).get();
        rankingService.updateRanking(musicId, principal.getName());
        return submit;
    }

    @GetMapping("/ranking")
    @Operation(summary = "Speaking 랭킹 조회", description = "Speaking 랭킹 조회")
    public RankingDto getRanking(@RequestParam String musicId) {
        return rankingService.getRanking(musicId);
    }

}