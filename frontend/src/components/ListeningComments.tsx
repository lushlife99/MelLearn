import React from "react";

interface ListeningComment {
  listeningQuiz: {
    blankedText: string;
    answerList: string[];
  };
  submitAnswerList: string[];
}
interface IListeningComment {
  comments: ListeningComment;
}
function ListeningComments({ comments }: IListeningComment) {
  return (
    <div className="h-full px-4 py-2 bg-white rounded-xl">
      <div className="text-2xl font-bold text-black">
        {comments.listeningQuiz.blankedText
          .split("__")
          .map((part: string, index: number) => {
            if (index < comments.listeningQuiz.answerList.length) {
              return (
                <span key={index} className="">
                  <span className="text-2xl">{part}</span>
                  <span
                    className={`text-2xl ${
                      comments.listeningQuiz.answerList[index] ===
                      comments.submitAnswerList[index]
                        ? "text-blue-500" // 같을 때는 파란색
                        : "text-red-500" // 다를 때는 빨간색
                    }`}
                  >
                    {comments.listeningQuiz.answerList[index]}
                  </span>
                </span>
              );
            } else {
              return (
                <span className="text-green" key={index}>
                  {part}
                </span>
              );
            }
          })}
      </div>
    </div>
  );
}

export default ListeningComments;
