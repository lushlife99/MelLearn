import React from "react";

interface SpeakingComment {
  markedText: string;
  submit: string;
}
interface ISpeakingComment {
  comments: SpeakingComment;
}
function SpeakingComments({ comments }: ISpeakingComment) {
  return (
    <div className="w-full px-4 py-2 mb-8 overflow-y-auto text-black bg-white rounded-xl h-96 shadow-[0px_4px_4px_#00000040]">
      {comments.markedText.split(" ").map((part: string, index: number) => {
        const cleanedPart = part.startsWith("__") ? (
          <span key={index} className="text-[#FF0000] font-bold text-lg">
            {part.substring(2)}{" "}
          </span>
        ) : (
          <span className="text-lg font-bold" key={index}>
            {part}{" "}
          </span>
        );
        return cleanedPart;
      })}
    </div>
  );
}

export default SpeakingComments;
