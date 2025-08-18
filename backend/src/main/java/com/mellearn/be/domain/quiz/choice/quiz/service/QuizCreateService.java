package com.mellearn.be.domain.quiz.choice.quiz.service;

import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel.QuizListResponseDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizRepository;
import com.mellearn.be.domain.quiz.listening.quiz.dto.response.chatmodel.ListeningQuizResponseDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.global.prompt.service.PromptFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizCreateService {

    private final ChatModel chatModel;
    private final QuizListRepository quizListRepository;
    private final QuizRepository quizRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final PromptFetchService promptFetchService;


    @Transactional(propagation = Propagation.REQUIRED)
    public ListeningQuiz createListeningQuiz(QuizRequest request) {
        BeanOutputConverter<ListeningQuizResponseDto> converter = new BeanOutputConverter<>(ListeningQuizResponseDto.class);
        Prompt prompt = promptFetchService.fetch(request, converter.getFormat());
        Generation result = chatModel.call(prompt).getResult();
        ListeningQuizResponseDto listeningQuizResponseDto = converter.convert(Objects.requireNonNull(result.getOutput().getText()));

        return ListeningQuiz.create(listeningQuizResponseDto.blankedText(),
                request,
                listeningQuizResponseDto.answers());

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuizList createChoiceQuiz(QuizRequest request) {
        BeanOutputConverter<QuizListResponseDto> converter = new BeanOutputConverter<>(QuizListResponseDto.class);
        Prompt prompt = promptFetchService.fetch(request, converter.getFormat());
        Generation result = chatModel.call(prompt).getResult();
        QuizListResponseDto quizListDto = converter.convert(Objects.requireNonNull(result.getOutput().getText()));
        return QuizList.create(request, quizListDto);

    }

}

