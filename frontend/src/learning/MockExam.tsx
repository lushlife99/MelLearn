import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axiosApi, { axiosSpotifyScraper } from "../api";
import { FormControlLabel, Radio, RadioGroup } from "@mui/material";

interface ReadComp {
  id: number;
  quizzes: {
    answer: number;
    comment: string;
    correctRate: number;
    question: string;
    optionList: string[];
  }[];
}
interface Listening {
  answerList: string[];
  blankedText: string;
}
interface Exam {
  grammarQuiz: ReadComp;
  listeningQuizDto: Listening;
  readingQuiz: ReadComp;
  vocaQuiz: ReadComp;
}

function MockExam() {
  const location = useLocation();
  const { track } = location.state;
  const [exam, setExam] = useState<Exam>();
  const fetchExam = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}`
    );
    const res2 = await axiosApi.post("/api/comprehensive-quiz", {
      musicId: track.id,
      quizType: "GRAMMAR",
      lyric: res.data,
    });
    setExam(res2.data);
    console.log(res2.data);
  };
  useEffect(() => {
    fetchExam();
  }, []);

  return (
    <div className="bg-[white] flex flex-row justify-center w-full h-screen">
      <div className="bg-[white] overflow-hidden w-[450px] h-screen relative flex flex-col px-8 border border-black">
        {/* 모의고사 제목*/}
        <div className="my-2 border-t-2 border-gray-300"></div>
        <div className="flex justify-center w-full">
          <span className="text-3xl font-extrabold">{track.name} 모의고사</span>
        </div>
        <div className="my-2 border-t-2 border-gray-300"></div>

        {/* 문제 */}
        <div>
          <span className="font-bold ">
            [01-05] Read the following passages. Then choose the option that
            best completes the passage.
          </span>
          {exam?.grammarQuiz.quizzes.map((problem, index) => (
            <div key={index} className="flex flex-col mt-2 ">
              <span className="font-bold">{index + 1}.</span>
              <div className="p-2 mb-2 font-semibold border border-black">
                {problem.question}
              </div>
              <RadioGroup
                className="radio-buttons-group-focus"
                row
                // onChange={(e: any) =>
                //   onChagneAnswer(parseInt(e.target.value))
                // }
                // value={answers[index]}
              >
                {problem.optionList.map((option, index) => (
                  <FormControlLabel
                    key={index}
                    value={index + 1}
                    control={<Radio className="items-center" />}
                    label={
                      <div className="">
                        <div className="flex items-center">
                          <span className="text-[black] text-md  ">
                            {index + 1}. {option}
                          </span>
                        </div>
                      </div>
                    }
                  />
                ))}
              </RadioGroup>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default MockExam;
