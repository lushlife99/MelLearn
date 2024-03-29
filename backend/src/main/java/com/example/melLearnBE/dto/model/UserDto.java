package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.model.User;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String userId;
    private String username;
    private int level;
    private Language langType;

    public UserDto(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.level = user.getLevel();
        this.langType = user.getLangType();
    }
}
