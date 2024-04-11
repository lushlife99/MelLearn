import React, { useEffect, useState } from "react";
import Input from "@mui/joy/Input";
import { Button, LinearProgress, Radio, RadioGroup } from "@mui/joy";
import DoDisturbAltIcon from "@mui/icons-material/DoDisturbAlt";
import { FormControlLabel } from "@mui/material";
import BgCircle from "../components/BgCircle";
import { useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";
import { BsCheckCircle } from "react-icons/bs";
import { GiCancel } from "react-icons/gi";
import "../css/scroll.css";

// TODO 결과보기 했을 때 랭크나오게

export const QuestionBoardV2 = (): JSX.Element => {
  const [index, setIndex] = useState(0); // 현재 문제 인덱스
  const [answerCount, setAnswerCount] = useState(0); // 맞은 문제 개수

  const [words, setWords] = useState([
    {
      question:
        "'Very unmanageable day' What is the meaning of 'unmanageable' in this sentence?",
      optionList: [
        "not capable of being controlled or dealt with",
        "not scheduled properly",
        "physically larger than usual",
        "lacking in sunshine",
      ],
      answer: "1",
      comment:
        "'Very unmanageable day'에서 'unmanageable'은 일상의 사건이나 상황이 너무 복잡하거나 어려워서 쉽게 다루거나 제어할 수 없을 때 사용되는 형용사입니다. 이 경우, 'unmanageable'은 그 날이 너무 혼란스럽거나 예측할 수 없는 상황들로 가득 차서 일상적인 관리나 대처가 힘든 상태를 의미합니다. 선택지 B, C, D는 이 문맥에서 'unmanageable'의 의미와 직접적으로 관련이 없습니다. B는 일정이 제대로 계획되지 않았음을, C는 물리적 크기와 관련된 의미를, D는 날씨 상태와 관련된 의미를 나타내지만, 이들은 'unmanageable'이 표현하고자 하는 복잡하고 제어하기 어려운 상황의 본질과는 거리가 있습니다. 따라서 정답 A는 이 구절에서 'unmanageable'이 의미하는 바를 가장 잘 반영합니다.",
    },
    {
      question:
        "'Serene view' What is the meaning of 'serene' in this sentence?",
      optionList: [
        "peaceful and calm",
        "filled with noise",
        "lacking in color",
        "intensely bright",
      ],
      answer: "1",
      comment:
        "'Serene view'에서 'serene'은 평화롭고 고요한 상태를 나타냅니다. 이러한 상황은 일반적으로 자연 경관이나 풍경에서 나타납니다. 따라서 선택지 A가 이 문장에서 'serene'의 의미를 가장 잘 반영합니다.",
    },
    {
      question: "'Eloquent speaker' What does 'eloquent' mean in this context?",
      optionList: [
        "fluent or persuasive in speaking or writing",
        "lacking confidence",
        "using complex language",
        "speaking softly",
      ],
      answer: "1",
      comment:
        "'Eloquent speaker'에서 'eloquent'는 말이나 글쓰기에서 유창하고 설득력 있는 것을 의미합니다. 따라서 선택지 A가 이 문장에서 'eloquent'의 의미를 가장 잘 반영합니다.",
    },
    {
      question:
        "'Abundant resources' What does 'abundant' mean in this context?",
      optionList: [
        "plentiful or ample",
        "very expensive",
        "difficult to obtain",
        "lacking in variety",
      ],
      answer: "1",
      comment:
        "'Abundant resources'에서 'abundant'는 풍부하거나 충분한 것을 나타냅니다. 따라서 선택지 A가 이 문장에서 'abundant'의 의미를 가장 잘 반영합니다.",
    },
  ]);

  // 신기해서
  const [progress, setProgress] = useState(0);
  const [userAnswer, setUserAnswer] = useState(""); // 사용자 입력 답
  const [isCorrect, setIsCorrect] = useState(null); // 정답 확인 상태
  const [isLast, setIsLast] = useState(null);

  const navigate = useNavigate();

  const handleNext = async () => {
    // 다음 문제로 이동
    const nextIndex = index + 1; // % words.length
    if (nextIndex === words.length) {
      console.log("QuestionBoard -  마지막 문제 ");
      setProgress(100);
      // @ts-ignore
      setIsLast(true);
      console.log(answerCount);
    } else {
      setIndex(nextIndex);
      setProgress((nextIndex / words.length) * 100);
    }

    // Progress 나중에 손보기
    setUserAnswer(""); // 다음 문제로 이동할 때 사용자 입력 초기화
    setIsCorrect(null); // 정답 확인 상태 초기화
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // 사용자가 답을 제출했을 때 실행되는 함수
    const currentQuestion = words[index];
    console.log(userAnswer, 100, currentQuestion.answer);
    if (userAnswer === currentQuestion.answer) {
      // 정답인 경우
      // @ts-ignore
      setIsCorrect(true);
      const nextCount = answerCount + 1;
      setAnswerCount(nextCount);

      console.log("success");
    } else {
      // 오답인 경우
      // @ts-ignore
      setIsCorrect(false);
      console.log("fail");
    }
  };

  useEffect(() => {}, [index]);

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8">
        <BgCircle />
        <div className="z-10">
          <IoIosArrowRoundBack className="w-10 h-10 mt-8 fill-black hover:opacity-60" />
          <div className="flex items-center justify-center mt-8 mb-12">
            <LinearProgress determinate value={progress} className="h-4" />
            {/*    question 문제 받고 value 해주면 될듯 */}
          </div>

          {/* 문제 */}

          {words.map(
            (word, idx) =>
              idx === index && (
                <div key={idx}>
                  <span className="text-[#007AFF] text-3xl font-extrabold">
                    Q{index + 1}.
                  </span>
                  <p className="mt-2 mb-12 text-2xl font-extrabold text-black">
                    {word.question}
                  </p>
                  <RadioGroup
                    className=" radio-buttons-group-focus"
                    onChange={(e: any) => setUserAnswer(e.target.value)}
                  >
                    {word.optionList.map((option, idx) => (
                      <FormControlLabel
                        key={idx}
                        value={idx + 1}
                        control={<Radio className="items-center mr-2" />}
                        label={
                          <div className="flex flex-col items-start">
                            <div className="flex items-center mb-1">
                              <span className="text-[black] text-lg font-extrabold mr-2">
                                {idx + 1}.
                              </span>
                              <span className="text-[black] text-lg ">
                                {option}
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

          <form
            onSubmit={handleSubmit}
            className="flex justify-center w-full mt-28"
          >
            <input
              className="bg-[#007AFF] w-[70%] h-10 text-white rounded-lg hover:opacity-60"
              type="submit"
              value="정답 확인"
            />
          </form>
        </div>
      </div>

      {isCorrect !== null && (
        <div className="fixed bottom-0 w-[450px] bg-white z-10 h-96 rounded-t-3xl">
          <div className="flex flex-col items-start justify-center px-4">
            <div className="flex items-center mt-3">
              {isCorrect ? (
                <BsCheckCircle className={"w-7 h-7 fill-[#007AFF] mr-2"} />
              ) : (
                <GiCancel className="w-7 h-7 fill-[#D53F36] mr-2" />
              )}
              <span
                className={`text-[${
                  isCorrect ? "#007AFF" : "#D53F36"
                }] text-xl font-extrabold`}
              >
                {isCorrect ? "Great!" : "Wrong!"}
              </span>
            </div>
            <span className="mt-4 mb-1 text-lg font-extrabold text-black">
              Comment:
            </span>
            <span
              className={`pl-3 text-lg h-28 font-extrabold ${
                isCorrect ? "" : "overflow-y-auto "
              }`}
            >
              {isCorrect ? "정답입니다." : words[index].comment}
            </span>
            <div className="flex justify-center w-full mt-20 ">
              <button
                className={`bg-[${
                  isCorrect ? "#007AFF" : "#D53F36"
                }] w-[80%] h-10 text-white rounded-lg hover:opacity-60`}
                onClick={handleNext}
              >
                {isLast ? "결과 보기" : "다음 문제"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
