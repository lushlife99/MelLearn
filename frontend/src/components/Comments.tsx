interface Comment {
  id: number;
  quizList: {
    id: number;
    level: number;
    musicId: string;
    quizzes: {
      answer: number;
      comment: string;
      correctRate: number;
      id: number;
      optionList: string[];
      question: string;
    }[];
  };
  submitAnswerList: number[];
}
interface IComment {
  comments: Comment;
}
interface CommentQuiz {
  answer: number;
  comment: string;
  correctRate: number;
  id: number;
  optionList: string[];
  question: string;
}

function Comments({ comments }: IComment) {
  return (
    <div className="font-[roboto]">
      {comments?.quizList?.quizzes.map((quiz: CommentQuiz, index: number) => (
        <div
          key={index}
          className="mb-8 bg-white w-[92%] h-[40%] rounded-3xl p-3"
        >
          <div className="flex items-center justify-between">
            {/* 문제 표시 */}
            <span
              className={`text-3xl font-extrabold text-[${
                quiz.answer === comments.submitAnswerList[index]
                  ? "#007AFF"
                  : "red"
              }]`}
            >
              Q{index + 1}.
            </span>
            <span className="text-sm text-gray-400">
              정답률{" "}
              {!isNaN(Number(quiz.correctRate))
                ? quiz.correctRate.toFixed(2) + "%"
                : "없음"}
            </span>
          </div>

          <div className="flex flex-col mt-2">
            <span className="mb-3 text-xl font-bold">
              {quiz.question.includes("_____") ? (
                <>
                  {quiz.question.split("_____")[0]}
                  <span
                    className={`text-[${
                      quiz.answer === comments.submitAnswerList[index]
                        ? "#007AFF"
                        : "red"
                    }]`}
                  >
                    {quiz.optionList[quiz.answer - 1]?.replace(/\d+\./g, "")}
                  </span>
                  {quiz.question.split("_____")[1]}
                </>
              ) : (
                quiz.question
              )}
            </span>
            <div className="flex flex-col">
              <span className="mb-0 font-extrabold">제출 답안:</span>
              <span className="px-1 mb-2 text-lg font-semibold">
                {quiz.optionList[comments.submitAnswerList[index] - 1]?.replace(
                  /\d+\./g,
                  ""
                )}
              </span>
            </div>

            {/* Comment */}
            <div>
              <span className="text-lg font-extrabold ">Note:</span>
              <p className="px-1 text-[9px] text-gray-500">{quiz.comment}</p>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default Comments;
