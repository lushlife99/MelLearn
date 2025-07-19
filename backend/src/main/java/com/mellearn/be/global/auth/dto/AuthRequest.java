package com.mellearn.be.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    private String memberId;
    private String name;
    private String password;
}
