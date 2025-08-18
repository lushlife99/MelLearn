package com.mellearn.be.domain.member.entity.role;

import com.mellearn.be.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member_roles")
public class MemberRole {

    @EmbeddedId
    private MemberRoleId id;

    @MapsId("memberId")
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

}