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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingService {

    private final AnswerSpeakingRepository answerSpeakingRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SupportService supportService;
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;
    private static final String AUDIO_PATH = "." + File.separator + "audio" + File.separator;
    private static final double SILENCE_LENGTH = 2.0; // 전처리 할 때 사용하는 temp audio의 시간

    @Transactional
    public List<WhisperSegment> submit(SpeakingSubmitRequest submitRequest, HttpServletRequest request) {

        List<LrcLyric> lyricList = submitRequest.getLyricList();
        MultipartFile file = submitRequest.getFile();
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        SupportQuizCategories supportQuizCategory = supportService.getSupportQuizCategory(lyricList, request);
        if (!supportQuizCategory.isSpeaking())
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);


        // 0. 녹음 파일 저장. 테스트용도로 저장해놓음. 나중에 삭제.
        //saveAudioFile(submitRequest);
        // 1. 전처리 1 - audio 파일을 lrc파일에 맞게 분할하고 사이에 temp audio 삽입
        File preprocessingAudio = audioPreprocess(submitRequest);

        // 2. whisper 전송
        WhisperTranscriptionResponse transcription = createTranscription(preprocessingAudio, member.getLangType().getIso639Value());

        System.out.println("lrcLyric: ");

        for (LrcLyric lrcLyric : lyricList) {
            System.out.println(lrcLyric.getText());
        }

        System.out.println("Whisper Segments: ");

        System.out.println(transcription);

        for (WhisperSegment segment : transcription.getSegments()) {
            System.out.println(segment.getText());
        }
        // 3. 전처리 2 - 세그먼트와 lyric 줄 싱크 맞추기, 대소문자 변경, 특수문자 제거
        List<WhisperSegment> whisperSegments = synchronizeLyricsAndSegments(lyricList, transcription);
        convertGradableFormat(lyricList, whisperSegments);

        // 4. 채점
        grade(lyricList, whisperSegments);

        // 5. 랭킹
        return whisperSegments;
    }

    private void saveAudioFile(SpeakingSubmitRequest submitRequest) {
        MultipartFile file = submitRequest.getFile();
        if (file.isEmpty()) {
            throw new IllegalStateException("파일이 비어 있습니다.");
        }

        try {
            String fileName = UUID.randomUUID() + ".m4a";
            Path directory = Paths.get(AUDIO_PATH).toAbsolutePath();
            if (!Files.exists(directory)) {
                Files.createDirectories(directory); // 디렉토리 생성
            }
            File uploadFile = new File(directory + File.separator + fileName);
            System.out.println(uploadFile.getAbsolutePath());
            file.transferTo(uploadFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 중 오류 발생", e);
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

        for (int i = 0; i < answerLyrics.size(); i++) {
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

        // size가 같을경우 후처리가 필요없음.
        if (lrcLyrics.size() == segments.size()) {
            return segments;
        }

        log.info("Lyric Size is Not Matched. Start Progress of Whisper Transcription");

        // lrcLyrics.size() > segments.size()일 상황은 없음.
        if (lrcLyrics.size() > segments.size()) {
            log.info("Segment Lines is shorter than lrcLyrics");
            throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
        }

        // lrcLyrics.size() < segments.size()일 경우 전처리.
        for (int i = 0; i < segments.size(); i++) {
            if (i != segments.size() - 1) {
                if (segments.get(i + 1).getStart() - segments.get(i).getEnd() < SILENCE_LENGTH) {
                    log.info("Segmented By Whisper Model");
                    log.info("LrcLyric = {}", lrcLyrics.get(i).getText());
                    log.info("Divided Segment1 ={}", segments.get(i).getText());
                    log.info("Divided Segment2 ={}", segments.get(i + 1).getText());

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


    private WhisperTranscriptionResponse createTranscription(File audioFile, String langCode) {

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

    private File audioPreprocess(SpeakingSubmitRequest submitRequest) {
        MultipartFile file = submitRequest.getFile();
        List<LrcLyric> lrcLyrics = submitRequest.getLyricList();
        Path directory = Paths.get(AUDIO_PATH).toAbsolutePath();

        try {
            File requestAudioFile = new File(directory + File.separator + UUID.randomUUID() + ".wav");
            file.transferTo(requestAudioFile);

            AudioFormat audioFormat;
            try (AudioInputStream tempStream = AudioSystem.getAudioInputStream(requestAudioFile)) {
                audioFormat = tempStream.getFormat();
            }

            // 병합할 오디오 스트림 리스트
            List<AudioInputStream> audioParts = new ArrayList<>();

            // 사일런스 데이터 준비
            AudioInputStream silenceStream = AudioSystem.getAudioInputStream(new File(directory + File.separator + "silence.wav"));
            byte[] silenceData = new byte[(int) silenceStream.getFrameLength() * silenceStream.getFormat().getFrameSize()];
            silenceStream.read(silenceData);
            ByteArrayInputStream silenceBAIS = new ByteArrayInputStream(silenceData);
            AudioInputStream silenceAudioStream = new AudioInputStream(silenceBAIS, audioFormat, 2000);

            for (LrcLyric lrcLyric : lrcLyrics) {
                AudioInputStream originalStream = AudioSystem.getAudioInputStream(requestAudioFile);
                long framesToSkip = lrcLyric.getStartMs() * (long) (audioFormat.getFrameRate() / 1000);
                originalStream.skip(framesToSkip * audioFormat.getFrameSize());

                long framesOfAudioToCopy = (long) ((lrcLyric.getDurMs()) * (audioFormat.getFrameRate() / 1000));
                AudioInputStream shortenedStream = new AudioInputStream(originalStream, audioFormat, framesOfAudioToCopy);

                // 오디오 조각과 사일런스 추가
                audioParts.add(shortenedStream);
                audioParts.add(silenceAudioStream);
            }

            // 오디오 스트림 병합
            AudioInputStream concatenatedStream = new SequenceAudioInputStream(audioFormat, audioParts);

            // 최종 오디오 파일 저장
            File preProcessingAudio = new File(directory + File.separator + "pre" + UUID.randomUUID() + ".wav");
            AudioSystem.write(concatenatedStream, AudioFileFormat.Type.WAVE, preProcessingAudio);

            return preProcessingAudio;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
        }
    }

    class SequenceAudioInputStream extends AudioInputStream {
        public SequenceAudioInputStream(AudioFormat audioFormat, List<AudioInputStream> audioInputStreams) {
            super(new SequenceInputStream(Collections.enumeration(getInputStreams(audioInputStreams))), audioFormat, getFrameLength(audioInputStreams));
        }

        private static Vector<InputStream> getInputStreams(List<AudioInputStream> streams) {
            Vector<InputStream> inputStreams = new Vector<>();
            for (AudioInputStream stream : streams) {
                inputStreams.add(stream);
            }
            return inputStreams;
        }

        private static long getFrameLength(List<AudioInputStream> streams) {
            long totalLength = 0;
            for (AudioInputStream stream : streams) {
                long frameLength = stream.getFrameLength();
                if (frameLength != AudioSystem.NOT_SPECIFIED) {
                    totalLength += frameLength;
                } else {
                    return AudioSystem.NOT_SPECIFIED;
                }
            }
            return totalLength;
        }
    }

}
