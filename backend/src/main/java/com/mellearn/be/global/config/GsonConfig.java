package com.mellearn.be.global.config;

/**
 * Gson - AI 모델의 응답을 역직렬화 하는데 사용
 *
 * 25.07.20
 * Spring AI 도입으로 더이상 사용하지 않음
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mellearn.be.global.adapter.LocalDateTimeTypeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setLenient()
                .create();
    }
} 