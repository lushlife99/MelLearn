import React from "react";

interface IExample {
  text: string;
}
function HistoryList({ text }: IExample) {
  return <div className="text-white">{text}</div>;
}

export default HistoryList;
