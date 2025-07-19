package com.mellearn.be.domain.word.dto;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.word.entity.Word;
import com.mellearn.be.domain.word.entity.WordList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordListDto {

    private Long id;
    private List<WordDto> words;
    private Language langType;

    public WordListDto(WordList wordList) {
        this.id = wordList.getId();
        this.words = new ArrayList<>();
        for (Word word : wordList.getWords()) {
            words.add(new WordDto(word));
        }
        this.langType = wordList.getLangType();
    }
}
