package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.request.openAI.ChatGPTRequest;
import com.example.melLearnBE.dto.request.openAI.ChatRequest;
import com.example.melLearnBE.dto.request.openAI.Message;
import com.example.melLearnBE.dto.request.openAI.WhisperTranscriptionRequest;
import com.example.melLearnBE.dto.response.openAI.ChatGPTResponse;
import com.example.melLearnBE.dto.response.openAI.WhisperTranscriptionResponse;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClient;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

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

        Message userMessage = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getQuestion())
                .build();

        Message systemMessage = Message.builder()
                .role(ROLE_SYSTEM)
                .content(chatRequest.getSystem())
                .build();

        List<Message> messages = List.of(systemMessage, userMessage);
        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getFineTuningModel())
                .max_tokens(2048)
                .messages(messages)
                .build();
        return openAIClient.chat(chatGPTRequest);
    }

    public ChatGPTResponse requestGPT(ChatRequest chatRequest){
        Message userMessage = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getQuestion())
                .build();

        Message systemMessage = Message.builder()
                .role(ROLE_SYSTEM)
                .content(chatRequest.getSystem())
                .build();

        List<Message> messages = List.of(systemMessage, userMessage);
        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getModel())
                .max_tokens(4095)
                .messages(messages)
                .build();
        return openAIClient.chat(chatGPTRequest);
    }
}
