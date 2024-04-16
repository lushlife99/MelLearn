import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import BgCircle from "../components/BgCircle";
import "../css/scroll.css";
import { FaPlayCircle } from "react-icons/fa";
import { FaPause, FaPlay } from "react-icons/fa6";
import axiosApi, { axiosSpotify, axiosSpotifyScraper } from "../api";
import { IoIosArrowBack, IoIosArrowRoundBack } from "react-icons/io";
interface TimeLyric {
  startMs: number;
  durMs: number;
}

const Listening = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { category, track, quiz } = location.state;
  const [isPlaying, setIsPlaying] = useState(false);
  const [lyrics, setLyrics] = useState([]);
  const [currentTime, setCurrentTime] = useState<number>(0);
  const [duration, setDuration] = useState<number>(0);
  const [intervalId, setIntervalId] =
    useState<ReturnType<typeof setInterval>>();

  const [timeLyric, setTimeLyric] = useState<TimeLyric[]>([]);

  const [userAnswers, setUserAnswers] = useState(
    Array(quiz.answerList.length).fill("")
  );
  const getFetchTimeLyric = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}&format=json`
    );
    setTimeLyric(res.data);
  };
  useEffect(() => {
    getFetchTimeLyric();
    const newArr = quiz.blankedText.split("\n");
    setLyrics(newArr);
    setDuration(track.duration_ms);
  }, []);

  useEffect(() => {
    if (currentTime >= duration) {
      clearInterval(intervalId);
      setCurrentTime(0);
      setIsPlaying(false);
    }
  }, [currentTime, duration]);

  const startTime = () => {
    const interval = setInterval(() => {
      setCurrentTime((prev) => prev + 1000);
    }, 1000);
    setIntervalId(interval);
  };
  const stopTime = () => {
    clearInterval(intervalId);
  };

  const handleAnswerChange = (index: any, event: any) => {
    const newAnswers = [...userAnswers];
    newAnswers[index] = event.target.value;
    setUserAnswers(newAnswers);
  };
  const [idx, setIdx] = useState(0);
  const resume = async () => {
    startTime();
    const res = await axiosSpotify.get("/me/player/currently-playing");
    let progress_ms = 0;
    if (res.data.item === undefined) {
      progress_ms = 0;
    } else {
      if (track.id === res.data.item.id) {
        progress_ms = res.data.progress_ms;
      } else {
        progress_ms = 0;
      }
    }
    const res2 = await axiosSpotify.put("/me/player/play", {
      uris: ["spotify:track:" + track.id],
      position_ms: progress_ms,
    });

    if (res2.status === 202) {
      setIsPlaying(true);
    }
  };
  const pause = async () => {
    stopTime();
    const res = await axiosSpotify.put("/me/player/pause");
    if (res.status === 202) {
      setIsPlaying(false);
    }
  };

  const submitAnswer = async () => {
    const res = await axiosApi.post(`/api/quiz/submit/${category}`, {
      musicId: track.id,
      submitWordList: userAnswers,
    });
    console.log(res.data);
  };
  console.log(userAnswers);
  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen ">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8 items-center">
        <BgCircle />
        <div className="z-10 flex justify-start w-full mt-4 hover:opacity-60">
          <IoIosArrowRoundBack
            onClick={() => navigate(-1)}
            className="w-8 h-10"
          />
        </div>
        <div className="rounded-3xl bg-white z-10 font-bold text-black text-2xl leading-[normal]  whitespace-normal  overflow-y-auto scrollbarwhite px-4 py-2">
          {timeLyric &&
            lyrics?.map((lyric: string, index: number) => (
              <div key={index}>
                {lyric.split("__").map((part: string, indexs: number) => (
                  <span
                    key={indexs}
                    className={`text-[${
                      currentTime >= timeLyric[index]?.startMs &&
                      currentTime <=
                        timeLyric[index]?.startMs + timeLyric[index]?.durMs
                        ? "gray"
                        : "black"
                    }] hover:text-[gray] text-2xl font-semibold`}
                  >
                    {part}

                    {indexs !== lyric.split("__").length - 1 && (
                      <input
                        type="text"
                        onChange={(event) => handleAnswerChange(idx, event)}
                        value={userAnswers[idx] || ""}
                        className="w-20 h-5 text-xl text-center text-white placeholder-gray-400 bg-black border-none rounded-md"
                      />
                    )}
                  </span>
                ))}
              </div>
            ))}
        </div>
        <div className="z-10 flex items-center w-full mt-4 mb-4 bg-white rounded-2xl h-36">
          <img
            src={track.album.images[2].url}
            alt="Album Cover"
            className="w-16 h-16 rounded-xl"
          />
          <div className="flex flex-col ml-4">
            <span className="font-bold">{track.name}</span>
            <span className="text-sm">{track.artists[0].name}</span>
          </div>
          <div className="ml-44 hover:opacity-50">
            {isPlaying ? (
              <FaPause onClick={pause} className="w-6 h-6" />
            ) : (
              <FaPlay onClick={resume} className="w-6 h-6" />
            )}
          </div>
        </div>
        <div className="flex justify-center w-full h-16 ">
          <button
            onClick={submitAnswer}
            className="z-10  bg-[#007AFF] text-white font-bold w-full rounded-lg hover:opacity-60 "
          >
            제출
          </button>
        </div>
      </div>
    </div>
  );
};

export default Listening;
