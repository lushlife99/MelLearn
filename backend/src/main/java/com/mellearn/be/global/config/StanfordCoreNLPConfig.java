package com.mellearn.be.global.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class StanfordCoreNLPConfig {

    @Bean
    public StanfordCoreNLP stanfordCoreNLP() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        return new StanfordCoreNLP(props);
    }
} 