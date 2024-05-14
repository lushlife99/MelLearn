import React, { useState } from "react";
import { LinearProgress, Radio, RadioGroup } from "@mui/joy";
import { FormControlLabel } from "@mui/material";
import BgCircle from "../components/BgCircle";
import { useLocation, useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";
import "../css/scroll.css";
import axiosApi from "../api";

interface Quiz {
  id: number;
  level: number;
  musicId: string;
  quizzes: {
    answer: number;
    comment: string;
    id: number;
    question: string;
    optionList: string[];
  }[];
}

// TODO 결과보기 했을 때 랭크나오게

export const QuestionBoardV2 = (): JSX.Element => {
  const [index, setIndex] = useState(0); // 현재 문제 인덱스
  const location = useLocation();
  const { category, track, quiz } = location.state;
  const [problem, setProblem] = useState<Quiz>(quiz);
  const [progress, setProgress] = useState(0);
  const [answers, setAnswers] = useState<number[]>(
    new Array(problem.quizzes.length).fill(0)
  );
  // 유저가 푼 문제 전체 답

  const [isLast, setIsLast] = useState<boolean>(false);

  const navigate = useNavigate();

  const onChagneAnswer = (answer: number) => {
    if (answer !== undefined) {
      const newArr = [...answers];
      newArr[index] = answer;
      setAnswers(newArr);
    }
  };

  const handleNextProblem = async () => {
    // 다음 문제로 이동
    if (index <= problem.quizzes.length - 2) {
      setIndex(index + 1);
      setProgress(((index + 1) / (problem.quizzes.length - 1)) * 100);
    }
    if (index === problem.quizzes.length - 1) {
      setIsLast(true);
    }
  };
  const submitProblem = async () => {
    const hasZeroAnswer = answers.some((answer) => answer === 0);
    if (hasZeroAnswer) {
      alert("풀지 않은 문제가 있습니다.");
    } else {
      const res = await axiosApi.post(`/api/quiz/submit/${category}`, {
        musicId: track.id,
        quizType: category.toUpperCase(),
        answers: answers,
      });

      if (res.status === 200) {
        navigate("/score", {
          state: {
            comments: res.data,
          },
        });
      }
    }
  };

  const move = () => {
    if (index !== 0) {
      //첫번째 문제 아니면 이전 문제로
      setIndex(index - 1);
      setProgress((index / (problem.quizzes.length - 1)) * 100);
      setIsLast(false);
    } else {
      navigate(-1); // 첫번째 문제면 뒤로가기
    }
  };

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8">
        <BgCircle />
        <div className="z-10">
          <IoIosArrowRoundBack
            onClick={move}
            className="w-10 h-10 mt-8 fill-black hover:opacity-60"
          />
          <div className="flex items-center justify-center mt-8 mb-12">
            <LinearProgress determinate value={progress} />
          </div>

          {/* 문제 */}

          {problem.quizzes.map(
            (quiz, idx) =>
              idx === index && (
                <div key={idx}>
                  <span className="text-[#007AFF] text-3xl font-extrabold">
                    Q{index + 1}.
                  </span>
                  <p className="mt-2 mb-12 text-2xl font-extrabold text-black">
                    {quiz.question}
                  </p>
                  <RadioGroup
                    className=" radio-buttons-group-focus"
                    onChange={(e: any) =>
                      onChagneAnswer(parseInt(e.target.value))
                    }
                    value={answers[index]}
                  >
                    {quiz.optionList.map((option, idx) => (
                      <FormControlLabel
                        key={idx}
                        value={idx + 1}
                        control={<Radio className="items-center mr-2" />}
                        label={
                          <div className="flex flex-col items-start">
                            <div className="flex items-center mb-1">
                              <span className="text-[black] text-lg ">
                                {idx + 1}. {option}
                              </span>
                            </div>
                          </div>
                        }
                      />
                    ))}
                  </RadioGroup>
                </div>
              )
          )}
          <div className="flex justify-center w-full mb-20 fixed-bottom">
            <button
              onClick={!isLast ? handleNextProblem : submitProblem}
              className="bg-[#007AFF] w-[80%] h-10 text-white rounded-lg hover:opacity-60"
            >
              {isLast ? "결과 확인 " : "다음"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
