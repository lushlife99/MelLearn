package com.mellearn.be.domain.member.dto.request;

import com.mellearn.be.domain.member.enums.Language;
import lombok.Getter;

@Getter
public class MemberRequest {
    private String memberId;
    private String name;
    private String password;
    private Language langType;

} 