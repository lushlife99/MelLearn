import React, { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import axiosApi, { axiosSpotify, axiosSpotifyScraper } from "../api";
import axios from "axios";

interface Window {
  webkitAudioContext: typeof AudioContext;
}

const Speaking = () => {
  const [recordedUrl, setRecordedUrl] = useState("");
  const mediaStream = useRef<MediaStream | null>(null);
  const mediaRecorder = useRef<MediaRecorder | null>(null);
  const chunks = useRef<Blob[]>([]);
  const location = useLocation();
  const { track } = location.state;
  console.log(track);

  const [test, setTest] = useState();

  const getLyric = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}&format=json`
    );

    setTest(res.data); // 가사 보낼용 -> 이걸로 사용
  };
  useEffect(() => {
    getLyric();
  }, []);
  const [id, setId] = useState();

  const convert = async () => {
    const res = await axios.get(`https://api.convertio.co/convert/${id}/dl`);
    console.log(res.data);
  };
  const convertStatus = async () => {
    const res = await axios.get(
      `https://api.convertio.co/convert/${id}/status`
    );
    console.log("상태", res.data.data.step);
  };
  const convertWebMToWAV = async (audioUrl: string) => {
    console.log(audioUrl);
    try {
      const requestBody = {
        apikey: "774b220833b0a6c13636283904a1ab93",
        input: "raw",
        file: audioUrl,
        filename: "audio.webm",
        outputformat: "wav",
      };

      const response = await axios.post(
        "https://api.convertio.co/convert",
        requestBody
      );
      //console.log("a", response.data.data.id);
      setId(response.data.data.id);

      // const downloadUrl = response.data.data.output.url;
      // const wavFile = await axios.get(downloadUrl, { responseType: "blob" });
      // console.log("c", wavFile.data);
      // return wavFile.data;
    } catch (error) {
      console.error("Error converting to WAV:", error);
      throw error;
    }
  };

  const accessMicrophone = async () => {
    try {
      // mic 접근 권한 허용
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaStream.current = stream;
      mediaRecorder.current = new MediaRecorder(stream);
      mediaRecorder.current.ondataavailable = (e) => {
        const wavAudioData = new Blob([e.data]);
        chunks.current = [];
        chunks.current.push(wavAudioData);
      };
      mediaRecorder.current.onstop = async () => {
        const recordedBlob = new Blob(chunks.current, { type: "audio/webm" });
        //const wav = await convertWebMToWAV(recordedBlob);
        const url = URL.createObjectURL(recordedBlob);
        await convertWebMToWAV(url);
        // setRecordedUrl(url);
        // // 녹음 파일 임시 다운로드
        // const a = document.createElement("a");
        // document.body.appendChild(a);
        // a.href = url;
        // a.download = "recorded_audio.wav";
        // a.click();
        // window.URL.revokeObjectURL(url);
      };
      mediaRecorder.current.start();
    } catch (error) {
      console.error("마이크 접근 에러", error);
    }
  };

  const stopRecording = async () => {
    /*  테스트용 */
    const formData = new FormData();
    const recordedBlob = new Blob(chunks.current, { type: "audio/wav" });

    formData.append(`file`, recordedBlob);
    const lyricsBlob = new Blob([JSON.stringify(test)], {
      type: "application/json",
    });
    formData.append("lyricList", lyricsBlob, "lyricList.json"); // 파일명은 선택사항

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
  const handleFileUpload = async (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      const formData = new FormData();
      formData.append("file", file);
      const lyricsBlob = new Blob([JSON.stringify(test)], {
        type: "application/json",
      });
      formData.append("lyricList", lyricsBlob, "lyricList.json");
      const musicId = new Blob([JSON.stringify(track.id)], {
        type: "application/json",
      });
      formData.append("musicId", musicId, "musicId.json");
      const res = await axiosApi.post(
        "/api/problem/speaking/transcription",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      console.log(res.data);
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
      <input type="file" onChange={handleFileUpload} />
      <button className="bg-[blue]" onClick={accessMicrophone}>
        녹음 시작
      </button>
      <button className="bg-[blue]" onClick={stopRecording}>
        녹음 중지
      </button>
      <button onClick={convertStatus}>상태</button>
      <button onClick={convert}>변환</button>

      <button onClick={playMusic}>노래 재생</button>
      <button onClick={pause}>정지</button>
    </div>
  );
};

export default Speaking;
