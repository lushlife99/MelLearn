package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mellearn.be.api.feign.openai.client.OpenAIClient;
import com.mellearn.be.api.feign.openai.dto.request.UploadFileRequest;
import com.mellearn.be.api.feign.openai.dto.response.UploadFileResponse;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel.QuizListResponseDto;
import com.mellearn.be.domain.quiz.listening.quiz.dto.response.chatmodel.ListeningQuizResponseDto;
import com.mellearn.be.global.prompt.service.PromptFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchFileService {

    private static final String QUIZ_REQUEST_KEY_PATTERN = "quizRequest:*";
    private static final BeanOutputConverter<QuizListResponseDto> choiceQuizConverter = new BeanOutputConverter<>(QuizListResponseDto.class);
    private static final BeanOutputConverter<ListeningQuizResponseDto> listeningQuizConverter = new BeanOutputConverter<>(ListeningQuizResponseDto.class);

    private final OpenAIClient openAIClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PromptFetchService promptFetchService;
    private final ObjectMapper objectMapper;



    /**
     * Redis에 저장된 QuizRequest를 직접 조회 → JSONL 파일로 변환 → OpenAI 업로드 → fileId 반환
     */
    public String uploadBatchFile() {
        try {
            File tempFile = File.createTempFile("batch-", ".jsonl");

            // Redis에서 quizRequest:* 키 조회
            Set<String> redisKeys = redisTemplate.keys(QUIZ_REQUEST_KEY_PATTERN);
            if (redisKeys == null || redisKeys.isEmpty()) {
                log.info("No quiz requests found.");
                return null;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String redisKey : redisKeys) {
                    QuizRequest req = (QuizRequest) redisTemplate.opsForValue().get(redisKey);
                    if (req == null) continue;

                    // Prompt 생성
                    Prompt prompt = promptFetchService.fetch(req, choiceQuizConverter.getFormat());
                    String systemPrompt = prompt.getSystemMessage().getText();
                    String userPrompt = prompt.getUserMessage().getText();

                    Map<String, Object> body = Map.of(
                            "model", "gpt-4o-mini",
                            "messages", List.of(
                                    Map.of("role", "system", "content", systemPrompt),
                                    Map.of("role", "user", "content", userPrompt)
                            )
                    );

                    Map<String, Object> jsonl = Map.of(
                            "custom_id", redisKey,
                            "method", "POST",
                            "url", "/v1/chat/completions",
                            "body", body
                    );

                    writer.write(objectMapper.writeValueAsString(jsonl));
                    writer.newLine();
                }
            }

            UploadFileRequest request = UploadFileRequest.builder()
                    .file(tempFile)
                    .purpose("batch")
                    .build();

            UploadFileResponse response = openAIClient.uploadFile(request);

            return response.getId();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload batch file", e);
        }
    }

    public String uploadListeningBatchFile() {
        try {
            File tempFile = File.createTempFile("listening-batch-", ".jsonl");

            // Redis에서 listeningQuizRequest:* 키 조회
            Set<String> redisKeys = redisTemplate.keys("listeningQuizRequest:*");
            if (redisKeys == null || redisKeys.isEmpty()) {
                log.info("No listening quiz requests found.");
                return null;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String redisKey : redisKeys) {
                    QuizRequest req = (QuizRequest) redisTemplate.opsForValue().get(redisKey);
                    if (req == null) continue;

                    // Prompt 생성
                    Prompt prompt = promptFetchService.fetch(req, listeningQuizConverter.getFormat());
                    String systemPrompt = prompt.getSystemMessage().getText();
                    String userPrompt = prompt.getUserMessage().getText();

                    Map<String, Object> body = Map.of(
                            "model", "gpt-4o-mini",
                            "messages", List.of(
                                    Map.of("role", "system", "content", systemPrompt),
                                    Map.of("role", "user", "content", userPrompt)
                            )
                    );

                    Map<String, Object> jsonl = Map.of(
                            "custom_id", redisKey,
                            "method", "POST",
                            "url", "/v1/chat/completions",
                            "body", body
                    );

                    writer.write(objectMapper.writeValueAsString(jsonl));
                    writer.newLine();
                }
            }

            UploadFileRequest request = UploadFileRequest.builder()
                    .file(tempFile)
                    .purpose("batch")
                    .build();

            UploadFileResponse response = openAIClient.uploadFile(request);

            return response.getId();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload listening batch file", e);
        }
    }

}
