package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.request.openAI.SpeakingSubmitRequest;
import com.example.melLearnBE.dto.request.openAI.TranscriptionRequest;
import com.example.melLearnBE.dto.request.openAI.WhisperTranscriptionRequest;
import com.example.melLearnBE.dto.response.SupportQuizCategories;
import com.example.melLearnBE.dto.response.openAI.WhisperTranscriptionResponse;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClient;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClientConfig;
import com.example.melLearnBE.repository.AnswerSpeakingRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingService {

    private final AnswerSpeakingRepository answerSpeakingRepository;
    private final SupportService supportService;
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;
    public void submit(SpeakingSubmitRequest submitRequest, HttpServletRequest request) {
        String lrcLyric = submitRequest.getLrcLyric();
        MultipartFile file = submitRequest.getFile();

        SupportQuizCategories supportQuizCategory = supportService.getSupportQuizCategory(lrcLyric, request);
        if(!supportQuizCategory.isSpeaking())
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);

        //전처리

        // 1. lrc 파일의 시간데이터를 분할.
        List<LrcLyric> lrcLyrics = parseLrc(submitRequest);

        // 2. audio 파일을 lrc파일에 맞게 분할하고 사이에 temp audio 삽입
        File preprocessingAudio = audioPreprocessing(file, lrcLyrics);
        // whisper 전송
        WhisperTranscriptionResponse transcription = createTranscription(preprocessingAudio);

        // 전처리 2 (필요없을수도 있음)

        // 채점
    }

    private WhisperTranscriptionResponse createTranscription(File audioFile){

        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model(openAIClientConfig.getAudioModel())
                .file(audioFile)
                .timestamp_granularities(Collections.singletonList("segment"))
                .language("en")
                .response_format("verbose_json")
                .build();
        WhisperTranscriptionResponse transcription = openAIClient.createTranscription(whisperTranscriptionRequest);

        return transcription;
    }

    public File audioPreprocessing(MultipartFile file, List<LrcLyric> lrcLyrics) {
        try {
            ClassPathResource resource = new ClassPathResource("audio/silence.wav");
            ClassPathResource returnFilePath = new ClassPathResource("audio/silence.wav");

            File audioFile = new File(file.getName());
            File silenceFile = resource.getFile();
            File preProcessingAudio = new File(returnFilePath.getPath());

            file.transferTo(audioFile);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioInputStream silenceInputStream = AudioSystem.getAudioInputStream(silenceFile);


            for (LrcLyric lrcLyric : lrcLyrics) {
                AudioFormat format = inputStream.getFormat();

                long framesOfAudioToCopy = (long)((lrcLyric.endMs - lrcLyric.startMs) * (format.getFrameRate() / 1000));
                inputStream.skip(lrcLyric.startMs * (long) (format.getFrameRate() / 1000) * format.getFrameSize());
                AudioInputStream shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
                AudioSystem.write(shortenedStream, AudioFileFormat.Type.WAVE, preProcessingAudio);
                AudioSystem.write(silenceInputStream, AudioFileFormat.Type.WAVE, preProcessingAudio);

                shortenedStream.close();
            }

            inputStream.close();
            silenceInputStream.close();
            return preProcessingAudio;

        } catch (UnsupportedAudioFileException | IOException e) {
            log.info(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    private List<LrcLyric> parseLrc(SpeakingSubmitRequest submitRequest) {
        List<LrcLyric> lyrics = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2}\\.\\d{2})\\](.*)");
        String[] lines = submitRequest.getLrcLyric().split("\n");

        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                long startMs = convertToMilliseconds(matcher.group(1), matcher.group(2));
                long endMs = (i < lines.length - 1) ? convertToMilliseconds(lines[i + 1]) : submitRequest.getEndMs();
                String lyric = matcher.group(3);
                lyrics.add(new LrcLyric(startMs, endMs, lyric));
            }
        }

        // 마지막 가사의 끝 시간을 조정
        if (!lyrics.isEmpty()) {
            lyrics.get(lyrics.size() - 1).endMs = submitRequest.getEndMs();
        }

        return lyrics;
    }

    private long convertToMilliseconds(String line) {
        Matcher matcher = Pattern.compile("\\[(\\d{2}):(\\d{2}\\.\\d{2})\\]").matcher(line);
        if (matcher.find()) {
            return convertToMilliseconds(matcher.group(1), matcher.group(2));
        }
        return 0;
    }

    private long convertToMilliseconds(String minutes, String seconds) {
        return Long.parseLong(minutes) * 60000 + (long)(Double.parseDouble(seconds) * 1000);
    }

    @AllArgsConstructor
    class LrcLyric {
        long startMs;
        long endMs;
        String lyric;
    }

}
