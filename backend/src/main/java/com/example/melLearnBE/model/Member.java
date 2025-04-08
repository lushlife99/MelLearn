package com.example.melLearnBE.model;

import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.enums.LearningLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberId;

    private String name;

    private String password;

    private int levelPoint;

    @Enumerated(EnumType.STRING)
    private LearningLevel level;

    @Enumerated(EnumType.STRING)
    private Language langType;

    private String spotifyAccountId;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<WordList> wordLists = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<SpeakingSubmit> speakingSubmitList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<QuizSubmit> quizSubmitList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<ListeningSubmit> listeningSubmitList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    private LocalDateTime createdTime;

    @Builder
    public Member(String memberId, String name, String password, Language langType, LearningLevel level) {
        this.memberId = memberId;
        this.name = name;
        this.password = password;
        this.langType = langType;
        this.level = level;
        this.levelPoint = 0;
        this.createdTime = LocalDateTime.now();
    }

    public static Member create(String memberId, String password, String name, LearningLevel level, Language langType, List<String> roles) {
        Member member = Member.builder()
                .memberId(memberId)
                .name(name)
                .password(password)
                .langType(langType)
                .level(level)
                .build();
        member.roles.addAll(roles);
        return member;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public void updateProfile(String name, LearningLevel level, Language langType) {
        this.name = name;
        this.level = level;
        this.langType = langType;
    }

    public void updateSpotifyAccount(String accountId) {
        this.spotifyAccountId = accountId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.memberId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
