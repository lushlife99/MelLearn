package com.mellearn.be.api.feign.openai.dto.response;

import lombok.Data;

@Data
public class UploadFileResponse {
    private String id;
    private String object;
}
