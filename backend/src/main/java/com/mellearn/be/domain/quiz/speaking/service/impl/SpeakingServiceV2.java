package com.mellearn.be.domain.quiz.speaking.service.impl;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.music.dto.LrcLyric;
import com.mellearn.be.domain.music.dto.MusicDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitRequest;
import com.mellearn.be.domain.quiz.speaking.entity.SpeakingSubmit;
import com.mellearn.be.domain.quiz.speaking.repository.SpeakingSubmitRepository;
import com.mellearn.be.domain.quiz.speaking.service.SpeakingService;
import com.mellearn.be.domain.support.service.SupportService;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.ai.audio.transcription.*;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingServiceV2 implements SpeakingService {

    private static final String AUDIO_PATH = "." + File.separator + "audio" + File.separator;

    private final SpeakingSubmitRepository speakingSubmitRepository;
    private final MemberRepository memberRepository;
    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    private final SupportService supportService;

    @Autowired(required = false)
    private FFmpeg ffmpeg;
    @Autowired(required = false)
    private FFprobe ffprobe;

    @Async
    @Override
    public CompletableFuture<SpeakingSubmitDto> submit(SpeakingSubmitRequest submitRequest, String musicId, String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        List<LrcLyric> lyricList = submitRequest.getLyricList();
        MusicDto musicDto = supportService.getSupportQuizCategory(musicId, lyricList, memberId);
        if (!musicDto.isSpeaking())
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);

        // 1. 전처리 1 - audio 파일을 lrc파일에 맞게 분할하고 사이에 temp audio 삽입
        File preprocessingAudio = audioPreprocess(submitRequest);

        // 2. whisper 전송
        List<String> resultSegments = transcribeWithSpringAi(preprocessingAudio, member.getLangType().getIso639Value());

        // 3. 후처리 2 - 세그먼트와 lyric 줄 싱크 맞추기, 대소문자 변경, 특수문자 제거
        //List<WhisperSegment> whisperSegments = synchronizeLyricsAndSegments(lyricList, transcription);
        convertGradableFormat(lyricList, resultSegments);

        // 4. 채점
        SpeakingSubmitDto answerSpeaking = grade(musicId, lyricList, member, resultSegments);

        // 5. 랭킹
        return CompletableFuture.completedFuture(answerSpeaking);
    }

    private void convertGradableFormat(List<LrcLyric> answerLyrics, List<String> segments) {

        convertLyricToLowerCase(answerLyrics, segments);
        removeSpecialChar(answerLyrics, segments);
    }

    private void removeSpecialChar(List<LrcLyric> answerLyrics, List<String> segments) {
        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            lrcLyricLine.setText(lrcLyricLine.getText().replaceAll("[^a-zA-Z0-9\\s]", ""));
        }

        List<String> converts = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            converts.add(segments.get(i).replaceAll("[^a-zA-Z0-9\\s]", ""));
        }

        segments = converts;
    }

    private void convertLyricToLowerCase(List<LrcLyric> answerLyrics, List<String> segments) {

        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            lrcLyricLine.setText(lrcLyricLine.getText().toLowerCase());
        }

        List<String> converts = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            converts.add(segments.get(i).toLowerCase());
        }

        segments = converts;
    }

    private SpeakingSubmitDto grade(String musicId, List<LrcLyric> answerLyrics, Member member, List<String> segments) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        double allTokenSize = 0;
        double wrongTokenSize = 0;
        StringBuilder answerSheet = new StringBuilder();

        for (int i = 0; i < answerLyrics.size(); i++) {
            LrcLyric lrcLyricLine = answerLyrics.get(i);
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            CoreDocument doc = new CoreDocument(lrcLyricLine.getText());
            pipeline.annotate(doc);

            Set<String> allTokens = new HashSet<>();
            for (CoreLabel token : doc.tokens()) {
                String word = token.word();
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // 수사(CD), 감탄사(UH) 제외
                if (!"CD".equals(pos) && !"UH".equals(pos)) {
                    allTokens.add(word);
                }
            }

            allTokenSize += allTokens.size();
            String lyricLineText = lrcLyricLine.getText();

            if (i < segments.size()) {
                List<String> wrongWords = gradeByLine(lrcLyricLine.getText(), segments.get(i))
                        .stream()
                        .filter(wrong -> allTokens.contains(wrong)) // 수사/감탄사 제외
                        .collect(Collectors.toList());

                wrongTokenSize += wrongWords.size();

                for (String wrongWord : wrongWords) {
                    lyricLineText = lyricLineText.replaceAll("(?i)\\b" + wrongWord + "\\b", "__" + wrongWord);
                }
                answerSheet.append(lyricLineText).append("\n");
            } else {
                answerSheet.append(lyricLineText.replaceAll("\\b(\\w+)", "__$1"));
            }
        }


        String result = segments.stream()
                .map(s -> s.replaceAll("\\. ", ".\n")) // 각 문장 내에서 ". " → ".\n"
                .collect(Collectors.joining("\n"));     // 각 줄을 \n으로 이어붙임

        SpeakingSubmit speakingSubmit = SpeakingSubmit.builder()
                .musicId(musicId)
                .markedText(answerSheet.toString())
                .submit(result)
                .score(100.0 - (wrongTokenSize / allTokenSize * 100.0))
                .member(member)
                .build();


        member.setLevelPoint(member.getLevelPoint() + (int) speakingSubmit.getScore());
        speakingSubmitRepository.save(speakingSubmit);
        memberRepository.save(member);
        return new SpeakingSubmitDto(speakingSubmit);
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

        Set<String> difference = new HashSet<>(words1);
        difference.removeAll(words2);
        return difference.stream().toList();
    }


    private File audioPreprocess(SpeakingSubmitRequest submitRequest) {
        MultipartFile multipartFile = submitRequest.getFile();
        List<LrcLyric> lrcLyrics = submitRequest.getLyricList();
        Path directory = Paths.get(AUDIO_PATH).toAbsolutePath();


        try {
            File originalFile = new File(directory + File.separator + UUID.randomUUID());
            multipartFile.transferTo(originalFile);

            // FFmpeg을 사용하여 오디오 파일을 WAV 형식으로 변환


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
            AudioInputStream concatenatedStream = new SpeakingServiceV2.SequenceAudioInputStream(audioFormat, Arrays.stream(audioParts.stream().toArray(AudioInputStream[]::new)).toList());

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

    private List<String> transcribeWithSpringAi(File audioFile, String languageCode) {

        AudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language(languageCode)
                .build();

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new FileSystemResource(audioFile), options);
        AudioTranscriptionResponse response = audioTranscriptionModel.call(prompt);

        return response.getResults()
                .stream()
                .map(AudioTranscription::getOutput)
                .collect(Collectors.toList());
    }

}
