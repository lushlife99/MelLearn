package com.example.melLearnBE.dto.request;

import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.model.Member;
import lombok.Getter;

@Getter
public class MemberRequest {
    private String memberId;
    private String name;
    private String password;
    private Language langType;

} 