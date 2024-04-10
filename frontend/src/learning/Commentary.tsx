import React, { useState } from "react";
import { Button } from "@mui/joy";

const pageSize = 2; // 페이지당 항목 수

const Commentary = () => {
  const [words, setWords] = useState([
    {
      question: "Im not a _____ of a redneck agenda",
      answer: "part",
      commentary: "Part is ...",
    },
    {
      question: "All _____ the alien nation",
      answer: "across",
      commentary: "Across is ...",
    },
    {
      question: "For that enough to _____",
      answer: "argue",
      commentary: "Argue is ...",
    },
    {
      question: "The sun _____ from the east",
      answer: "rises",
      commentary: "Rises is ...",
    },
    {
      question: "She _____ to school every day",
      answer: "goes",
      commentary: "Goes is ...",
    },
    {
      question: "We _____ to the beach last summer",
      answer: "went",
      commentary: "Went is ...",
    },
    {
      question: "He _____ a book when I saw him",
      answer: "was reading",
      commentary: "Was reading is ...",
    },
    {
      question: "I _____ my keys yesterday",
      answer: "lost",
      commentary: "Lost is ...",
    },
    {
      question: "We _____ to the cinema tonight",
      answer: "are going",
      commentary: "Are going is ...",
    },
    {
      question: "The cat _____ on the roof",
      answer: "is sitting",
      commentary: "Is sitting is ...",
    },
  ]);

  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 상태
  const pageCount = Math.ceil(words.length / pageSize); // 전체 페이지 수

  // 현재 페이지에 따라 해당 범위의 항목을 선택하는 함수
  const getVisibleItems = () => {
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = Math.min(startIndex + pageSize, words.length);
    return words.slice(startIndex, endIndex);
  };

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen h-100 ">
      <div className="relative bg-[#9bd1e5] w-full max-w-[450px] h-auto flex flex-col ">
        {/*<div className="px-5 mt-24 z-1">*/}
        {/*    {words.map((question, index) => (*/}
        {/*        <div style={{*/}
        {/*            backgroundColor: "white",*/}
        {/*            borderRadius: "6px",*/}
        {/*            margin: "10px",*/}
        {/*            height: "250px",*/}
        {/*            marginBottom: "30px"*/}

        {/*        }}>*/}
        {/*            <div*/}
        {/*                style={{*/}
        {/*                    color: true ?"blue":"red", // 맞으면 파란 틀리면 red 문제에 맞았는지 틀렸는지 boolean값 가져오기*/}
        {/*                    fontSize: "30px",*/}
        {/*                    marginBottom: "10px"*/}
        {/*            }}*/}
        {/*            >Q{index + 1}.</div>*/}
        {/*            <div style={{*/}
        {/*                fontSize: "25px",*/}
        {/*                textAlign:"center",*/}
        {/*                marginBottom: "10px",*/}

        {/*            }}>*/}
        {/*                /!*{question.question.replace("_____", (*!/*/}
        {/*                /!*    <span style={{color: "green"}}>{question.answer}</span>*!/*/}
        {/*                /!*))}*!/*/}
        {/*                {question.question.includes("_____") ? question.question.split("_____").map((part, index) => {*/}
        {/*                    if (index === 0) {*/}
        {/*                        return (*/}
        {/*                            <span key={index}>{part}</span>*/}
        {/*                        );*/}
        {/*                    } else {*/}
        {/*                        return (*/}
        {/*                            <span key={index}>*/}
        {/*                                /!*정답이면 파랑 아니면 빨강 *!/*/}
        {/*                                <span style={{ color: "green" }}>{question.answer}</span>*/}
        {/*                                {part}*/}
        {/*                            </span>*/}
        {/*                        );*/}
        {/*                    }*/}
        {/*                }) : question.question}*/}
        {/*            </div>*/}

        {/*            Note: {question.commentary}*/}
        {/*        </div>*/}
        {/*    ))}*/}
        {/*</div>*/}
        <div className="px-5 mt-24 z-1">
          {getVisibleItems().map((question, index) => (
            <div
              key={index}
              style={{
                backgroundColor: "white",
                borderRadius: "6px",
                margin: "10px",
                height: "250px",
                marginBottom: "30px",
              }}
            >
              {/* 문제 표시 */}
              <div
                style={{
                  color: true ? "blue" : "red", // 맞으면 파란 틀리면 red 문제에 맞았는지 틀렸는지 boolean값 가져오기
                  fontSize: "30px",
                  marginBottom: "10px",
                }}
              >
                Q{(currentPage - 1) * pageSize + index + 1}.
              </div>
              <div
                style={{
                  fontSize: "25px",
                  textAlign: "center",
                  marginBottom: "10px",
                }}
              >
                {question.question.includes("_____")
                  ? question.question.split("_____").map((part, index) => {
                      if (index === 0) {
                        return <span key={index}>{part}</span>;
                      } else {
                        return (
                          <span key={index}>
                            {/* 정답이면 파랑 아니면 빨강 */}
                            <span style={{ color: "green" }}>
                              {question.answer}
                            </span>
                            {part}
                          </span>
                        );
                      }
                    })
                  : question.question}
              </div>
              {/* 주석 표시 */}
              Note: {question.commentary}
            </div>
          ))}
          {/* 페이징 버튼 */}
          <div style={{ textAlign: "center" }}>
            {Array.from({ length: pageCount }, (_, i) => i + 1).map((page) => (
              <button
                key={page}
                onClick={() => setCurrentPage(page)}
                style={{
                  margin: "5px",
                  padding: "5px 10px",
                  backgroundColor: currentPage === page ? "blue" : "white",
                  color: currentPage === page ? "white" : "black",
                  border: "1px solid blue",
                  borderRadius: "5px",
                  cursor: "pointer",
                }}
              >
                {page}
              </button>
            ))}
          </div>
        </div>

        <div className="w-[600px] flex justify-center">
          <Button>이건 차라리 위에 뒤로가기 만들어버리자 </Button>
        </div>
      </div>
    </div>
  );
};

export default Commentary;
