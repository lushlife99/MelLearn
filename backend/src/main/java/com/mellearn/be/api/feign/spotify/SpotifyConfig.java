package com.mellearn.be.api.feign.spotify;

import feign.RequestInterceptor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Indexed;

@Indexed
@Data
@Slf4j
public class SpotifyConfig {

    @Value("${spotify.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("x-rapidapi-key", apiKey);
        };
    }
}
