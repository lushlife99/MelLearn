package com.example.melLearnBE.openFeign.naverCloudClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "papago-detectLang-service",
        url = "https://naveropenapi.apigw.ntruss.com"
)
public interface NaverCloudClient {

    @PostMapping("/langs/v1/dect")
    String detectLanguage(@RequestHeader("X-NCP-APIGW-API-KEY-ID") String clientId,
                          @RequestHeader("X-NCP-APIGW-API-KEY") String clientSecret,
                          @RequestParam("query") String query);
}
