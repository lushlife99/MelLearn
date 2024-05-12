import React, { useEffect, useState } from "react";
import axiosApi, { axiosSpotify } from "../api";
import { useNavigate } from "react-router-dom";
import Pagination from "react-bootstrap/Pagination";

interface History {
  content: {
    id: number;
    quizList: {
      id: number;
      musicId: string;
      level: number;
      quizzes: {
        id: number;
        question: string;
        answer: number;
        comment: string;
        correctRate: number;
        optionList: string[];
      }[];
    };
    score: number;
    submitAnswerList: number[];
  }[];
  number: number;
  numberOfElements: number;
  size: number;
  totalPages: number;
}
interface IHistory {
  quizType: string;
}
function HistoryList({ quizType }: IHistory) {
  const [tracks, setTracks] = useState();
  const [history, setHistory] = useState<History>();
  const [page, setPage] = useState(1);
  const [pageGroup, setPageGroup] = useState(1);
  const navigate = useNavigate();

  const fetchHistory = async (page: number) => {
    const upperQuiz = quizType.toUpperCase();
    const res = await axiosApi.get(
      `/api/quiz/submit?page=${page - 1}&quizType=${upperQuiz}`
    );
    const musicIds = res.data.content.map((item: any) => item.quizList.musicId);
    const idsParam = musicIds.join(",");
    // spotify api 요청

    setHistory(res.data);
  };
  const handlePageChange = (pageNumber: number) => {
    setPage(pageNumber);
    fetchHistory(pageNumber);
    setPageGroup(Math.ceil(pageNumber / 10));
  };
  const goComment = (item: any) => {
    navigate("/comment", {
      state: {
        comments: item,
      },
    });
  };
  useEffect(() => {
    setPage(1);
    fetchHistory(1);
  }, [quizType]);
  if (!history) {
    return <div className="text-white">Loading...</div>;
  }
  return (
    <div className="w-full h-full px-4 text-white">
      {history.content.map((item, index) => (
        <div key={index} className="flex justify-between h-16 my-2 ">
          <div className="">
            <img alt="" className="w-16 h-16" />
          </div>
          <div className="flex flex-col items-start justify-center mr-32">
            <span className="font-bold text-md">Lucky</span>
            <span className="text-[#DED9D9] text-sm">Json Mraz</span>
          </div>
          <div className="flex flex-col items-center justify-center mr-4 ">
            <span className="font-bold">{item.score}점</span>
            <button
              onClick={() => goComment(item)}
              className="font-bold bg-[#1889FE] h-4 text-xs w-20 rounded-sm hover:opacity-60 flex items-center justify-center"
            >
              <span>해설 보기</span>
            </button>
          </div>
        </div>
      ))}
      <div className="flex items-end justify-center w-full fixed-bottom">
        <Pagination>
          <Pagination.First onClick={() => handlePageChange(1)} />
          <Pagination.Prev
            onClick={() => setPageGroup(pageGroup - 1)}
            disabled={pageGroup === 1}
          />
          {Array.from({ length: 10 }, (_, i) => (pageGroup - 1) * 10 + i + 1)
            .filter((pageNumber) => pageNumber <= history?.totalPages)
            .map((pageNumber) => (
              <Pagination.Item
                key={pageNumber}
                active={pageNumber === page}
                onClick={() => handlePageChange(pageNumber)}
              >
                {pageNumber}
              </Pagination.Item>
            ))}
          <Pagination.Next
            onClick={() => setPageGroup(pageGroup + 1)}
            disabled={pageGroup === Math.ceil(history?.totalPages / 10)}
          />
          <Pagination.Last
            onClick={() => handlePageChange(history?.totalPages)}
          />
        </Pagination>
      </div>
    </div>
  );
}

export default HistoryList;
