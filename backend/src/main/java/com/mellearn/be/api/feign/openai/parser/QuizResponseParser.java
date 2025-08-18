package com.mellearn.be.api.feign.openai.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mellearn.be.domain.quiz.listening.quiz.dto.response.chatmodel.ListeningQuizResponseDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel.QuizListResponseDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel.QuizResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QuizResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Choice Quiz: LLM JSONL body → QuizListResponseDto
     */
    public QuizListResponseDto parseQuizListResponse(Object body) {
        if (body == null) return new QuizListResponseDto(List.of());

        try {
            Map<String, Object> bodyMap = body instanceof Map
                    ? (Map<String, Object>) body
                    : objectMapper.readValue(body.toString(), Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) bodyMap.get("choices");
            if (choices == null || choices.isEmpty()) return new QuizListResponseDto(List.of());

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String contentStr = (String) message.get("content");
            if (contentStr == null || contentStr.isBlank()) return new QuizListResponseDto(List.of());

            Map<String, Object> quizJson = objectMapper.readValue(contentStr, Map.class);
            Object quizzesObj = quizJson.get("list of quiz");
            if (!(quizzesObj instanceof List)) return new QuizListResponseDto(List.of());

            List<QuizResponseDto> quizResponses = objectMapper.convertValue(
                    quizzesObj,
                    new TypeReference<List<QuizResponseDto>>() {}
            );

            return new QuizListResponseDto(quizResponses);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse QuizListResponseDto using ObjectMapper", e);
        }
    }

    /**
     * Listening Quiz: LLM JSONL body → ListeningQuizResponseDto
     */
    public ListeningQuizResponseDto parseListeningQuizResponse(Object body) {
        if (body == null) return null;

        try {
            Map<String, Object> bodyMap = body instanceof Map
                    ? (Map<String, Object>) body
                    : objectMapper.readValue(body.toString(), Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) bodyMap.get("choices");
            if (choices == null || choices.isEmpty()) return null;

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String contentStr = (String) message.get("content");
            if (contentStr == null || contentStr.isBlank()) return null;

            // content 안 Map으로 변환 후 DTO로 convertValue
            Map<String, Object> contentMap = objectMapper.readValue(contentStr, Map.class);
            ListeningQuizResponseDto dto = objectMapper.convertValue(contentMap, ListeningQuizResponseDto.class);

            // 필수 값 체크
            if (dto.blankedText() == null || dto.answers() == null) return null;

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ListeningQuizResponseDto using ObjectMapper", e);
        }
    }
}
