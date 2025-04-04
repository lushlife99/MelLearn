package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.MusicDto;
import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.request.SpeakingSubmitRequest;
import com.example.melLearnBE.dto.model.SpeakingSubmitDto;
import com.example.melLearnBE.dto.response.openAI.WhisperSegment;
import com.example.melLearnBE.dto.response.openAI.WhisperTranscriptionResponse;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.model.SpeakingSubmit;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.repository.SpeakingSubmitRepository;
import com.example.melLearnBE.repository.MemberRepository;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingService {

    private final SpeakingSubmitRepository speakingSubmitRepository;
    private final MemberRepository memberRepository;
    private final SupportService supportService;
    private final OpenAIService openAIService;
    private final StanfordCoreNLP stanfordCoreNLP;
    
    @Value("${ffmpeg.mpeg}")
    private String ffmpegPath;
    
    @Value("${ffmpeg.probe}")
    private String ffprobePath;
    
    private static final String AUDIO_PATH = "." + File.separator + "audio" + File.separator;

    @Async
    public CompletableFuture<SpeakingSubmitDto> submit(SpeakingSubmitRequest submitRequest, String musicId, String memberId) {
        Member member = findMember(memberId);
        MusicDto musicDto = validateSpeakingQuiz(musicId, submitRequest.getLyricList(), memberId);
        
        File preprocessedAudio = audioPreprocess(submitRequest);
        WhisperTranscriptionResponse transcription = openAIService.createTranscription(preprocessedAudio, member.getLangType().getIso639Value());
        
        convertGradableFormat(submitRequest.getLyricList(), transcription);
        SpeakingSubmitDto result = grade(musicId, submitRequest.getLyricList(), member, transcription);
        
        return CompletableFuture.completedFuture(result);
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found with id: {}", memberId);
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }

    private MusicDto validateSpeakingQuiz(String musicId, List<LrcLyric> lyricList, String memberId) {
        MusicDto musicDto = supportService.getSupportQuizCategory(musicId, lyricList, memberId);
        if (!musicDto.isSpeaking()) {
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);
        }
        return musicDto;
    }

    private void convertGradableFormat(List<LrcLyric> answerLyrics, WhisperTranscriptionResponse transcription) {
        processLyrics(answerLyrics, transcription.getSegments(), this::convertLyricToLowerCase);
        processLyrics(answerLyrics, transcription.getSegments(), this::removeSpecialChar);
    }

    private void processLyrics(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics, LyricsProcessor processor) {
        answerLyrics.forEach(lyric -> processor.process(lyric.getText()));
        submitLyrics.forEach(segment -> processor.process(segment.getText()));
    }

    @FunctionalInterface
    private interface LyricsProcessor {
        void process(String text);
    }

    private void removeSpecialChar(String text) {
        text = text.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    private void convertLyricToLowerCase(String text) {
        text = text.toLowerCase();
    }

    public SpeakingSubmitDto grade(String musicId, List<LrcLyric> answerLyrics, Member member, WhisperTranscriptionResponse transcriptionResponse) {
        List<WhisperSegment> submitLyrics = transcriptionResponse.getSegments();
        GradingResult gradingResult = calculateGradingResult(answerLyrics, submitLyrics);
        
        SpeakingSubmit speakingSubmit = SpeakingSubmit.create(
            musicId,
            member,
            gradingResult.answerSheet(),
            transcriptionResponse.getText(),
            100.0 - (gradingResult.wrongTokenSize() / gradingResult.allTokenSize() * 100.0)
        );
        
        speakingSubmitRepository.save(speakingSubmit);
        
        return new SpeakingSubmitDto(speakingSubmit);
    }

    private GradingResult calculateGradingResult(List<LrcLyric> answerLyrics, List<WhisperSegment> submitLyrics) {
        double allTokenSize = 0;
        double wrongTokenSize = 0;
        StringBuilder answerSheet = new StringBuilder();

        try {
            for (int i = 0; i < answerLyrics.size(); i++) {
                LrcLyric lrcLyricLine = answerLyrics.get(i);
                Set<String> allTokens = getTokens(lrcLyricLine.getText());
                allTokenSize += allTokens.size();
                
                String lyricLineText = processLyricLine(lrcLyricLine, submitLyrics, i);
                answerSheet.append(lyricLineText).append("\n");
            }
        } catch (Exception e) {
            log.error("Error in calculateGradingResult: ", e);
            throw new CustomException(ErrorCode.SPEAKING_GRADING_ERROR);
        }

        return new GradingResult(allTokenSize, wrongTokenSize, answerSheet.toString());
    }

    private Set<String> getTokens(String text) {
        try {
            CoreDocument doc = new CoreDocument(text);
            stanfordCoreNLP.annotate(doc);
            return doc.tokens().stream()
                    .map(token -> token.word())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error in getTokens: ", e);
            throw new CustomException(ErrorCode.SPEAKING_GRADING_ERROR);
        }
    }

    private String processLyricLine(LrcLyric lrcLyricLine, List<WhisperSegment> submitLyrics, int index) {
        String lyricLineText = lrcLyricLine.getText();
        
        if (index < submitLyrics.size()) {
            WhisperSegment submitLyricLine = submitLyrics.get(index);
            List<String> wrongWords = gradeByLine(lrcLyricLine.getText(), submitLyricLine.getText());
            return markWrongWords(lyricLineText, wrongWords);
        }
        
        return markAllWords(lyricLineText);
    }

    private String markWrongWords(String text, List<String> wrongWords) {
        String result = text;
        for (String wrongWord : wrongWords) {
            result = result.replaceAll("(?i)\\b" + wrongWord + "\\b", "__" + wrongWord);
        }
        return result;
    }

    private String markAllWords(String text) {
        return text.replaceAll("\\b(\\w+)", "__$1");
    }

    private List<String> gradeByLine(String answerTextLine, String submitTextLine) {
        try {
            Set<String> answerWords = getTokens(answerTextLine);
            Set<String> submitWords = getTokens(submitTextLine);

            Set<String> difference = new HashSet<>(answerWords);
            difference.removeAll(submitWords);
            return new ArrayList<>(difference);
        } catch (Exception e) {
            log.error("Error in gradeByLine: ", e);
            throw new CustomException(ErrorCode.SPEAKING_GRADING_ERROR);
        }
    }

    private File audioPreprocess(SpeakingSubmitRequest submitRequest) {
        MultipartFile multipartFile = submitRequest.getFile();
        List<LrcLyric> lrcLyrics = submitRequest.getLyricList();
        Path directory = Paths.get(AUDIO_PATH).toAbsolutePath();

        try {
            File originalFile = new File(directory + File.separator + UUID.randomUUID());
            multipartFile.transferTo(originalFile);

            // FFmpeg을 사용하여 오디오 파일을 WAV 형식으로 변환
            FFmpeg ffmpeg = new FFmpeg(new ClassPathResource(ffmpegPath).getURL().getPath());
            FFprobe ffprobe = new FFprobe(new ClassPathResource(ffprobePath).getURL().getPath());

            File convertedFile = new File(directory + File.separator + UUID.randomUUID() + ".wav");
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(originalFile.getAbsolutePath())
                    .addOutput(convertedFile.getAbsolutePath())
                    .setAudioCodec("pcm_s16le") // WAV 형식 오디오 코덱
                    .setFormat("wav")
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();

            // 변환된 오디오 파일을 사용하여 전처리 시작
            AudioFormat audioFormat;
            try (AudioInputStream tempStream = AudioSystem.getAudioInputStream(convertedFile)) {
                audioFormat = tempStream.getFormat();
            } catch (Exception e) {
                throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
            }

            List<AudioInputStream> audioParts = new ArrayList<>();
            File silenceFile = new File(directory + File.separator + "silence.wav");
            AudioInputStream silenceStream = AudioSystem.getAudioInputStream(silenceFile);
            byte[] silenceData = new byte[(int) silenceStream.getFrameLength() * silenceStream.getFormat().getFrameSize()];
            silenceStream.read(silenceData);

            for (LrcLyric lrcLyric : lrcLyrics) {
                AudioInputStream originalStream = AudioSystem.getAudioInputStream(convertedFile);
                long framesToSkip = lrcLyric.getStartMs() * (long) (audioFormat.getFrameRate() / 1000);
                originalStream.skip(framesToSkip * audioFormat.getFrameSize());

                long framesOfAudioToCopy = (long) ((lrcLyric.getDurMs()) * (audioFormat.getFrameRate() / 1000));
                AudioInputStream shortenedStream = new AudioInputStream(originalStream, audioFormat, framesOfAudioToCopy);
                ByteArrayInputStream silenceBAISForThisLoop = new ByteArrayInputStream(silenceData);
                AudioInputStream silenceAudioStreamForThisLoop = new AudioInputStream(silenceBAISForThisLoop, audioFormat, silenceData.length / audioFormat.getFrameSize());

                audioParts.add(shortenedStream);
                audioParts.add(silenceAudioStreamForThisLoop);
            }

            // 오디오 스트림 병합
            AudioInputStream concatenatedStream = createSequenceAudioInputStream(audioFormat, audioParts);

            // 최종 오디오 파일 저장
            File preProcessingAudio = new File(directory + File.separator + "pre" + UUID.randomUUID() + ".wav");
            AudioSystem.write(concatenatedStream, AudioFileFormat.Type.WAVE, preProcessingAudio);

            return preProcessingAudio;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.AUDIO_PRE_PROCESSING_ERROR);
        }
    }

    private AudioInputStream createSequenceAudioInputStream(AudioFormat audioFormat, List<AudioInputStream> audioInputStreams) {
        Vector<InputStream> inputStreams = new Vector<>();
        for (AudioInputStream stream : audioInputStreams) {
            inputStreams.add(stream);
        }
        return new AudioInputStream(new SequenceInputStream(Collections.enumeration(inputStreams)), audioFormat, -1);
    }

    private record GradingResult(double allTokenSize, double wrongTokenSize, String answerSheet) {}
}
