package com.mellearn.be.domain.quiz.listening.submit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeningSubmitRequest {

    private String musicId;
    private List<String> submitWordList;
}
