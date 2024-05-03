import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axiosApi, { axiosSpotifyScraper } from "../api";
import { FormControlLabel, Radio, RadioGroup } from "@mui/material";
import "../css/scroll.css";

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
  const [currentPage, setCurrentPage] = useState(1);
  const [exam, setExam] = useState<
    {
      answer: number;
      comment: string;
      correctRate: number;
      question: string;
      optionList: string[];
    }[]
  >([]);

  const combineQuizzArray = (quiz: Exam) => {
    const combinArray = [];
    if (quiz.grammarQuiz.quizzes) combinArray.push(...quiz.grammarQuiz.quizzes);
    if (quiz.readingQuiz.quizzes) combinArray.push(...quiz.readingQuiz.quizzes);
    if (quiz.vocaQuiz.quizzes) combinArray.push(...quiz.vocaQuiz.quizzes);

    return combinArray;
  };

  const fetchExam = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}`
    );
    const res2 = await axiosApi.post("/api/comprehensive-quiz", {
      musicId: track.id,
      quizType: "GRAMMAR",
      lyric: res.data,
    });

    const combineQuizArr = combineQuizzArray(res2.data);
    setExam(combineQuizArr);
  };
  useEffect(() => {
    fetchExam();
  }, []);

  return (
    <div className="bg-[white] flex flex-row justify-center w-full h-screen font-[roboto]">
      <div className="bg-[white] whi overflow-hidden w-[450px] h-full relative flex flex-col px-8 border border-black">
        {/* 모의고사 제목*/}
        <div className="h-[10%] ">
          <div className="my-2 border-t-2 border-gray-300"></div>
          <div className="flex justify-center w-full">
            <span className="text-3xl font-extrabold">
              {track.name} 모의고사
            </span>
          </div>
          <div className="my-2 border-t-2 border-gray-300"></div>
        </div>

        {/* 문제 */}
        <div>
          <span className="font-bold ">
            {currentPage === 1 &&
              "[01-05] Read the following passages. Then choose the option that best completes the passage."}
            {currentPage === 2 &&
              "[06-10] Read the following passages. Then choose the option that best completes the passage."}
            {currentPage === 3 &&
              "[11-15] Read the following passages. Then choose the option that best completes the passage."}
          </span>
          <div className="overflow-y-auto  h-[80%] scrollbarwhite  whitespace-normal ">
            {exam
              .slice((currentPage - 1) * 5, currentPage * 5)
              ?.map((problem, index) => (
                <div key={index} className="flex flex-col mt-2">
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

          <div className="flex justify-center w-full h-[10%] mt-8">
            {Array.from({ length: exam.length / 5 + 1 }, (_, i) => i + 1).map(
              (page) => (
                <button
                  key={page}
                  onClick={() => setCurrentPage(page)}
                  className={`p-2 w-10 h-10 mx-1  rounded-md cursor-pointer bg-[${
                    currentPage === page ? "#007AFF" : "white"
                  }] text-[${
                    currentPage === page ? "white" : "black"
                  }] text-center hover:opacity-60`}
                >
                  {page}
                </button>
              )
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MockExam;
