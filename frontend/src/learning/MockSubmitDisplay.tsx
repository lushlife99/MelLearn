import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";

interface Submission {
  title: string;
  submissions: string[] | number[];
}
const MockSubmitDisplay: React.FC<Submission> = ({ title, submissions }) => {
  const getIndex = (index: number) => {
    switch (title) {
      case "Grammar":
        return index + 1;
      case "Reading":
        return index + 6;
      case "Vocabulary":
        return index + 11;
      case "Listening":
        return index + 16;
    }
  };
  return (
    <div>
      <span className="font-bold">{title}</span>
      <div className="p-2 border border-black">
        {submissions.map((answer, index) => (
          <div key={index} className="flex ">
            <span>
              {getIndex(index)}. {answer}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MockSubmitDisplay;
