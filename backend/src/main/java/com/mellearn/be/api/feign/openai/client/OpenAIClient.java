package com.mellearn.be.api.feign.openai.client;

import com.mellearn.be.api.feign.openai.config.OpenAIClientConfig;
import com.mellearn.be.api.feign.openai.dto.request.BatchRequest;
import com.mellearn.be.api.feign.openai.dto.request.ChatGPTRequest;
import com.mellearn.be.api.feign.openai.dto.request.UploadFileRequest;
import com.mellearn.be.api.feign.openai.dto.request.WhisperTranscriptionRequest;
import com.mellearn.be.api.feign.openai.dto.response.BatchResponse;
import com.mellearn.be.api.feign.openai.dto.response.ChatGPTResponse;
import com.mellearn.be.api.feign.openai.dto.response.UploadFileResponse;
import com.mellearn.be.api.feign.openai.dto.response.WhisperTranscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "openai-service",
        url = "${openai-service.urls.base-url}", // Base URL: https://api.openai.com/v1
        configuration = OpenAIClientConfig.class
)
public interface OpenAIClient {

    // === Whisper Transcription ===
    @PostMapping(
            value = "${openai-service.urls.create-transcription-url}",
            headers = {"Content-Type=multipart/form-data"}
    )
    WhisperTranscriptionResponse createTranscription(@ModelAttribute WhisperTranscriptionRequest whisperTranscriptionRequest);

    // === ChatGPT ===
    @PostMapping(
            value = "${openai-service.urls.chat-url}",
            headers = {"Content-Type=application/json"}
    )
    ChatGPTResponse chat(@RequestBody ChatGPTRequest chatGPTRequest);

    // === Batch API ===
    @PostMapping(
            value = "${openai-service.urls.batch-url}",
            headers = {"Content-Type=application/json"}
    )
    BatchResponse createBatch(@RequestBody BatchRequest batchRequest);

    @GetMapping("${openai-service.urls.batch-url}/{batch_id}")
    BatchResponse retrieveBatch(@PathVariable("batch_id") String batchId);

    @DeleteMapping("${openai-service.urls.batch-url}/{batch_id}")
    BatchResponse cancelBatch(@PathVariable("batch_id") String batchId);

    @GetMapping("${openai-service.urls.batch-url}")
    BatchResponse listBatches();

    // === File API ===
    @GetMapping("${openai-service.urls.file-url}/{file_id}")
    Map<String, Object> retrieveFile(@PathVariable("file_id") String fileId);

    @GetMapping("${openai-service.urls.file-url}/{file_id}/content")
    byte[] downloadFile(@PathVariable("file_id") String fileId);

    // Upload File (multipart/form-data) – URL 변경: Base URL과 합쳐지지 않도록 ${...} 사용
    @PostMapping(
            value = "${openai-service.urls.file-url}",
            headers = {"Content-Type=multipart/form-data"}
    )
    UploadFileResponse uploadFile(@ModelAttribute UploadFileRequest request);
}
