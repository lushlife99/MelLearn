package com.example.melLearnBE.domain.quiz.speaking.controller;

import com.example.melLearnBE.domain.quiz.speaking.service.SpeakingService;
import com.example.melLearnBE.domain.quiz.ranking.dto.RankingDto;
import com.example.melLearnBE.domain.music.dto.LrcLyric;
import com.example.melLearnBE.domain.quiz.speaking.dto.SpeakingSubmitRequest;
import com.example.melLearnBE.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.example.melLearnBE.domain.quiz.ranking.service.RankingService;
import com.example.melLearnBE.global.auth.jwt.service.JwtTokenProvider;
import com.example.melLearnBE.global.error.CustomException;
import com.example.melLearnBE.global.error.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Speaking 채점", description = "Speaking 채점")
    public SpeakingSubmitDto submit(@RequestPart("file") MultipartFile file,
                                    @RequestPart("lyricList") List<LrcLyric> lyricList,
                                    @RequestPart("musicId") String musicId,
                                    HttpServletRequest request) throws ExecutionException, InterruptedException {
        String memberId = extractMemberId(request);
        
        SpeakingSubmitRequest submitRequest = SpeakingSubmitRequest.builder()
                .file(file)
                .lyricList(lyricList)
                .build();

        SpeakingSubmitDto submit = speakingService.submit(submitRequest, musicId, memberId).get();
        rankingService.updateRanking(musicId, memberId);
        return submit;
    }

    @GetMapping("/ranking")
    @Operation(summary = "Speaking 랭킹 조회", description = "Speaking 랭킹 조회")
    public RankingDto getRanking(@RequestParam String musicId) {
        return rankingService.getRanking(musicId);
    }

    private String extractMemberId(HttpServletRequest request) {
        return jwtTokenProvider.getMember(request)
                .map(member -> member.getMemberId())
                .orElseThrow(() -> {
                    log.error("Failed to extract member ID from request");
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }
}