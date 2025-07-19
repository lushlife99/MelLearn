package com.mellearn.be.global.error;

import com.mellearn.be.global.error.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

}
