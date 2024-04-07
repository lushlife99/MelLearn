import React, { useEffect, useState } from "react";
import { axiosSpotify, axiosSpotifyScraper } from "../api";
import { useLocation, useNavigate } from "react-router-dom";
import {
  FaPlay,
  FaPause,
  FaStepBackward,
  FaStepForward,
  FaPlayCircle,
  FaPauseCircle,
} from "react-icons/fa";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import { IoIosArrowRoundBack, IoIosArrowUp } from "react-icons/io";
import { LuPencilLine } from "react-icons/lu";
import { FaMicrophoneLines } from "react-icons/fa6";

export interface CurrentTimeData {
  progress_ms: number;
}

function PlayMusic() {
  const location = useLocation();
  const [isPlaying, setIsPlaying] = useState<boolean>();

  const navigate = useNavigate();
  const { track } = location.state;

  //메타 데이터 가져오기
  const { trackMetaData } = useSelector((state: RootState) => state.trackMeta);

  //재생

  const play = async () => {
    console.log("새로운 재생", track.id);
    const playbackState = await axiosSpotify.get("/me/player"); //현재 재생 위치 확인
    const res2 = await axiosSpotify.get("/me/player/currently-playing");

    let progress_ms = 0;
    if (res2.data.item === undefined) {
      console.log("success");
      progress_ms = 0;
    } else {
      if (track.id === res2.data.item.id) {
        progress_ms = playbackState.data.progress_ms;
      } else {
        progress_ms = 0;
      }
    }

    const res = await axiosSpotify.put("/me/player/play", {
      uris: ["spotify:track:" + track.id],
      position_ms: progress_ms,
    });

    if (res.status === 202) {
      startProgressBar();
      setIsPlaying(true);
    }
  };

  //정지
  const pause = async () => {
    const res = await axiosSpotify.put("/me/player/pause");

    if (res.status === 202) {
      setIsPlaying(false);
      stopProgressBar();
    }
  };

  //이전 재생
  const playPrevious = async () => {
    const res = await axiosSpotify.post("/me/player/previous");
    console.log("이전", res.status);
  };

  //다음재생
  const playNext = async () => {
    const res = await axiosSpotify.post("/me/player/next");
    console.log("다음", res.data);
  };
  const goBack = () => {
    navigate(-1); //뒤로가기
  };
  const [currentTime, setCurrentTime] = useState<number>(0);
  const [intervalId, setIntervalId] =
    useState<ReturnType<typeof setInterval>>();
  const startProgressBar = () => {
    const interval = setInterval(() => {
      setCurrentTime((prevTime) => prevTime + 1000);
      if (currentTime === duration) {
        clearInterval(intervalId);
      }
    }, 1000);
    setIntervalId(interval);
  };
  const [duration, setDuration] = useState<number>(0);
  useEffect(() => {
    setDuration(track.duration_ms);
  }, []);

  const stopProgressBar = () => {
    clearInterval(intervalId);
  };
  const progressPercentage = (currentTime / duration) * 100;

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="relative bg-[black] overflow-hidden w-full max-w-[450px] h-screen  flex flex-col px-5">
        <IoIosArrowRoundBack
          onClick={goBack}
          className="fill-[white] w-10 h-10 mt-8 hover:opacity-60"
        />
        {/* 앪범 커버 */}
        <div className="flex items-center justify-center mt-4 ">
          <img
            src={track.album.images[0].url}
            alt="Album Cover"
            className="rounded-lg w-84 h-84"
          />
        </div>

        {/* 트랙 명 */}
        <span className="text-[white]  text-3xl font-bold mt-4 mb-1">
          {track.name}
        </span>

        {/* 아티스트 이름*/}
        <div>
          {track.artists.map((artist: any, index: number) => (
            <span key={index} className="text-[#B3B3B3] text-left text-lg ">
              {artist.name}
            </span>
          ))}
        </div>

        {/* progress Bar */}
        <div className="w-full h-2 mt-12 bg-gray-200 rounded-full">
          <div
            className={`h-full bg-green-500 rounded-full transition-all duration-500 ease-in-out`}
            style={{ width: `${progressPercentage}%` }}
          ></div>
        </div>

        {/** 재생 시간*/}
        <div className="flex justify-between mt-1">
          <span className="text-[#B3B3B3]">
            {Math.floor(currentTime / 1000 / 60)}:
            {Math.floor((currentTime / 1000) % 60) < 10
              ? `0${Math.floor((currentTime / 1000) % 60)}`
              : Math.floor((currentTime / 1000) % 60)}
          </span>
          <span className=" text-[#B3B3B3]">
            {Math.floor(track.duration_ms / 1000 / 60)}:
            {Math.floor((track.duration_ms / 1000) % 60) < 10
              ? `0${Math.floor((track.duration_ms / 1000) % 60)}`
              : Math.floor((track.duration_ms / 1000) % 60)}
          </span>
        </div>
        {/* 버튼 div */}
        <div className="flex justify-between mt-12">
          <button className="bg-[#D3D3D3] rounded-2xl h-9 w-28 flex items-center justify-center hover:opacity-60">
            <LuPencilLine />
            문제풀기
          </button>
          <div className="flex flex-col items-center">
            <div className=" flex flex-col justify-center items-center text-[#d3d3d3] hover:opacity-60">
              <IoIosArrowUp />
              <span>가사</span>
            </div>
          </div>
          <button className="bg-[#D3D3D3] rounded-2xl h-9 w-28 flex items-center justify-center hover:opacity-60">
            <FaMicrophoneLines />
            따라부르기
          </button>
        </div>

        {/*아이콘 */}
        <div className="flex items-center justify-around mt-24">
          <FaStepBackward
            onClick={playPrevious}
            className="fill-[white] w-8 h-8 hover:opacity-60"
          />
          {!isPlaying ? (
            <FaPlayCircle
              className="fill-[white] w-16 h-16 hover:opacity-60"
              onClick={play}
            />
          ) : (
            <FaPauseCircle
              className="fill-[white] w-16 h-16 hover:opacity-60"
              onClick={pause}
            />
          )}
          <FaStepForward
            onClick={playNext}
            className="fill-[white] w-8 h-8 hover:opacity-60"
          />
        </div>
      </div>
    </div>
  );
}

export default PlayMusic;
