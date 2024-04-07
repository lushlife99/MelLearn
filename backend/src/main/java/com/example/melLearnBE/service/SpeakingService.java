package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.request.SpeakingSubmitRequest;
import com.example.melLearnBE.dto.request.openAI.WhisperTranscriptionRequest;
import com.example.melLearnBE.dto.response.AnswerSpeakingDto;
import com.example.melLearnBE.dto.response.SupportQuizCategories;
import com.example.melLearnBE.dto.response.openAI.WhisperSegment;
import com.example.melLearnBE.dto.response.openAI.WhisperTranscriptionResponse;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.AnswerSpeaking;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClient;
import com.example.melLearnBE.openFeign.openAIClient.OpenAIClientConfig;
import com.example.melLearnBE.repository.AnswerSpeakingRepository;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    @Transactional
    public AnswerSpeakingDto submit(SpeakingSubmitRequest submitRequest, String musicId, HttpServletRequest request) {

        List<LrcLyric> lyricList = submitRequest.getLyricList();
        MultipartFile file = submitRequest.getFile();
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        SupportQuizCategories supportQuizCategory = supportService.getSupportQuizCategory(lyricList, request);
        if (!supportQuizCategory.isSpeaking())
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);

        // 1. 전처리 1 - audio 파일을 lrc파일에 맞게 분할하고 사이에 temp audio 삽입
        File preprocessingAudio = audioPreprocess(submitRequest);

        // 2. whisper 전송
        WhisperTranscriptionResponse transcription = createTranscription(preprocessingAudio, member.getLangType().getIso639Value());

        List<WhisperSegment> whisperSegments = transcription.getSegments();
        // 3. 후처리 2 - 세그먼트와 lyric 줄 싱크 맞추기, 대소문자 변경, 특수문자 제거
        //List<WhisperSegment> whisperSegments = synchronizeLyricsAndSegments(lyricList, transcription);
        convertGradableFormat(lyricList, whisperSegments);

        // 4. 채점
        AnswerSpeakingDto answerSpeaking = grade(musicId, lyricList, member, transcription);

        // 5. 랭킹
        return answerSpeaking;
    }



    private void convertGradableFormat(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {
        convertLyricToLowerCase(answerLyrics, submitLyrics);
        removeSpecialChar(answerLyrics, submitLyrics);
    }

    private void removeSpecialChar(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {
        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);

            lrcLyricLine.setText(lrcLyricLine.getText().replaceAll("[^a-zA-Z0-9\\s]", ""));
        }

        for (int i = 0; i < submitLyrics.size(); i++) {
            WhisperSegment submitLyricLine = submitLyrics.get(i);
            submitLyricLine.setText(submitLyricLine.getText().replaceAll("[^a-zA-Z0-9\\s]", ""));
        }
    }

    private void convertLyricToLowerCase(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {

        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);

            lrcLyricLine.setText(lrcLyricLine.getText().toLowerCase());
        }

        for (int i = 0; i < submitLyrics.size(); i++) {
            WhisperSegment submitLyricLine = submitLyrics.get(i);
            submitLyricLine.setText(submitLyricLine.getText().toLowerCase());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AnswerSpeakingDto grade(String musicId, List<LrcLyric> answerLyrics, Member member, WhisperTranscriptionResponse transcriptionResponse) {

        List<WhisperSegment> submitLyrics = transcriptionResponse.getSegments();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        int allTokenSize = 0;
        int wrongTokenSize = 0;
        StringBuilder answerSheet = new StringBuilder();

        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            CoreDocument doc = new CoreDocument(lrcLyricLine.getText());
            pipeline.annotate(doc);
            Set<String> allTokens = new HashSet<>();
            doc.tokens().forEach(token -> allTokens.add(token.word()));
            allTokenSize += allTokens.size();
            String lyricLineText = lrcLyricLine.getText();

            if(i < submitLyrics.size()) {

                WhisperSegment submitLyricLine = submitLyrics.get(i);
                List<String> wrongWords = gradeByLine(lrcLyricLine.getText(), submitLyricLine.getText());
                wrongTokenSize += wrongWords.size();

                for(String wrongWord : wrongWords) {
                    answerSheet.append(lyricLineText.replaceAll("(?i)\\b" + wrongWord + "\\b", "__" + wrongWord));
                }
            } else {
                answerSheet.append(lyricLineText.replaceAll("\\b(\\w+)", "__$1"));
            }

            // 사전에 등재되어 있지 않은 단어나 수사, 감탄사 로직 추가. (아직 사전 api 허가 못받음)

        }

        log.info("answerSheet={}", answerSheet);

        AnswerSpeaking answerSpeaking = AnswerSpeaking.builder()
                .musicId(musicId)
                .markedText(answerSheet.toString())
                .submit(transcriptionResponse.getText())
                .score(wrongTokenSize / allTokenSize)
                .member(member)
                .build();

        answerSpeakingRepository.save(answerSpeaking);


        return new AnswerSpeakingDto(answerSpeaking);
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

            for (LrcLyric lrcLyric : lrcLyrics) {
                AudioInputStream originalStream = AudioSystem.getAudioInputStream(requestAudioFile);
                long framesToSkip = lrcLyric.getStartMs() * (long) (audioFormat.getFrameRate() / 1000);
                originalStream.skip(framesToSkip * audioFormat.getFrameSize());

                long framesOfAudioToCopy = (long) ((lrcLyric.getDurMs()) * (audioFormat.getFrameRate() / 1000));
                AudioInputStream shortenedStream = new AudioInputStream(originalStream, audioFormat, framesOfAudioToCopy);
                ByteArrayInputStream silenceBAISForThisLoop = new ByteArrayInputStream(silenceData);
                AudioInputStream silenceAudioStreamForThisLoop = new AudioInputStream(silenceBAISForThisLoop, audioFormat, silenceData.length / audioFormat.getFrameSize());

                // 오디오 조각과 사일런스 추가
                audioParts.add(shortenedStream);
                audioParts.add(silenceAudioStreamForThisLoop);
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

//    private static void testLog(List<LrcLyric> lyricList, WhisperTranscriptionResponse transcription) {
//        System.out.println("lrcLyric: ");
//
//        for (LrcLyric lrcLyric : lyricList) {
//            System.out.println(lrcLyric.getText());
//        }
//
//        System.out.println("Whisper Segments: ");
//
//        System.out.println(transcription);
//
//        for (WhisperSegment segment : transcription.getSegments()) {
//            System.out.println(segment.getText());
//        }
//    }
//
//    private List<WhisperSegment> synchronizeLyricsAndSegments(List<LrcLyric> lrcLyrics, WhisperTranscriptionResponse transcription) {
//        List<WhisperSegment> segments = transcription.getSegments();
//
//        // size가 같을경우 후처리가 필요없음.
//        if (lrcLyrics.size() == segments.size()) {
//            return segments;
//        }
//
//        log.info("Lyric Size is Not Matched. Start Progress of Whisper Transcription");
//
//        // lrcLyrics.size() > segments.size()일 상황은 없음.
//        if (lrcLyrics.size() > segments.size()) {
//            log.info("Segment Lines is shorter than lrcLyrics");
//            throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
//        }
//
//        // lrcLyrics.size() < segments.size()일 경우 전처리.
//        for (int i = 0; i < segments.size(); i++) {
//            if (i != segments.size() - 1) {
//                if (segments.get(i + 1).getStart() - segments.get(i).getEnd() < SILENCE_LENGTH) {
//                    log.info("Segmented By Whisper Model");
//                    log.info("LrcLyric = {}", lrcLyrics.get(i).getText());
//                    log.info("Divided Segment1 ={}", segments.get(i).getText());
//                    log.info("Divided Segment2 ={}", segments.get(i + 1).getText());
//
//                    WhisperSegment whisperSegment = segments.get(i);
//                    WhisperSegment nextSegment = segments.get(i + 1);
//                    whisperSegment.setEnd(nextSegment.getEnd());
//                    whisperSegment.setText(whisperSegment.getText() + nextSegment.getText());
//                    segments.remove(nextSegment);
//                }
//
//                if (lrcLyrics.size() == segments.size()) {
//                    break;
//                }
//            }
//        }
//
//        return segments;
//    }

}
