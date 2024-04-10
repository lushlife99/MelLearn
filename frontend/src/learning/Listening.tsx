import React, { useState } from "react";
import Input from "@mui/material/Input";
import TextField from "@mui/material/TextField";
import Button from "@mui/joy/Button";

const Listening = () => {
  const [lylic, setLylic] = useState({
    blankedText:
      "Well, now then, mardy bum\n" +
      "I've seen your __ and it's like looking down\n" +
      "The __ of a gun\n" +
      "And it goes off\n" +
      "And out __ all these words\n" +
      "Oh, there's a very __ side to you\n" +
      "A side I much __\n",
    answerList: ["frown", "argumentative", "things", "pleasant", "prefer"],
  });

  const [userAnswers, setUserAnswers] = useState(
    Array(lylic.answerList.length).fill("")
  );

  const handleAnswerChange = (index: any, event: any) => {
    const newAnswers = [...userAnswers];
    newAnswers[index] = event.target.value;
    setUserAnswers(newAnswers);
  };

  return (
    <>
      <div className="bg-[#121111] flex flex-row justify-center w-full">
        <div className="bg-[#121111] w-[400px] h-[800px] relative">
          <div className="absolute w-[84px] top-[29px] left-[137px]  font-bold text-white text-[44px] tracking-[0] leading-[normal] whitespace-nowrap">
            가사
          </div>
          <div
            className="absolute max-w-[400px]  top-[90px] left-[10px] [font-family:'Acme-Regular',Helvetica] font-normal text-white text-[20px] tracking-[1.44px] leading-[normal] overflow-hidden whitespace-normal"
            style={{ lineHeight: "35px" }}
          >
            {lylic.blankedText.split("__").map((part, index) => (
              <React.Fragment key={index}>
                {part}
                {index !== lylic.blankedText.split("__").length - 1 && (
                  <TextField
                    variant="standard"
                    type="text"
                    value={userAnswers[index]}
                    onChange={(event) => handleAnswerChange(index, event)}
                    InputProps={{
                      style: {
                        color: "white",
                        width: "80px",
                        fontSize: "20px",
                      },
                    }}
                    focused
                  />
                )}
              </React.Fragment>
            ))}
            <div>
              <p>-----------------------------------</p>
              <p>내가 적은 정답</p>
              {userAnswers.map((answer, index) => (
                <span key={index}>{answer} </span>
              ))}
              <Button value="여기에 Backend에 채점 요청 유저 정답 넣어서 보내기 " />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Listening;
