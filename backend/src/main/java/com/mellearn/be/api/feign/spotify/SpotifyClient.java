package com.mellearn.be.api.feign.spotify;

import com.mellearn.be.domain.music.chart.dto.ChartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "spotify-service",
        url = "https://spotify-scraper.p.rapidapi.com/v1",
        configuration = SpotifyConfig.class
)
public interface SpotifyClient {

    @GetMapping("/chart/tracks/top")
    ChartDto getChart(@RequestParam String type);
}
