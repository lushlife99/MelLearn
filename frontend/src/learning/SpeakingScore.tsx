import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import BgCircle from "../components/BgCircle";

function SpeakingScore() {
  const location = useLocation();
  const navigate = useNavigate();
  const { comments, track } = location.state;

  const parts = comments.markedText.split("_");
  const viewRank = async () => {
    navigate("/rank", {
      state: {
        track,
      },
    });
  };
  const goHome = () => {
    navigate("/home");
  };
  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col ">
        <BgCircle />
        <div className="z-10 flex flex-col items-center justify-center w-full h-full px-12">
          <div className="w-full px-4 py-2 mb-8 overflow-y-auto text-black bg-white rounded-3xl h-96 shadow-[0px_4px_4px_#00000040]">
            <p>
              {parts.map((part: any, index: number) => {
                if (index % 2 === 0) {
                  // 인덱스가 홀수인 경우(즉, __ 다음에 오는 문자열)
                  return (
                    <span
                      className="font-bold text-[#FF0000] text-lg"
                      key={index}
                    >
                      {part}
                    </span>
                  );
                } else {
                  // 인덱스가 짝수인 경우(즉, __)
                  return part;
                }
              })}
            </p>
          </div>
          <div className="bg-[#55A2FD] w-80 h-80 rounded-full flex justify-center items-center">
            <div className="flex items-center justify-center w-72 h-72 bg-[#9bd1e5] rounded-full">
              <div className="flex items-center justify-center w-64 h-64 bg-[#F8F8F8] rounded-full">
                <div className="flex flex-col items-center justify-center w-56 h-56 bg-[#ffffff] rounded-full relative border border-[#f8f8f8]">
                  <div className="flex items-center justify-center w-48 h-24 bg-[#55A2FD] rounded-t-full rounded-b-none overflow-hidden top-3 absolute"></div>
                  <span className="mt-24 mb-2 text-3xl font-bold">점수</span>
                  <span className="text-[#55A2FD] text-5xl ">
                    {comments.score}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div className="flex justify-center w-[80%] h-20 text-white font-bold flex-col  mt-8">
            <button
              className="bg-[#007AFF] mb-2 h-12 rounded-lg hover:opacity-60"
              onClick={viewRank}
            >
              랭킹 보기
            </button>
            <button
              className="bg-[#007AFF] h-12 rounded-lg hover:opacity-60"
              onClick={goHome}
            >
              홈으로
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SpeakingScore;
