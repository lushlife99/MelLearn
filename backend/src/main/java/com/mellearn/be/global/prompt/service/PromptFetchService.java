package com.mellearn.be.global.prompt.service;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PromptFetchService {

    @Value("classpath:/prompt/GRAMMAR/system/grammar-system-message.st")
    private Resource grammarSystemMessageTemplate;
    @Value("classpath:/prompt/GRAMMAR/user/grammar-user-message.st")
    private Resource grammarUserMessageTemplate;

    @Value("classpath:/prompt/LISTENING/system/listening-system-message.st")
    private Resource listeningSystemMessageTemplate;
    @Value("classpath:/prompt/LISTENING/user/listening-user-message.st")
    private Resource listeningUserMessageTemplate;

    @Value("classpath:/prompt/READING/system/reading-system-message.st")
    private Resource readingSystemMessageTemplate;
    @Value("classpath:/prompt/READING/user/reading-user-message.st")
    private Resource readingUserMessageTemplate;

    @Value("classpath:/prompt/VOCABULARY/system/advanced/vocabulary-advanced-system-message.st")
    private Resource vocabularyAdvancedSystemMessageTemplate;
    @Value("classpath:/prompt/VOCABULARY/system/beginner/vocabulary-beginner-system-message.st")
    private Resource vocabularyBeginnerSystemMessageTemplate;
    @Value("classpath:/prompt/VOCABULARY/system/intermediate/vocabulary-intermediate-system-message.st")
    private Resource vocabularyIntermediateSystemMessageTemplate;

    @Value("classpath:/prompt/VOCABULARY/user/vocabulary-user-message.st")
    private Resource vocabularyUserMessageTemplate;

    public Prompt fetch(QuizRequest request, Member member) {
        LearningLevel level = member.getLevel();
        Language langType = member.getLangType();
        SystemPromptTemplate systemPrompt = null;

        if (request.getQuizType().equals(QuizType.VOCABULARY)) {
            systemPrompt = getVocabularySystemPrompt(request, level);
        }

        else if (request.getQuizType().equals(QuizType.GRAMMAR)) {
            systemPrompt = getGrammarSystemPrompt();
        }

        else if (request.getQuizType().equals(QuizType.READING)) {
            systemPrompt = getReadingSystemPrompt();
        }

        // listening
        else systemPrompt = getListeningSystemPrompt();

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .resource(readingUserMessageTemplate)
                .build();

        String renderedMessage = promptTemplate.render();
        UserMessage userMessage = new UserMessage(renderedMessage);

        return new Prompt(List.of(systemPrompt.createMessage(), userMessage));

    }

    private SystemPromptTemplate getVocabularySystemPrompt(QuizRequest quizRequest, LearningLevel level) {
        if (level.equals(LearningLevel.Beginner)) {
            return new SystemPromptTemplate(vocabularyBeginnerSystemMessageTemplate);
        }

        if (level.equals(LearningLevel.Intermediate)) {
            return new SystemPromptTemplate(vocabularyIntermediateSystemMessageTemplate);
        }

        else return new SystemPromptTemplate(vocabularyAdvancedSystemMessageTemplate);
    }

    private SystemPromptTemplate getReadingSystemPrompt() {
        return new SystemPromptTemplate(readingSystemMessageTemplate);
    }

    private SystemPromptTemplate getListeningSystemPrompt() {
        return new SystemPromptTemplate(listeningSystemMessageTemplate);
    }

    private SystemPromptTemplate getGrammarSystemPrompt() {
        return new SystemPromptTemplate(grammarSystemMessageTemplate);
    }
}
