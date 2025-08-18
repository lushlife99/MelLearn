package com.mellearn.be.api.feign.openai.service;

import com.mellearn.be.api.feign.openai.client.OpenAIClient;
import com.mellearn.be.api.feign.openai.config.OpenAIClientConfig;
import com.mellearn.be.api.feign.openai.dto.request.*;
import com.mellearn.be.api.feign.openai.dto.response.BatchResponse;
import com.mellearn.be.api.feign.openai.dto.response.ChatGPTResponse;
import com.mellearn.be.api.feign.openai.dto.response.WhisperTranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * OpenAIService - OpenAI API 호출
 *
 * 25.07.20
 * Spring AI 도입으로 더이상 사용하지 않음
 */

@Service
@RequiredArgsConstructor
public class OpenAIService {


    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;
    private final static String ROLE_USER = "user";
    private final static String ROLE_SYSTEM = "system";
    public WhisperTranscriptionResponse createTranscription(File audioFile, String langCode) {

        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model(openAIClientConfig.getAudioModel())
                .file(audioFile)
                .timestamp_granularities(Collections.singletonList("segment"))
                .language(langCode)
                .response_format("verbose_json")
                .build();
        WhisperTranscriptionResponse transcription = openAIClient.createTranscription(whisperTranscriptionRequest);

        return transcription;
    }

    public ChatGPTResponse requestFinetuningModel(ChatRequest chatRequest) {

        Message systemMessage = Message.builder()
                .role(ROLE_SYSTEM)
                .content(chatRequest.getSystem())
                .build();

        List<Message> userMessages = new ArrayList<>();
        Message userMessage = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getUserInput())
                .build();
        userMessages.add(userMessage);


        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getFineTuningModel())
                .max_tokens(2048)
                .messages(List.of(systemMessage, userMessage))
                .build();

        return openAIClient.chat(chatGPTRequest);
    }

    public ChatGPTResponse requestGPT(ChatRequest chatRequest){

        Message systemMessage = Message.builder()
                .role(ROLE_SYSTEM)
                .content(chatRequest.getSystem())
                .build();

        Message userMessage = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getUserInput())
                .build();

        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getModel())
                .max_tokens(4095)
                .messages(List.of(systemMessage, userMessage))
                .build();

        return openAIClient.chat(chatGPTRequest);
    }

    public ChatGPTResponse requestGPT4(ChatRequest chatRequest){

        Message systemMessage = Message.builder()
                .role(ROLE_SYSTEM)
                .content(chatRequest.getSystem())
                .build();

        Message userMessage = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getUserInput())
                .build();


        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getGpt4Model())
                .max_tokens(4095)
                .messages(List.of(systemMessage, userMessage))
                .build();

        return openAIClient.chat(chatGPTRequest);
    }

    public BatchResponse createBatch(BatchRequest batchRequest) {
        return openAIClient.createBatch(batchRequest);
    }

    public BatchResponse retrieveBatch(String batchId) {
        return openAIClient.retrieveBatch(batchId);
    }

    public BatchResponse cancelBatch(String batchId) {
        return openAIClient.cancelBatch(batchId);
    }

    public BatchResponse listBatches() {
        return openAIClient.listBatches();
    }

    public String getBatchResult(String fileId) {
        byte[] fileContent = openAIClient.downloadFile(fileId);
        return new String(fileContent, StandardCharsets.UTF_8);
    }
}
