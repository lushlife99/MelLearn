package com.example.melLearnBE.controller;

import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Support")
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/language")
    @Operation(summary = "지원 언어 조회", description = "서버에서 지원할 수 있는 ISO-639-1 포맷의 언어 리스트 반환")
    public List<String> getSupportLang() {
        return supportService.getSupportLang();
    }
}
