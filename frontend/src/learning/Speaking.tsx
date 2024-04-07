import React, { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import axiosApi, { axiosSpotify, axiosSpotifyScraper } from "../api";
import axios from "axios";

interface Lyric {
  time: number;
  lyric: string;
}

const Speaking = () => {
  const [recordedUrl, setRecordedUrl] = useState("");
  const mediaStream = useRef<MediaStream | null>(null);
  const mediaRecorder = useRef<MediaRecorder | null>(null);
  const chunks = useRef<Blob[]>([]);
  const location = useLocation();
  const { track } = location.state;
  const [lyricData, setLyricData] = useState<Lyric[]>([]);

  const [stop, setStop] = useState(false);

  const [test, setTest] = useState();

  const timeStringToMs = (timeString: string) => {
    const [min, sec] = timeString.split(":");
    const [seconds, milliseconds] = sec.split(".");
    return (
      parseInt(min) * 60 * 1000 +
      parseInt(seconds) * 1000 +
      parseInt(milliseconds)
    );
  };

  const getLyric = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}&format=json`
    );

    setTest(res.data); // 가사 보낼용 -> 이걸로 사용
    // const lines = res.data.split("\n");
    // const lyricArr: Lyric[] = [];
    // lines.forEach((line: string) => {
    //   const match = line.match(/^\[(\d{2}:\d{2}\.\d{2})\](.*)$/);
    //   if (match) {
    //     const time = timeStringToMs(match[1]);
    //     const lyric = match[2];
    //     lyricArr.push({ time, lyric });

    //     //console.log(`Time: ${timeStringToMs(time)}, Lyrics: ${lyrics}`);
    //   }
    // });
    // setLyricData(lyricArr);
  };
  useEffect(() => {
    getLyric();
  }, []);

  const accessMicrophone = async () => {
    try {
      // mic 접근 권한 허용
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaStream.current = stream;
      mediaRecorder.current = new MediaRecorder(stream);
      mediaRecorder.current.ondataavailable = (e) => {
        const wavAudioData = new Blob([e.data], {
          type: "audio/wav",
        });
        chunks.current = [];
        chunks.current.push(wavAudioData);
      };
      mediaRecorder.current.onstop = () => {
        const recordedBlob = new Blob(chunks.current);
        const url = URL.createObjectURL(recordedBlob);
        setRecordedUrl(url);

        // 녹음 파일 임시 다운로드
        const a = document.createElement("a");
        document.body.appendChild(a);
        a.href = url;
        a.download = "recorded_audio.wav";
        a.click();
        window.URL.revokeObjectURL(url);
        //
      };
      mediaRecorder.current.start();
    } catch (error) {
      console.error("마이크 접근 에러", error);
    }
  };
  // const lyricList = [{ startMs: 0, durMs: 1000, text: "Sample text" }];
  // console.log(typeof lyricList, lyricList);
  // console.log(typeof test, test);

  const stopRecording = async () => {
    /*  테스트용 */
    const formData = new FormData();
    const recordedBlob = new Blob(chunks.current, { type: "audio/wav" });

    formData.append(`file`, recordedBlob);
    const lyricsBlob = new Blob([JSON.stringify(test)], {
      type: "application/json",
    });
    formData.append("lyricList", lyricsBlob, "lyricList.json"); // 파일명은 선택사항

    //formData.append("lyricList", JSON.stringify(test));

    if (mediaRecorder.current && mediaRecorder.current.state === "recording") {
      mediaRecorder.current.stop();
      // const res = await axiosApi.post(
      //   "/api/problem/speaking/transcription",
      //   formData,
      //   {
      //     headers: {
      //       "Content-Type": "multipart/form-data",
      //     },
      //   }
      // );
    }
    if (mediaStream.current) {
      mediaStream.current.getTracks().forEach((track) => {
        track.stop();
      });
    }
  };

  const playMusic = async () => {
    const res = await axiosSpotify.put("/me/player/play", {
      uris: ["spotify:track:" + track.id],
    });

    if (res.status === 202) {
      accessMicrophone();
      // 노래 한곡 재생 데이터 보내주기
      //노래 끝나면 녹음 중지하면서 서버에 요청
    }
  };
  //test용 정지
  const pause = async () => {
    const res = await axiosSpotify.put("/me/player/pause");
    if (res.status === 202) {
      stopRecording();
    }
  };

  return (
    <div>
      <audio controls src={recordedUrl} />
      <button className="bg-[blue]" onClick={accessMicrophone}>
        녹음 시작
      </button>
      <button className="bg-[blue]" onClick={stopRecording}>
        녹음 중지
      </button>
      <button onClick={playMusic}>노래 재생</button>
      <button onClick={pause}>정지</button>
    </div>
  );
};

export default Speaking;
