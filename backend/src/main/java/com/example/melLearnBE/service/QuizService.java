package com.example.melLearnBE.service;

import com.example.melLearnBE.adapter.LocalDateTimeTypeAdapter;
import com.example.melLearnBE.dto.model.QuizDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.openAI.ChatGPTRequest;
import com.example.melLearnBE.dto.request.openAI.ChatQuestion;
import com.example.melLearnBE.dto.request.openAI.ChatRequest;
import com.example.melLearnBE.dto.request.openAI.Message;
import com.example.melLearnBE.dto.response.openAI.ChatGPTResponse;
import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.model.Quiz;
import com.example.melLearnBE.model.QuizList;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClient;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClientConfig;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.QuizRepository;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final OpenAIClientConfig clientConfig;
    private final QuizListRepository quizListRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OpenAIClient openAIClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final static String ROLE_USER = "user";
    private final static String ROLE_SYSTEM = "system";

    /**
     * 동시성 고려해서 코드 업데이트 해야함.
     * 아직 안했음.
     */

    @Transactional
    public QuizListDto getQuizList(QuizRequest quizRequest, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        Optional<QuizList> optionalQuizList = quizListRepository.findByMusicIdAndQuizTypeAndLevel(quizRequest.getMusicId(), quizRequest.getQuizType(), member.getLevel().getValue());
        if(optionalQuizList.isEmpty()) {
            return createQuizList(quizRequest, member.getLevel());
        } else {
            QuizList quizList = optionalQuizList.get();
            // redis를 이용해 quizList가 만들어지고 있는 상황인지 체크하기
            return new QuizListDto();
        }
    }
    public QuizListDto createQuizList(QuizRequest quizRequest, LearningLevel level) {
        QuizType quizType = quizRequest.getQuizType();
        if(quizType.equals(QuizType.GRAMMAR)) {

        } else if (quizType.equals(QuizType.READING)) {

        } else if (quizType.equals(QuizType.LISTENING)) {

        } else if (quizType.equals(QuizType.VOCABULARY)) {

            ChatQuestion question = ChatQuestion.builder()
                    .lyric(quizRequest.getLyric())
                    .level(level.getValue())
                    .build();

            ChatRequest vocaRequest = ChatRequest.builder()
                    .system(getPrompt(quizType))
                    .question(question.toString())
                    .build();


            int retries = 0;
            final int maxRetries = 3;
            boolean success = false;

            while (!success && retries < maxRetries) {
                try {
                    ChatGPTResponse chatGPTResponse = requestGPT(vocaRequest);
                    String jsonContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                    System.out.println(jsonContent);

                    JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                    JsonArray probListJsonArray = jsonObject.getAsJsonArray("probList");
                    Type listType = new TypeToken<List<Quiz>>(){}.getType();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                            .create();
                    List<Quiz> quizzes = gson.fromJson(probListJsonArray, listType);

                    QuizList quizList = QuizList.builder()
                            .quizzes(quizzes)
                            .quizType(quizType)
                            .level(level.getValue())
                            .musicId(quizRequest.getMusicId())
                            .build();
                    List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes.stream().toList());
                    QuizList savedQuizList = quizListRepository.save(quizList);
                    savedQuizList.setQuizzes(savedQuizzes);

                    success = true;
                    return new QuizListDto(savedQuizList);
                } catch (Exception e) {
                    retries++;
                    e.printStackTrace();
                    if (retries >= maxRetries) {
                        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }
                }
            }

        } else throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_TYPE);

        return null;
    }

    private ChatGPTResponse requestGPT(ChatRequest chatRequest){
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
                .model(clientConfig.getModel())
                .max_tokens(4095)
                .messages(messages)
                .build();
        return openAIClient.chat(chatGPTRequest);
    }

    private String getPrompt(QuizType quizType) {
        String filePath = "." + File.separator + "prompt" + File.separator + quizType.toString() + ".txt";

        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
