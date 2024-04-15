import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import BgCircle from "../components/BgCircle";

const Listening = () => {
  const location = useLocation();
  const { category, track, quiz } = location.state;
  console.log(quiz);

  const [userAnswers, setUserAnswers] = useState(
    Array(quiz.blankedText.length).fill("")
  );

  const handleAnswerChange = (index: any, event: any) => {
    const newAnswers = [...userAnswers];
    newAnswers[index] = event.target.value;
    setUserAnswers(newAnswers);
  };

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8">
        <BgCircle />
        <div className="z-10 font-normal text-white text-2xl leading-[normal]  whitespace-normal mt-12">
          {quiz.blankedText.split("__").map((part: any, index: number) => (
            <React.Fragment key={index}>
              {part}
              {index !== quiz.blankedText.split("__").length - 1 && (
                <input
                  type="text"
                  onChange={(event) => handleAnswerChange(index, event)}
                  value={userAnswers[index]}
                  className="w-16 h-4 text-center text-black border-none"
                />
              )}
            </React.Fragment>
          ))}
          <div>
            <p>-----------------------------------</p>
            <p>내가 적은 정답</p>
            {userAnswers.map((answer, index) => (
              <span key={index}>{answer} </span>
            ))}
            <div>플레이어 재생</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Listening;
