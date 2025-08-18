package com.mellearn.be.api.feign.openai.dto.request;

import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class UploadFileRequest {
    private File file;
    private String purpose;
}

