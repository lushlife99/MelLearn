package com.mellearn.be.global.web.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.global.auth.jwt.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class QuizRequestResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(QuizRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        // request body를 읽어 JSON -> QuizRequest 변환
        String body = request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);

        ObjectMapper objectMapper = new ObjectMapper();
        QuizRequest original = objectMapper.readValue(body, QuizRequest.class);

        // JWT에서 level, language 가져오기
        String token = jwtTokenProvider.resolveToken(request);
        LearningLevel level = jwtTokenProvider.getLearningLevelFromToken(token);
        Language language = jwtTokenProvider.getLanguageFromToken(token);

        return QuizRequest.builder()
                .musicId(original.getMusicId())
                .quizType(original.getQuizType())
                .lyric(original.getLyric())
                .learningLevel(level)
                .language(language)
                .build();
    }
}