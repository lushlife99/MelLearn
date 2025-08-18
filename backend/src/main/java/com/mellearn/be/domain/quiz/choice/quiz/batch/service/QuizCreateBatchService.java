package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mellearn.be.api.feign.openai.client.OpenAIClient;
import com.mellearn.be.api.feign.openai.dto.request.BatchRequest;
import com.mellearn.be.api.feign.openai.dto.response.BatchResponse;
import com.mellearn.be.api.feign.openai.parser.QuizResponseParser;
import com.mellearn.be.api.feign.openai.service.OpenAIService;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel.QuizListResponseDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.listening.quiz.dto.response.chatmodel.ListeningQuizResponseDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizCreateBatchService {

    private static final String QUIZ_REQUEST_KEY = "quizRequest:*";
    private static final String LISTENING_QUIZ_REQUEST_KEY = "listeningQuizRequest:*";

    private final OpenAIService openAIService;
    private final BatchFileService batchFileService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;
    private final QuizResponseParser quizResponseParser;

    /**
     * Choice / Listening 퀴즈 배치 생성 요청
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createQuizListBatch() {
        createChoiceQuizBatch();
        createListeningQuizBatch();
    }

    /**
     * Choice / Listening 퀴즈 배치 결과 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processQuizCreateBatchResult() throws JsonProcessingException {
        pollChoiceQuizBatch();
        pollListeningQuizBatch();
    }

    private void createChoiceQuizBatch() {
        String inputFileId = batchFileService.uploadBatchFile();
        if (inputFileId == null) return;

        BatchRequest batchRequest = BatchRequest.builder()
                .inputFileId(inputFileId)
                .endpoint("/v1/chat/completions")
                .completionWindow("24h")
                .build();

        BatchResponse batchResponse = openAIService.createBatch(batchRequest);
        String batchId = batchResponse.getId();
        redisTemplate.opsForValue().set("batchId:choiceQuiz", batchId);
        log.info("ChoiceQuiz Batch created: {}", batchId);
    }

    private void createListeningQuizBatch() {
        String inputFileId = batchFileService.uploadListeningBatchFile();
        if (inputFileId == null) return;

        BatchRequest batchRequest = BatchRequest.builder()
                .inputFileId(inputFileId)
                .endpoint("/v1/chat/completions")
                .completionWindow("24h")
                .build();

        BatchResponse batchResponse = openAIService.createBatch(batchRequest);
        String batchId = batchResponse.getId();
        redisTemplate.opsForValue().set("batchId:listeningQuiz", batchId);
        log.info("ListeningQuiz Batch created: {}", batchId);
    }

    private void pollChoiceQuizBatch() throws JsonProcessingException {
        String batchId = (String) redisTemplate.opsForValue().get("batchId:choiceQuiz");
        if (batchId == null) return;

        BatchResponse response = openAIClient.retrieveBatch(batchId);
        if (!"completed".equals(response.getStatus())) return;

        String outputFileId = response.getOutputFileId();
        byte[] fileContent = openAIClient.downloadFile(outputFileId);
        String jsonlStr = new String(fileContent);

        List<QuizList> quizLists = new ArrayList<>();
        List<String> redisKeysToDelete = new ArrayList<>();

        for (String line : jsonlStr.split("\n")) {
            Map<String, Object> item = objectMapper.readValue(line, Map.class);
            String redisKey = (String) item.get("custom_id");
            QuizRequest qr = (QuizRequest) redisTemplate.opsForValue().get(redisKey);
            if (qr == null) continue;

            Map<String, Object> responseMap = (Map<String, Object>) item.get("response");
            if (responseMap == null) continue;

            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap == null) continue;

            QuizListResponseDto dto = quizResponseParser.parseQuizListResponse(bodyMap);
            if (dto == null) continue;

            QuizList quizList = QuizList.create(qr, dto);
            quizLists.add(quizList);
            redisKeysToDelete.add(redisKey);
        }

        if (!quizLists.isEmpty()) {
            quizListRepository.saveAll(quizLists);
            redisKeysToDelete.forEach(redisTemplate::delete);
            redisTemplate.delete("batchId:choiceQuiz");
            log.info("Choice quiz batch completed and saved.");
        }
    }

    private void pollListeningQuizBatch() throws JsonProcessingException {
        String batchId = (String) redisTemplate.opsForValue().get("batchId:listeningQuiz");
        if (batchId == null) return;

        BatchResponse response = openAIClient.retrieveBatch(batchId);
        if (!"completed".equals(response.getStatus())) return;

        String outputFileId = response.getOutputFileId();
        byte[] fileContent = openAIClient.downloadFile(outputFileId);
        String jsonlStr = new String(fileContent);

        List<ListeningQuiz> listeningQuizzes = new ArrayList<>();
        List<String> redisKeysToDelete = new ArrayList<>();

        for (String line : jsonlStr.split("\n")) {
            Map<String, Object> item = objectMapper.readValue(line, Map.class);
            String redisKey = (String) item.get("custom_id");
            QuizRequest qr = (QuizRequest) redisTemplate.opsForValue().get(redisKey);
            if (qr == null) continue;

            Map<String, Object> responseMap = (Map<String, Object>) item.get("response");
            if (responseMap == null) continue;

            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap == null) continue;

            ListeningQuizResponseDto dto = quizResponseParser.parseListeningQuizResponse(bodyMap);
            if (dto == null) continue;

            ListeningQuiz quiz = ListeningQuiz.create(
                    dto.blankedText(),
                    qr,
                    dto.answers()
            );
            listeningQuizzes.add(quiz);
            redisKeysToDelete.add(redisKey);
        }

        if (!listeningQuizzes.isEmpty()) {
            listeningQuizRepository.saveAll(listeningQuizzes);
            redisKeysToDelete.forEach(redisTemplate::delete);
            redisTemplate.delete("batchId:listeningQuiz");
            log.info("Listening quiz batch completed and saved.");
        }
    }
}
