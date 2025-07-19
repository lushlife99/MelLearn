package com.mellearn.be.global.config;

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