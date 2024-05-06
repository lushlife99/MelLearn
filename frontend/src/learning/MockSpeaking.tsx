import React, { useEffect, useRef, useState } from "react";
import axiosApi, { axiosSpotify, axiosSpotifyScraper } from "../api";
import { useQuery } from "react-query";
import Lyric from "../musichome/Lyric";
import { LyricData } from "../redux/type";
import { IoMdMicrophone } from "react-icons/io";
import { useDispatch } from "react-redux";
import { setSpeakingData } from "../redux/mockSpeaking/mockSpeakingSlice";
interface ITrack {
  album: {
    images: {
      url: string;
    }[];
  };
  artists: {
    id: string;
    name: string;
  }[];
  is_playable: boolean;
  name: string;
  type: string;
  uri: string;
  duration_ms: number;
  id: string;
}
interface Track {
  track: ITrack;
  formData: FormData;
}

function MockSpeaking({ track, formData }: Track) {
  const mediaStream = useRef<MediaStream | null>(null);
  const mediaRecorder = useRef<MediaRecorder | null>(null);
  const chunks = useRef<Blob[]>([]);
  const [currentTime, setCurrentTime] = useState<number>(0);
  const [duration, setDuration] = useState<number>(0);
  const [intervalId, setIntervalId] =
    useState<ReturnType<typeof setInterval>>();

  const dispatch = useDispatch();
  const getLyric = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}&format=json`
    );
    return res.data;
  };

  const { data: lyricData, isLoading: lyricLoading } = useQuery<LyricData[]>(
    ["lyric", track.id],
    () => getLyric(),
    {
      staleTime: 10800000,
    }
  );

  const startProgressBar = () => {
    const interval = setInterval(() => {
      setCurrentTime((prevTime) => prevTime + 1000);
    }, 1000);
    setIntervalId(interval);
  };

  const playMusic = async () => {
    const res = await axiosSpotify.put("/me/player/play", {
      uris: ["spotify:track:" + track.id],
      position_ms: 0,
    });
    if (res.status === 202) {
      startProgressBar();
      // 노래 한곡 재생 데이터 보내주기
      //노래 끝나면 녹음 중지하면서 서버에 요청
    }
  };
  const pause = async () => {
    const res = await axiosSpotify.put("/me/player/pause");
  };
  const accessMicrophone = async () => {
    try {
      // mic 접근 권한 허용
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      // true 되면 실행

      if (stream.active) {
        playMusic(); // 마이크 연결 될시 음악 실행
        mediaStream.current = stream;
        mediaRecorder.current = new MediaRecorder(stream);
        mediaRecorder.current.ondataavailable = (e) => {
          const wavAudioData = new Blob([e.data]);
          chunks.current = [];
          chunks.current.push(wavAudioData);
        };
        mediaRecorder.current.start();
        mediaRecorder.current.onstop = async () => {
          // 노래 끝나고 서버에 speaking data 보냄
          const recordedBlob = new Blob(chunks.current, { type: "audio/wav" });
          //const url = URL.createObjectURL(recordedBlob);
          //const formData = new FormData();
          formData.append(`speakingSubmit`, recordedBlob);
          const lyricsBlob = new Blob([JSON.stringify(lyricData)], {
            type: "application/json",
          });
          formData.append("lyricList", lyricsBlob, "lyricList.json");
          const musicId = new Blob([track.id], {
            type: "text/plain",
          });

          formData.append("musicId", musicId, "musicId.json");
          console.log(formData);

          //   const res = await axiosApi.post(
          //     "/api/problem/speaking/transcription",
          //     formData,
          //     {
          //       headers: {
          //         "Content-Type": "multipart/form-data",
          //       },
          //     }
          //   ); // 파일명은 선택사항
        };
      }
    } catch (error) {
      alert("마이크를 연결해주세요");
    }
  };
  const stopRecording = async () => {
    if (mediaRecorder.current && mediaRecorder.current.state === "recording") {
      mediaRecorder.current.stop();
      pause();
    }
    if (mediaStream.current) {
      mediaStream.current.getTracks().forEach((track) => {
        track.stop();
      });
    }
  };

  useEffect(() => {
    if (currentTime >= duration) {
      //노래 종료
      setCurrentTime(0);
      stopRecording();
      clearInterval(intervalId);
    }
  }, [currentTime, duration]);
  useEffect(() => {
    setDuration(track.duration_ms);
    return () => {
      pause(); // 컴포넌트 언마운트시 음악 정지
    };
  }, []);

  return (
    <div className="h-[80%]">
      <div className="flex flex-col items-start h-[15%]">
        <div className="flex items-center w-full h-16 p-2 my-2 bg-white border border-black rounded-2xl">
          <img
            src={track.album.images[2].url}
            alt="Album Cover"
            className="w-12 h-12 rounded-xl"
          />
          <div className="flex flex-col justify-start ml-4">
            <span className="text-sm font-bold">{track.name}</span>
            <span className="text-sm">{track.artists[0].name}</span>
          </div>
          <div className="ml-44 hover:opacity-60">
            <IoMdMicrophone className="w-8 h-8" onClick={accessMicrophone} />
          </div>
        </div>
      </div>
      <span className="font-bold">26.</span>
      <div className="h-full p-3 overflow-y-auto leading-[normal] border border-black scrollbarwhite">
        {lyricData?.map((lyric, index) => (
          <div key={index}>
            <p
              className={`text-${
                currentTime >= lyric.startMs &&
                currentTime <= lyric.startMs + lyric.durMs
                  ? "#B3B3B3"
                  : "black"
              } hover:opacity-60 text-2xl font-semibold`}
            >
              {lyric.text}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MockSpeaking;
