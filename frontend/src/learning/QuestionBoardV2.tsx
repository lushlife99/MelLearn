import React, { useEffect, useState } from "react";
import Input from "@mui/joy/Input";
import { Button, LinearProgress, Radio, RadioGroup } from "@mui/joy";
import DoDisturbAltIcon from "@mui/icons-material/DoDisturbAlt";
import { FormControlLabel } from "@mui/material";

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

  const handleSubmit = () => {
    // 사용자가 답을 제출했을 때 실행되는 함수
    const currentQuestion = words[index];
    console.log(userAnswer);
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
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full">
      <div className="bg-[#9bd1e5] overflow-hidden w-[360px] h-[800px] relative">
        <div className="absolute w-[597px] h-[668px] top-[156px] left-[-182px]">
          <div className="absolute w-[500px] h-[500px] top-0 left-0 bg-[#d1faff2b] rounded-[250px]" />
          <div className="absolute w-[170px] h-[170px] top-[498px] left-[393px] bg-[#a4d8e9] rounded-[85px]" />
          <p className="absolute w-[308px] top-[109px] left-[204px] [font-family:'Baloo_Tamma-Regular',Helvetica] font-normal text-black text-[20px] text-center tracking-[-0.10px] leading-[40px]">
            {words[index].question}
          </p>
          <div className="flex flex-col w-[393px] items-start gap-[12px] px-[16px] py-0 absolute top-[38px] left-[204px]">
            <div className="relative w-[361px] mt-[-1.00px] [font-family:'Baloo_Tamma-Regular',Helvetica] font-normal text-light-modeprimary  text-blue-600 text-[35px] tracking-[-0.14px] leading-[24px]">
              Q{index + 1}
            </div>
          </div>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSubmit();
            }}
          >
            {/*<Input*/}
            {/*    className="!h-[56px] !absolute !left-[225px] !bg-white !w-[267px] !top-[265px]"*/}
            {/*    placeholder="Try to submit with no text!"*/}
            {/*    required*/}
            {/*    value={userAnswer}*/}
            {/*    onChange={(e) => setUserAnswer(e.target.value)}*/}
            {/*/>*/}
            <RadioGroup
              className=" radio-buttons-group-focus !h-[56px] !absolute !left-[225px]  !w-[267px] !top-[265px]"
              onChange={(e) => setUserAnswer(e.target.value)}
              value={userAnswer}
            >
              <FormControlLabel
                style={{ marginBottom: "5px" }}
                value="1"
                control={<Radio style={{ marginRight: "10px" }} />}
                label={words[index].optionList[0]}
              />
              <FormControlLabel
                style={{ marginBottom: "5px" }}
                value="2"
                control={<Radio style={{ marginRight: "10px" }} />}
                label={words[index].optionList[1]}
              />
              <FormControlLabel
                style={{ marginBottom: "5px" }}
                value="3"
                control={<Radio style={{ marginRight: "10px" }} />}
                label={words[index].optionList[2]}
              />
              <FormControlLabel
                style={{ marginBottom: "5px" }}
                value="4"
                control={<Radio style={{ marginRight: "10px" }} />}
                label={words[index].optionList[3]}
              />
            </RadioGroup>
            <Button
              className="!h-[35px] !w-[80px] !absolute !left-[300px]  !top-[450px]"
              onClick={handleSubmit}
            >
              제출
            </Button>

            {isCorrect !== null && (
              <div>
                {isCorrect ? (
                  <div>
                    <p className="!flex  !absolute !left-[320px] !w-[336px] !top-[520px]">
                      정답입니다!
                    </p>
                    <Button
                      className="!flex !absolute !left-[200px] !w-[336px] !top-[559px]"
                      type="button"
                      onClick={handleNext}
                    >
                      다음 문제
                    </Button>
                  </div>
                ) : (
                  <div>
                    <div className="flex-column rounded bg-white !absolute !left-[200px] !w-[336px] !top-[100px] !h-[450px]">
                      <p style={{ color: "red" }}>
                        <DoDisturbAltIcon></DoDisturbAltIcon>
                        오답입니다
                      </p>
                      <p>{words[index].comment}</p>
                    </div>
                    <Button
                      color="danger"
                      className="!flex !absolute !left-[200px] !w-[336px] !top-[559px]"
                      type="button"
                      onClick={handleNext}
                    >
                      다음 문제
                    </Button>
                  </div>
                )}
              </div>
            )}

            <div>
              {isLast ? (
                <div>
                  <Button
                    className="!flex !absolute !left-[200px] !w-[336px] !top-[559px]"
                    type="button"
                  >
                    결과보기
                  </Button>
                </div>
              ) : (
                <> </>
              )}
            </div>
          </form>
        </div>
        <div className="absolute w-[354px] h-[139px] top-[-20px] left-[-22px]">
          <div className="absolute w-[139px] h-[139px] top-0 left-0 bg-[#a4d8e9] rounded-[69.5px]" />
          <div className="flex flex-col w-[292px] items-center absolute top-[118px] left-[62px]">
            <div className="flex w-[354px] items-center justify-center pt-[8px] pb-0 px-[16px] relative flex-[0_0_auto] ml-[-31.00px] mr-[-31.00px]">
              <LinearProgress determinate value={progress} />
              {/*    question 문제 받고 value 해주면 될듯 */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
