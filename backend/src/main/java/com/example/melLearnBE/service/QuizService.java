package com.example.melLearnBE.service;

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
import com.example.melLearnBE.model.QuizList;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClient;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClientConfig;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.JsonObject;
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
    private final Environment env;

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
                    .system(env.getProperty("prompt.vocabulary"))
                    .question(question.toString())
                    .build();

            System.out.println(vocaRequest);
            //ChatGPTResponse chatGPTResponse = requestGPT(vocaRequest);

        } else throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_TYPE);

        return null;
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
                .model(clientConfig.getModel())
                .max_tokens(4095)
                .messages(messages)
                .build();
        return openAIClient.chat(chatGPTRequest);
    }
}
