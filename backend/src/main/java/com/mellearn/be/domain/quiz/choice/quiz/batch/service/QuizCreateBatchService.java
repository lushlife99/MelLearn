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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizCreateBatchService {

    private static final String BATCH_ID_CHOICE_KEY = "batchId:choiceQuiz";
    private static final String BATCH_ID_LISTENING_KEY = "batchId:listeningQuiz";

    private static final String OPEN_AI_BATCH_ID_KEY = "custom_id";
    private static final String OPEN_AI_RESPONSE_KEY = "response";
    private static final String OPEN_AI_BODY_KEY = "body";
    private static final String COMPLETE_STATUS = "complete";
    private static final String OPENAI_ENDPOINT = "/v1/chat/completions";
    private static final String COMPLETION_WINDOW = "24h";
    private static final int BATCH_SIZE = 100;

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
                .endpoint(OPENAI_ENDPOINT)
                .completionWindow(COMPLETION_WINDOW)
                .build();

        BatchResponse batchResponse = openAIService.createBatch(batchRequest);
        redisTemplate.opsForValue().set(BATCH_ID_CHOICE_KEY, batchResponse.getId());
        log.info("ChoiceQuiz Batch created: {}", batchResponse.getId());
    }

    private void createListeningQuizBatch() {
        String inputFileId = batchFileService.uploadListeningBatchFile();
        if (inputFileId == null) return;

        BatchRequest batchRequest = BatchRequest.builder()
                .inputFileId(inputFileId)
                .endpoint(OPENAI_ENDPOINT)
                .completionWindow(COMPLETION_WINDOW)
                .build();

        BatchResponse batchResponse = openAIService.createBatch(batchRequest);
        redisTemplate.opsForValue().set(BATCH_ID_LISTENING_KEY, batchResponse.getId());
        log.info("ListeningQuiz Batch created: {}", batchResponse.getId());
    }

    private void pollChoiceQuizBatch() throws JsonProcessingException {
        String batchId = (String) redisTemplate.opsForValue().get(BATCH_ID_CHOICE_KEY);
        if (batchId == null) return;

        BatchResponse response = openAIClient.retrieveBatch(batchId);
        if (!COMPLETE_STATUS.equals(response.getStatus())) return;

        String jsonlStr = new String(openAIClient.downloadFile(response.getOutputFileId()));

        List<QuizList> quizLists = new ArrayList<>();
        List<String> redisKeysToDelete = new ArrayList<>();

        for (String line : jsonlStr.split("\n")) {
            Map<String, Object> item = objectMapper.readValue(line, Map.class);
            String redisKey = (String) item.get(OPEN_AI_BATCH_ID_KEY);
            QuizRequest qr = (QuizRequest) redisTemplate.opsForValue().get(redisKey);

            Map<String, Object> responseMap = (Map<String, Object>) item.get(OPEN_AI_RESPONSE_KEY);
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get(OPEN_AI_BODY_KEY);
            QuizListResponseDto dto = quizResponseParser.parseQuizListResponse(bodyMap);

            QuizList quizList = QuizList.create(qr, dto);
            quizLists.add(quizList);
            redisKeysToDelete.add(redisKey);
        }

        batchInsert(quizLists, quizListRepository);
        deleteRedisKeys(redisKeysToDelete, BATCH_ID_CHOICE_KEY);

        log.info("Choice quiz batch completed and saved.");
    }

    private void pollListeningQuizBatch() throws JsonProcessingException {
        String batchId = (String) redisTemplate.opsForValue().get(BATCH_ID_LISTENING_KEY);
        if (batchId == null) return;

        BatchResponse response = openAIClient.retrieveBatch(batchId);
        if (!COMPLETE_STATUS.equals(response.getStatus())) return;

        String jsonlStr = new String(openAIClient.downloadFile(response.getOutputFileId()));

        List<ListeningQuiz> listeningQuizzes = new ArrayList<>();
        List<String> redisKeysToDelete = new ArrayList<>();

        for (String line : jsonlStr.split("\n")) {
            Map<String, Object> item = objectMapper.readValue(line, Map.class);
            String redisKey = (String) item.get(OPEN_AI_BATCH_ID_KEY);
            QuizRequest qr = (QuizRequest) redisTemplate.opsForValue().get(redisKey);

            Map<String, Object> responseMap = (Map<String, Object>) item.get(OPEN_AI_RESPONSE_KEY);
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get(OPEN_AI_BODY_KEY);
            ListeningQuizResponseDto dto = quizResponseParser.parseListeningQuizResponse(bodyMap);

            ListeningQuiz quiz = ListeningQuiz.create(dto.blankedText(), qr, dto.answers());
            listeningQuizzes.add(quiz);
            redisKeysToDelete.add(redisKey);
        }

        batchInsert(listeningQuizzes, listeningQuizRepository);
        deleteRedisKeys(redisKeysToDelete, BATCH_ID_LISTENING_KEY);

        log.info("Listening quiz batch completed and saved.");
    }

    /**
     * Batch Insert
     * 실패 시, 해당 청크는 하나씩 개별 저장
     */
    private <T> void batchInsert(List<T> entities, JpaRepository<T, ?> repository) {
        IntStream.range(0, (entities.size() + BATCH_SIZE - 1) / BATCH_SIZE)
                .mapToObj(i -> entities.subList(i * BATCH_SIZE, Math.min((i + 1) * BATCH_SIZE, entities.size())))
                .forEach(batch -> {
                    try {
                        repository.saveAll(batch);
                    } catch (Exception e) {
                        log.error("Failed to save batch, skipping. batchSize={}, error={}", batch.size(), e.getMessage());

                        // 선택적으로, 실패한 배치 개별 처리
                        batch.forEach(entity -> {
                            try {
                                repository.save(entity);
                            } catch (Exception ex) {
                                log.error("Failed to save entity individually, skipping. entity={}, error={}", entity, ex.getMessage());
                            }
                        });
                    }
                });
    }

    private void deleteRedisKeys(List<String> keys, String batchIdKey) {
        keys.forEach(redisTemplate::delete);
        redisTemplate.delete(batchIdKey);
    }
}
