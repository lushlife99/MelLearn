package com.mellearn.be.domain.quiz.choice.quiz.service;

import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.quiz.dto.response.chatmodel.ListeningQuizResponseDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel.QuizListResponseDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.global.prompt.service.PromptFetchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizCreateService {

    private final ChatModel chatModel;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final PromptFetchService promptFetchService;


    @Transactional
    public ListeningQuizDto createListeningQuiz(QuizRequest request, Member member) {
        BeanOutputConverter<ListeningQuizResponseDto> converter = new BeanOutputConverter<>(ListeningQuizResponseDto.class);
        Prompt prompt = promptFetchService.fetch(request, member);
        Generation result = chatModel.call(prompt).getResult();
        ListeningQuizResponseDto listeningQuizResponseDto = converter.convert(Objects.requireNonNull(result.getOutput().getText()));

        ListeningQuiz listeningQuiz = ListeningQuiz.create(listeningQuizResponseDto.blankedText(),
                request.getMusicId(), member.getLevel(),
                listeningQuizResponseDto.answers());

        return new ListeningQuizDto(listeningQuizRepository.save(listeningQuiz));
    }

    @Transactional
    public QuizListDto createChoiceQuiz(QuizRequest request, Member member) {
        BeanOutputConverter<QuizListResponseDto> converter = new BeanOutputConverter<>(QuizListResponseDto.class);
        Prompt prompt = promptFetchService.fetch(request, member);
        Generation result = chatModel.call(prompt).getResult();
        QuizListResponseDto quizListDto = converter.convert(Objects.requireNonNull(result.getOutput().getText()));

        QuizList quizList = QuizList.create(request.getQuizType(), quizListDto, member.getLevel(), request.getMusicId());
        quizListRepository.save(quizList);
        return new QuizListDto(quizList);
    }

}

