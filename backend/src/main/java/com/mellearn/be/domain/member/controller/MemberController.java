package com.mellearn.be.domain.member.controller;

import com.mellearn.be.domain.member.dto.MemberDto;
import com.mellearn.be.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Tag(name = "Member")
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    @Operation(summary = "멤버 정보 조회", description = "멤버 정보 조회")
    public MemberDto getMemberInfo(Principal principal) {
        return memberService.getMemberProfile(principal.getName());
    }

    @PostMapping("/spotifyAccount")
    @Operation(summary = "스포티파이 계정 id 등록", description = "스포티파이 계정 id 등록")
    public ResponseEntity updateSpotifyId(@RequestParam String accountId, Principal principal) {
        memberService.updateSpotifyAccount(accountId, principal.getName());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/info")
    @Operation(summary = "멤버 정보 변경", description = "비밀번호, userId를 제외한 정보를 변경")
    public MemberDto updateMemberInfo(@RequestBody MemberDto memberDto, Principal principal) {
        System.out.println(memberDto);
        return memberService.updateMemberProfile(memberDto, principal.getName());
    }
}
