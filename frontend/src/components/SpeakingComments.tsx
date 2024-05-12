interface SpeakingComment {
  markedText: string;
  submit: string;
}
interface ISpeakingComment {
  comments: SpeakingComment;
}
function SpeakingComments({ comments }: ISpeakingComment) {
  const s =
    "__do __you __hear __me __im __talking __to __you\n__across __the __water __across __the __deep __blue __ocean";

  return (
    <div className="w-full h-[90%] mb-8 text-2xl text-black ">
      {comments.markedText
        .split(/[\s\n]+/)
        .map((part: string, index: number) => {
          const cleanedPart =
            part.startsWith("__") || part.startsWith("\n__") ? (
              <span key={index} className="text-[#FF0000] font-bold ">
                {part.substring(2)}{" "}
              </span>
            ) : (
              <span className="font-bold" key={index}>
                {part}{" "}
              </span>
            );
          return cleanedPart;
        })}
    </div>
  );
}

export default SpeakingComments;
