package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.model.Member;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String memberId;
    private String username;
    private int level;
    private Language langType;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.memberId = member.getMemberId();
        this.username = member.getUsername();
        this.level = member.getLevel();
        this.langType = member.getLangType();
    }
}
