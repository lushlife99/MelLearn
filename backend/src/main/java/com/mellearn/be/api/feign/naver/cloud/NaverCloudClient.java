package com.mellearn.be.api.feign.naver.cloud;

import com.mellearn.be.api.feign.naver.cloud.dto.DetectLang;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "papago-detectLang-service",
        url = "https://naveropenapi.apigw.ntruss.com",
        configuration = NaverCloudConfig.class
)
public interface NaverCloudClient {

    @PostMapping("/langs/v1/dect")
    DetectLang detectLanguage(@RequestParam String query);
}
