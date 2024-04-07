package com.example.melLearnBE.dto.response;

import com.example.melLearnBE.enums.ProbType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.xpath.operations.Bool;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportQuizCategories {

    private boolean speaking;
    private boolean listening;
    private boolean reading;
    private boolean vocabulary;
    private boolean grammar;
}
