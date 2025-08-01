package com.mellearn.be.domain.support.controller;


import com.mellearn.be.domain.music.dto.LrcLyric;
import com.mellearn.be.domain.music.dto.MusicDto;
import com.mellearn.be.domain.support.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Support")
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/language")
    @Operation(summary = "지원하는 학습 언어 조회", description = "서버에서 지원할 수 있는 ISO-639-1 포맷의 언어 리스트 반환")
    public List<String> getSupportLang() {
        return supportService.getSupportLang();
    }

    @PostMapping("/quiz/category/{musicId}")
    @Operation(summary = "특정 노래가 지원하는 문제 카테고리 조회", description = "서버에서 지원하는 5가지 카테고리의 문제 유형의 지원을 boolean 형식으로 리턴")
    public MusicDto getSupportQuizCategory(@RequestBody List<LrcLyric> lyric, @PathVariable String musicId, Authentication authentication) {
        return supportService.getSupportQuizCategory(musicId, lyric, authentication.getName());
    }
}
