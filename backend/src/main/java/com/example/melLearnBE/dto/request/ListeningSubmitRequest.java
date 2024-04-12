package com.example.melLearnBE.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ListeningSubmitRequest {

    private String musicId;
    private List<String> submitWordList;
}
