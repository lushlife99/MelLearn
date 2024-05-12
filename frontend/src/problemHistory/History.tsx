import React, { useEffect, useState } from "react";
import axiosApi from "../api";
import { IoIosArrowRoundBack } from "react-icons/io";
import HistoryList from "./HistoryList";
import { useNavigate } from "react-router-dom";

const History = () => {
  const navigate = useNavigate();
  const [value, setValue] = React.useState(0);
  const categories = [
    "Reading",
    "Grammar",
    "Vocabulary",
    "Listening",
    "Speaking",
  ];
  const [quizType, setQuizType] = useState("Reading");
  const [history, setHistory] = useState();

  //예를들어 Reading 히스토리 요청함
  // results -> Reading uri 요청한 값
  // 134 line map함수로 뿌려줌

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };
  const fetchHistory = async (quizType: string) => {
    const res = await axiosApi.get(`/api/quiz/submit?quizType=${quizType}`);
    console.log(res.data);
    setHistory(res.data);
  };
  useEffect(() => {
    fetchHistory("VOCABULARY");
  }, []);

  const onClickCategory = (category: string) => {
    const upperCategory = category.toUpperCase();
    setQuizType(category);
    //fetchHistory(upperCategory);
  };

  return (
    <>
      <div className="bg-[white] flex flex-row justify-center w-full h-screen font-roboto">
        <div className="bg-[black] overflow-hidden w-[450px] h-screen relative flex flex-col">
          <div className="flex items-center justify-between px-8 mt-4 ">
            <div className="w-[33%] hover:opacity-60">
              <IoIosArrowRoundBack
                onClick={() => navigate("/home")}
                className="w-10 h-10 fill-white"
              />
            </div>
            <div className="w-[33%]">
              <span className="text-3xl font-bold text-white">히스토리</span>
            </div>

            <div className="w-[33%]"></div>
          </div>
          <div className="flex items-end w-full mt-4">
            {categories.map((category, index) => (
              <div
                key={index}
                onClick={() => onClickCategory(category)}
                className={`${
                  quizType === category
                    ? "text-[#3D5AF1] h-16 rounded-t-xl"
                    : "text-[#C0C0C0] "
                } flex items-center justify-center w-24 h-12 bg-white font-bold text-lg px-2 hover:opacity-60`}
              >
                <div>
                  <span>{category}</span>
                </div>
              </div>
            ))}
          </div>
          <div>
            <HistoryList text={quizType} />
          </div>
        </div>
      </div>
    </>
  );
};

export default History;
