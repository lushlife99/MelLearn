import React, { useEffect, useState } from "react";
import { axiosSpotify, axiosSpotifyScraper } from "../api";
import { useLocation, useNavigate } from "react-router-dom";
import { FaPlay, FaPause, FaStepBackward, FaStepForward } from "react-icons/fa";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";

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

    const existingPlaying = res2.data.item.id;
    //새로운 음악 재생시 새로운 재생
    //const progress_ms = 0;
    const progress_ms =
      track.id === existingPlaying ? playbackState.data.progress_ms : 0;
    const res = await axiosSpotify.put("/me/player/play", {
      uris: ["spotify:track:" + track.id],
      position_ms: progress_ms,
    });

    if (res.status === 202) {
      setIsPlaying(true);
    }
  };

  //정지
  const pause = async () => {
    const res = await axiosSpotify.put("/me/player/pause");

    if (res.status === 202) {
      setIsPlaying(false);
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

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="relative bg-[black] overflow-hidden w-full max-w-[450px] h-screen  flex flex-col px-3">
        <span className="text-[white] text-center text-3xl font-bold mt-12">
          {track.name}
        </span>
        <div className="flex items-center justify-center mt-16 ">
          <img
            src={track.album.images[0].url}
            alt="Album Cover"
            className="rounded-lg w-80 h-80"
          />
        </div>
        <div className="border border-white">
          {track.artists.map((artist: any, index: number) => (
            <span
              key={index}
              className="text-[white] text-left text-3xl font-bold"
            >
              {artist.name}
            </span>
          ))}
        </div>
        <div className="flex justify-around mt-72">
          <FaStepBackward
            onClick={playPrevious}
            className="fill-[white] w-10 h-10 hover:opacity-60"
          />
          {!isPlaying ? (
            <FaPlay
              className="fill-[white] w-10 h-10 hover:opacity-60"
              onClick={play}
            />
          ) : (
            <FaPause
              className="fill-[white] w-10 h-10 hover:opacity-60"
              onClick={pause}
            />
          )}
          <FaStepForward
            onClick={playNext}
            className="fill-[white] w-10 h-10 hover:opacity-60"
          />
        </div>
        <span className="text-[white] text-xl">
          {Math.floor(track.duration_ms / 1000 / 60)}:
          {Math.floor((track.duration_ms / 1000) % 60) < 10
            ? `0${Math.floor((track.duration_ms / 1000) % 60)}`
            : Math.floor((track.duration_ms / 1000) % 60)}
        </span>
        <span className="text-[white] text-xl" onClick={goBack}>
          뒤로가기
        </span>
      </div>
    </div>
  );
}

export default PlayMusic;
