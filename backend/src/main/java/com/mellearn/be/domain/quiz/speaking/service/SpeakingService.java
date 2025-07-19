package com.mellearn.be.domain.quiz.speaking.service;

import com.mellearn.be.api.feign.openai.service.OpenAIService;
import com.mellearn.be.api.feign.openai.dto.response.WhisperSegment;
import com.mellearn.be.api.feign.openai.dto.response.WhisperTranscriptionResponse;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.music.dto.LrcLyric;
import com.mellearn.be.domain.music.dto.MusicDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitRequest;
import com.mellearn.be.domain.quiz.speaking.entity.SpeakingSubmit;
import com.mellearn.be.domain.quiz.speaking.repository.SpeakingSubmitRepository;
import com.mellearn.be.domain.support.service.SupportService;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SpeakingService {

    @Autowired
    private SpeakingSubmitRepository speakingSubmitRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SupportService supportService;

    @Autowired
    private OpenAIService openAIService;

    @Autowired(required = false)
    private FFmpeg ffmpeg;

    @Autowired(required = false)
    private FFprobe ffprobe;


    private static final String AUDIO_PATH = "." + File.separator + "audio" + File.separator;

    @Async
    public CompletableFuture<SpeakingSubmitDto> submit(SpeakingSubmitRequest submitRequest, String musicId, String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        List<LrcLyric> lyricList = submitRequest.getLyricList();
        MusicDto musicDto = supportService.getSupportQuizCategory(musicId, lyricList, memberId);
        if (!musicDto.isSpeaking())
            throw new CustomException(ErrorCode.UN_SUPPORTED_QUIZ_LANG);

        // 1. 전처리 1 - audio 파일을 lrc파일에 맞게 분할하고 사이에 temp audio 삽입
        File preprocessingAudio = audioPreprocess(submitRequest);

        // 2. whisper 전송
        WhisperTranscriptionResponse transcription = openAIService.createTranscription(preprocessingAudio, member.getLangType().getIso639Value());


        // 3. 후처리 2 - 세그먼트와 lyric 줄 싱크 맞추기, 대소문자 변경, 특수문자 제거
        //List<WhisperSegment> whisperSegments = synchronizeLyricsAndSegments(lyricList, transcription);
        convertGradableFormat(lyricList, transcription);

        // 4. 채점
        SpeakingSubmitDto answerSpeaking = grade(musicId, lyricList, member, transcription);

        // 5. 랭킹
        return CompletableFuture.completedFuture(answerSpeaking);
    }

    private void convertGradableFormat(List<LrcLyric> answerLyrics, WhisperTranscriptionResponse transcription) {
        convertLyricToLowerCase(answerLyrics, transcription.getSegments());
        removeSpecialChar(answerLyrics, transcription.getSegments());
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

    public SpeakingSubmitDto grade(String musicId, List<LrcLyric> answerLyrics, Member member, WhisperTranscriptionResponse transcriptionResponse) {
        List<WhisperSegment> submitLyrics = transcriptionResponse.getSegments();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        double allTokenSize = 0;
        double wrongTokenSize = 0;
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

            if (i < submitLyrics.size()) {

                WhisperSegment submitLyricLine = submitLyrics.get(i);
                List<String> wrongWords = gradeByLine(lrcLyricLine.getText(), submitLyricLine.getText());
                wrongTokenSize += wrongWords.size();

                for (String wrongWord : wrongWords) {
                    lyricLineText = lyricLineText.replaceAll("(?i)\\b" + wrongWord + "\\b", "__" + wrongWord);
                }
                answerSheet.append(lyricLineText + "\n");

            } else {
                answerSheet.append(lyricLineText.replaceAll("\\b(\\w+)", "__$1"));
            }

            // 사전에 등재되어 있지 않은 단어나 수사, 감탄사 로직 추가. (아직 사전 api 허가 못받음)

        }

        transcriptionResponse.getText().replaceAll(". ", ".\n");

        SpeakingSubmit speakingSubmit = SpeakingSubmit.builder()
                .musicId(musicId)
                .markedText(answerSheet.toString())
                .submit(transcriptionResponse.getText())
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
            AudioInputStream concatenatedStream = new SequenceAudioInputStream(audioFormat, Arrays.stream(audioParts.stream().toArray(AudioInputStream[]::new)).toList());

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
