package com.mellearn.be.domain.quiz.speaking.dto;

import com.mellearn.be.domain.music.dto.LrcLyric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpeakingSubmitRequest implements Serializable {

    private MultipartFile file;
    private List<LrcLyric> lyricList;
}