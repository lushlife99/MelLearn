package com.mellearn.be.domain.member.dto;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.LearningLevel;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String memberId;
    private String name;
    private int levelPoint;
    private LearningLevel level;
    private String langType;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.memberId = member.getMemberId();
        this.name = member.getName();
        this.level = member.getLevel();
        this.langType = member.getLangType().getIso639Value();
        this.levelPoint = member.getLevelPoint();
    }
}
