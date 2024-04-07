package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.request.SpeakingSubmitRequest;
import com.example.melLearnBE.dto.request.openAI.WhisperTranscriptionRequest;
import com.example.melLearnBE.dto.response.SupportQuizCategories;
import com.example.melLearnBE.dto.response.openAI.WhisperSegment;
import com.example.melLearnBE.dto.response.openAI.WhisperTranscriptionResponse;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClient;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClientConfig;
import com.example.melLearnBE.repository.AnswerSpeakingRepository;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingService {

    private final AnswerSpeakingRepository answerSpeakingRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SupportService supportService;
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;
    private static final double SILENCE_LENGTH = 2.0; // 전처리 할 때 사용하는 temp audio의 시간

    @Transactional
    public void submit(SpeakingSubmitRequest submitRequest, HttpServletRequest request) {

        List<LrcLyric> lyricList = submitRequest.getLyricList();
        String pureLyric = getPureLyric(lyricList);
        MultipartFile file = submitRequest.getFile();

        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        SupportQuizCategories supportQuizCategory = supportService.getSupportQuizCategory(pureLyric, request);
        if(!supportQuizCategory.isSpeaking())
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);

        log.info("lrcLyrics = {}", lyricList);

        // 0. 녹음 파일 저장. 테스트용도로 저장해놓음. 나중에 삭제.
        saveAudioFile(submitRequest);
        // 1. 전처리 1 - audio 파일을 lrc파일에 맞게 분할하고 사이에 temp audio 삽입
        File preprocessingAudio = audioPreprocess(file, lyricList);

        // 2. whisper 전송
        WhisperTranscriptionResponse transcription = createTranscription(preprocessingAudio, member.getLangType().getIso639Value());

        // 3. 전처리 2 - 세그먼트와 lyric 줄 싱크 맞추기, 대소문자 변경, 특수문자 제거
        List<WhisperSegment> whisperSegments = synchronizeLyricsAndSegments(lyricList, transcription);
        convertGradableFormat(lyricList, whisperSegments);

        // 4. 채점
        grade(lyricList, whisperSegments);

        // 5. 랭킹
    }

    private void saveAudioFile(SpeakingSubmitRequest submitRequest) {
        MultipartFile file = submitRequest.getFile();
        if (file.isEmpty()) {
            throw new IllegalStateException("파일이 비어 있습니다.");
        }

        try {
            // resources 디렉토리 아래에 audio 디렉토리 경로를 구성합니다.
            // Spring Boot 애플리케이션의 클래스 패스 상대 경로를 사용합니다.
            String resourcesDirectory = "./audio/" + UUID.randomUUID() + ".wav";
            Path directoryPath = Paths.get(resourcesDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath); // 디렉토리가 없다면 생성
            }

            Path filePath = directoryPath.resolve(file.getOriginalFilename());

            file.transferTo(filePath.toFile());

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }
    private void convertGradableFormat(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {
        convertLyricToLowerCase(answerLyrics, submitLyrics);
        removeSpecialChar(answerLyrics, submitLyrics);
    }

    private void removeSpecialChar(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {
        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            WhisperSegment submitLyricLine = submitLyrics.get(i);

            lrcLyricLine.setText(lrcLyricLine.getText().replaceAll("[^a-zA-Z0-9\\s]", ""));
            submitLyricLine.setText(submitLyricLine.getText().replaceAll("[^a-zA-Z0-9\\s]", ""));

        }
    }

    private void convertLyricToLowerCase(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {

        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            WhisperSegment submitLyricLine = submitLyrics.get(i);

            lrcLyricLine.setText(lrcLyricLine.getText().toLowerCase());
            submitLyricLine.setText(submitLyricLine.getText().toLowerCase());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String grade(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        double similarity = 0.0;

        for(int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            WhisperSegment submitLyricLine = submitLyrics.get(i);

            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            CoreDocument doc = new CoreDocument(lrcLyricLine.getText());
            pipeline.annotate(doc);
            Set<String> allTokens = new HashSet<>();
            doc.tokens().forEach(token -> allTokens.add(token.word()));
            List<String> rightSubmitTokens = gradeByLine(lrcLyricLine.getText(), submitLyricLine.getText());

            similarity += (rightSubmitTokens.size() / allTokens.size());

        }

        return null;
    }

    private List<String> gradeByLine(String answerTextLine, String submitTextLine) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        CoreDocument doc1 = new CoreDocument(answerTextLine);
        CoreDocument doc2 = new CoreDocument(submitTextLine);
        pipeline.annotate(doc1);
        pipeline.annotate(doc2);
        Set<String> words1 = new HashSet<>();
        Set<String> words2 = new HashSet<>();

        doc1.tokens().forEach(token -> words1.add(token.word()));
        doc2.tokens().forEach(token -> words2.add(token.word()));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        return intersection.stream().toList();
    }

    private List<WhisperSegment> synchronizeLyricsAndSegments(List<LrcLyric> lrcLyrics, WhisperTranscriptionResponse transcription) {
        List<WhisperSegment> segments = transcription.getSegments();

        // size가 같을경우 전처리가 필요없음.
        if(lrcLyrics.size() == segments.size()) {
            return segments;
        }

        log.info("Lyric Size is Not Matched. Start Progress of Whisper Transcription");

        // lrcLyrics.size() > segments.size()일 상황은 없음.
        if(lrcLyrics.size() > segments.size()) {
            log.info("Segment Lines is shorter than lrcLyrics");
            throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
        }

        // lrcLyrics.size() < segments.size()일 경우 전처리.
        for (int i = 0; i < segments.size(); i++) {
            if (i != segments.size() - 1) {
                if (segments.get(i+1).getStart() - segments.get(i).getEnd() < SILENCE_LENGTH) {
                    log.info("Segmented By Whisper Model");
                    log.info("LrcLyric = {}", lrcLyrics.get(i).getText());
                    log.info("Divided Segment1 ={}", segments.get(i).getText());
                    log.info("Divided Segment2 ={}", segments.get(i+1).getText());

                    WhisperSegment whisperSegment = segments.get(i);
                    WhisperSegment nextSegment = segments.get(i + 1);
                    whisperSegment.setEnd(nextSegment.getEnd());
                    whisperSegment.setText(whisperSegment.getText() + nextSegment.getText());
                    segments.remove(nextSegment);
                }

                if (lrcLyrics.size() == segments.size()) {
                    break;
                }
            }

        }

        return segments;
    }


    private WhisperTranscriptionResponse createTranscription(File audioFile, String langCode){

        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model(openAIClientConfig.getAudioModel())
                .file(audioFile)
                .timestamp_granularities(Collections.singletonList("segment"))
                .language(langCode)
                .response_format("verbose_json")
                .build();
        WhisperTranscriptionResponse transcription = openAIClient.createTranscription(whisperTranscriptionRequest);

        return transcription;
    }

    private File audioPreprocess(MultipartFile file, List<LrcLyric> lrcLyrics) {
        try {
            ClassPathResource resource = new ClassPathResource("audio/silence.wav");

            ClassPathResource returnFilePath = new ClassPathResource("audio/"+ UUID.randomUUID() +".wav");

            File audioFile = new File(file.getName());
            File silenceFile = resource.getFile();
            File preProcessingAudio = new File(returnFilePath.getPath());

            file.transferTo(audioFile);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioInputStream silenceInputStream = AudioSystem.getAudioInputStream(silenceFile);


            for (LrcLyric lrcLyric : lrcLyrics) {

                AudioFormat format = inputStream.getFormat();

                long framesOfAudioToCopy = (long)((lrcLyric.getStartMs() + lrcLyric.getDurMs()) * (format.getFrameRate() / 1000));
                inputStream.skip(lrcLyric.getStartMs() * (long) (format.getFrameRate() / 1000) * format.getFrameSize());
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
            throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
        }

    }

//    private List<LrcLyric> parseLrc(SpeakingSubmitRequest submitRequest) {
//        List<LrcLyric> lyrics = new ArrayList<>();
//        Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2}\\.\\d{2})\\](.*)");
//        String[] lines = submitRequest.getLrcLyric().split("\n");
//
//        for (int i = 0; i < lines.length; i++) {
//            Matcher matcher = pattern.matcher(lines[i]);
//            if (matcher.find()) {
//                long startMs = convertToMilliseconds(matcher.group(1), matcher.group(2));
//                long endMs = (i < lines.length - 1) ? convertToMilliseconds(lines[i + 1]) : submitRequest.getEndMs();
//                String lyric = matcher.group(3);
//                if(lyric.equals("♪")) {
//                    continue;
//                }
//                lyrics.add(new LrcLyric(startMs, endMs, lyric));
//            }
//        }
//
//        // 마지막 가사의 끝 시간을 조정
//        if (!lyrics.isEmpty()) {
//            lyrics.get(lyrics.size() - 1).endMs = submitRequest.getEndMs();
//        }
//
//        return lyrics;
//    }

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


    private String getPureLyric(List<LrcLyric> lrcLyrics) {
        StringBuilder stringBuilder = new StringBuilder();

        for (LrcLyric lrcLyric : lrcLyrics) {
            stringBuilder.append(lrcLyric.getText());
        }

        return stringBuilder.toString();
    }

}
