package com.example.melLearnBE.openFeign.naverCloudClient;

import feign.Logger;
import feign.Retryer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;

@Configuration
@Indexed
@Data
@Slf4j
public class NaverCloudConfig {

    @Value("${naver.cloud.api.key-id}")
    private String xNcpApigwApiKeyId;

    @Value("${naver.cloud.api.key}")
    private String xNcpApigwApiKey;

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }
}
