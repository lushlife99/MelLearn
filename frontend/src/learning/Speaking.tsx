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
  async function convertToWav(blob: Blob): Promise<Blob> {
    const audioContext = new AudioContext();
    const reader = new FileReader();

    return new Promise((resolve, reject) => {
      reader.onload = function () {
        audioContext.decodeAudioData(
          reader.result as ArrayBuffer,
          function (decodedData) {
            const offlineAudioContext = new OfflineAudioContext({
              numberOfChannels: 1,
              length: decodedData.length,
              sampleRate: decodedData.sampleRate,
            });
            const source = offlineAudioContext.createBufferSource();
            source.buffer = decodedData;

            offlineAudioContext.oncomplete = function (event) {
              const wavBuffer = event.renderedBuffer;

              // 'AudioBuffer'를 'Uint8Array'로 변환
              const wavData = new Uint8Array(
                wavBuffer.length * wavBuffer.numberOfChannels * 2
              );
              let offset = 0;
              for (
                let channel = 0;
                channel < wavBuffer.numberOfChannels;
                channel++
              ) {
                const channelData = wavBuffer.getChannelData(channel);
                for (let i = 0; i < channelData.length; i++) {
                  const sample = Math.max(-1, Math.min(1, channelData[i]));
                  wavData[offset++] =
                    sample < 0 ? sample * 0x8000 : sample * 0x7fff;
                }
              }

              const wavBlob = new Blob([wavData], { type: "audio/wav" });
              resolve(wavBlob);
            };

            source.connect(offlineAudioContext.destination);
            source.start();
            offlineAudioContext.startRendering();
          }
        );
      };

      reader.onerror = function (event: any) {
        reject(event.target.error);
      };

      reader.readAsArrayBuffer(blob);
    });
  }

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
        const recordedBlob = new Blob(chunks.current, { type: "audio/wav" });
        const wavBlob = await convertToWav(recordedBlob);
        const url = URL.createObjectURL(wavBlob);
        setRecordedUrl(url);

        // 녹음 파일 임시 다운로드
        const a = document.createElement("a");
        document.body.appendChild(a);
        a.href = url;
        a.download = "recorded_audio.wav";
        a.click();
        window.URL.revokeObjectURL(url);
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
      <button onClick={playMusic}>노래 재생</button>
      <button onClick={pause}>정지</button>
    </div>
  );
};

export default Speaking;
