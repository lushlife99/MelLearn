package com.example.melLearnBE.global.auth.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
