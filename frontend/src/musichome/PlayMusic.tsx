import React, { useEffect, useState } from "react";
import { aixosSpotify, axiosSpotifyScraper } from "../api";
import { useLocation, useNavigate } from "react-router-dom";
import { FaPlay, FaPause, FaStepBackward, FaStepForward } from "react-icons/fa";
import { useQuery } from "react-query";

function PlayMusic() {
  // https://developer.spotify.com/documentation/web-api/reference/start-a-users-playback

  // https://api.spotify.com/v1/me/player/play
  const location = useLocation();
  const [isPlaying, setIsPlaying] = useState<boolean>();
  const navigate = useNavigate();
  const { track } = location.state;
  // console.log(track);
  //console.log(location.state.track);
  const play = async () => {
    const playbackState = await aixosSpotify.get("/me/player"); //현재 재생 위치 확인
    const progress_ms = playbackState.data.progress_ms;
    const res = await aixosSpotify.put("/me/player/play", {
      uris: ["spotify:track:" + track.id],
      position_ms: progress_ms, // 재생 위치를 현재 위치로 설정
    });

    if (res.status === 202) {
      setIsPlaying(true);
    }
  };
  const getTrackMeta = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/metadata?trackId=${track.id}`
    );
    console.log("meta", res.data);
    //res.data.durationMs , res.data.durationText
  };
  const { data: trackMeta, isLoading: trackMetaLoading } = useQuery(
    "trackMeta",
    getTrackMeta
  );
  console.log("s", trackMeta);

  const pause = async () => {
    const res = await aixosSpotify.put("/me/player/pause");
    if (res.status === 202) {
      setIsPlaying(false);
    }
  };
  const playPrevious = async () => {
    const res = await aixosSpotify.post("/me/player/previous");
    console.log("이전", res.status);
  };
  const playNext = async () => {
    const res = await aixosSpotify.post("/me/player/next");
    console.log("이전", res.status);
  };
  const goHome = () => {
    navigate("/home");
  };

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="relative bg-[black] overflow-hidden w-full max-w-[450px] h-screen  flex flex-col px-3">
        <span className="text-[white] text-center text-3xl font-bold mt-12">
          {track.name}
        </span>
        <div className="flex items-center justify-center mt-16 ">
          <img
            src={track.album.cover[0].url}
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
        <span className="text-[white] text-xl" onClick={goHome}>
          뒤로가기
        </span>
      </div>
    </div>
  );
}

export default PlayMusic;
