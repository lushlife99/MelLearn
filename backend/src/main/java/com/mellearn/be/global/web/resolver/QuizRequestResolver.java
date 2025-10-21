//package com.mellearn.be.global.web.resolver;
//
//import com.mellearn.be.domain.member.enums.Language;
//import com.mellearn.be.domain.member.enums.LearningLevel;
//import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
//import com.mellearn.be.global.auth.jwt.service.JwtTokenProvider;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
//
//@Component
//@RequiredArgsConstructor
//public class QuizRequestResolver implements HandlerMethodArgumentResolver {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final RequestResponseBodyMethodProcessor delegate;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return parameter.getParameterType().equals(QuizRequest.class);
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter,
//                                  ModelAndViewContainer mavContainer,
//                                  NativeWebRequest webRequest,
//                                  WebDataBinderFactory binderFactory) throws Exception {
//
//        // 먼저 Spring이 제공하는 방식으로 request body를 QuizRequest로 변환
//        QuizRequest original;
//        try {
//            original = (QuizRequest) delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
//        } catch (HttpMessageNotReadableException e) {
//            throw new IllegalArgumentException("Request body is invalid", e);
//        }
//
//        // JWT에서 level, language 가져오기
//        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        String token = jwtTokenProvider.resolveToken(request);
//
//        LearningLevel level = null;
//        Language language = null;
//
//        if (token != null && !token.isBlank() && jwtTokenProvider.validateToken(token)) {
//            level = jwtTokenProvider.getLearningLevelFromToken(token);
//            language = jwtTokenProvider.getLanguageFromToken(token);
//        }
//
//        // JWT 정보 포함해서 새로운 QuizRequest 생성
//        return QuizRequest.builder()
//                .musicId(original.getMusicId())
//                .quizType(original.getQuizType())
//                .lyric(original.getLyric())
//                .learningLevel(level)
//                .language(language)
//                .build();
//    }
//}
