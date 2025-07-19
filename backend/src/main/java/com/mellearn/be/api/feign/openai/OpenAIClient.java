package com.mellearn.be.api.feign.openai;

import com.mellearn.be.api.feign.openai.dto.request.ChatGPTRequest;
import com.mellearn.be.api.feign.openai.dto.request.WhisperTranscriptionRequest;
import com.mellearn.be.api.feign.openai.dto.response.ChatGPTResponse;
import com.mellearn.be.api.feign.openai.dto.response.WhisperTranscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "openai-service",
        url = "${openai-service.urls.base-url}",
        configuration = OpenAIClientConfig.class
)
public interface OpenAIClient {

    @PostMapping(value = "${openai-service.urls.create-transcription-url}", headers = {"Content-Type=multipart/form-data"})
    WhisperTranscriptionResponse createTranscription(@ModelAttribute WhisperTranscriptionRequest whisperTranscriptionRequest);

    @PostMapping(value = "${openai-service.urls.chat-url}", headers = {"Content-Type=application/json"})
    ChatGPTResponse chat(@RequestBody ChatGPTRequest chatGPTRequest);
    
}
