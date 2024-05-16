import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import BgCircle from "../components/BgCircle";
import { IoIosArrowRoundBack } from "react-icons/io";

interface Comment {
  id: number;
  quizList: {
    id: number;
    level: number;
    musicId: string;
    quizzes: {
      answer: number;
      comment: string;
      correctRate: 75;
      id: number;
      optionList: string[];
      question: string;
    }[];
  }[];
  submitAnswerList: number[];
}
interface CommentQuiz {
  answer: number;
  comment: string;
  correctRate: 75;
  id: number;
  optionList: string[];
  question: string;
}

const Commentary = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { comments } = location.state;

  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 상태
  const pageSize = 2; // 페이지당 항목 수

  // 현재 페이지에 따라 해당 범위의 항목을 선택하는 함수

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen font-[roboto]">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8">
        <BgCircle />

        <div className="z-10 flex flex-col items-center h-full ">
          <div
            className="flex justify-start w-[90%] mb-8 h-10"
            onClick={() => navigate(-1)}
          >
            <IoIosArrowRoundBack className="w-8 h-10 mt-4 fill-black hover:opacity-55" />
          </div>

          {comments?.quizList?.quizzes
            .slice((currentPage - 1) * pageSize, currentPage * pageSize)
            .map((quiz: CommentQuiz, index: number) => (
              <div
                key={index}
                className="mb-12 bg-white w-[92%] h-[40%] rounded-3xl p-3 shadow-[0px_4px_4px_#00000040]"
              >
                <div className="flex items-center justify-between">
                  {/* 문제 표시 */}
                  <span
                    className={`text-3xl font-extrabold text-[${
                      quiz.answer ===
                      comments.submitAnswerList[
                        (currentPage - 1) * pageSize + index
                      ]
                        ? "#007AFF"
                        : "red"
                    }]`}
                  >
                    Q{(currentPage - 1) * pageSize + index + 1}.
                  </span>
                  <span className="text-sm text-gray-400">
                    정답률{" "}
                    {!isNaN(Number(quiz.correctRate))
                      ? quiz.correctRate.toFixed(2) + "%"
                      : "없음"}
                  </span>
                </div>

                <div className="flex flex-col mt-2">
                  <div className="">
                    <span className="mb-3 text-xl font-bold">
                      {quiz.question.includes("_____") ? (
                        <>
                          {quiz.question.split("_____")[0]}
                          <span
                            className={`text-[${
                              quiz.answer ===
                              comments.submitAnswerList[
                                (currentPage - 1) * pageSize + index
                              ]
                                ? "#007AFF"
                                : "red"
                            }]`}
                          >
                            {quiz.optionList[quiz.answer - 1]?.replace(
                              /\d+\./g,
                              ""
                            )}
                          </span>
                          {quiz.question.split("_____")[1]}
                        </>
                      ) : (
                        quiz.question
                      )}
                    </span>
                  </div>

                  {/* Comment */}
                  <div className="mt-2">
                    <span className="text-lg font-extrabold ">Note:</span>
                    <p className="px-1 text-[9px] text-gray-500 text-sm font-bold">
                      {quiz.comment}
                    </p>
                  </div>
                </div>

                <div className="flex justify-center mt-2 border border-black  shadow-[0px_4px_4px_#00000040] rounded-md">
                  <span className="mr-2 font-extrabold">사용자 답안: </span>
                  <span
                    className={`font-extrabold  text-[${
                      quiz.answer ===
                      comments.submitAnswerList[
                        (currentPage - 1) * pageSize + index
                      ]
                        ? "#007AFF"
                        : "red"
                    }]`}
                  >
                    {quiz.optionList[
                      comments.submitAnswerList[
                        (currentPage - 1) * pageSize + index
                      ] - 1
                    ]?.replace(/\d+\./g, "")}
                  </span>
                </div>
              </div>
            ))}
          {/* 페이징 버튼 */}
          <div className="flex justify-center w-full fixed-bottom">
            {Array.from(
              { length: comments.quizList.quizzes.length / 2 + 1 },
              (_, i) => i + 1
            ).map((page) => (
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
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Commentary;