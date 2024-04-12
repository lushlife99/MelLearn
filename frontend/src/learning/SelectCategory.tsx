import React, { useEffect, useState } from "react";
import BgCircle from "../components/BgCircle";
import { useLocation, useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";
import axiosApi, { axiosSpotifyScraper } from "../api";
import { Menu } from "antd";
import { useQuery } from "react-query";
import Spinner from "react-bootstrap/Spinner";

interface Category {
  name: string;
  value: boolean;
}

function SelectCategory() {
  const navigate = useNavigate();
  const location = useLocation();
  const { track } = location.state;

  const [category, setCategory] = useState("");
  const [lyric, setLyric] = useState();

  const {
    data: categories,
    isLoading,
    isError,
    refetch,
  } = useQuery<Category[]>(["categories", track.id], async () => {
    //서버에 보낼 가사 데이터
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}&format=json`
    );
    const res2 = await axiosApi.post(`/api/support/quiz/category`, res.data);
    return Object.entries(res2.data).map(([name, value]) => ({
      name,
      value: value as boolean,
    }));
  });

  const getLyric = async () => {
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}`
    );
    setLyric(res.data);
  };

  const handleMenuClick = (e: any) => {
    setCategory(e.key);
  };
  const goSolveProblem = async () => {
    console.log(category);
    if (
      category === "reading" ||
      category === "vocabulary" ||
      category === "grammar"
    ) {
      const res = await axiosApi.post(`/api/quiz/${category}`, {
        musicId: track.id,
        quizType: category.toUpperCase(),
        lyric,
      });
      if (res.status === 200) {
        navigate("/question", {
          state: {
            category,
            track,
          },
        });
      }

      console.log(category, res.data);
    } else if (category === "speaking") {
      navigate("/speaking", {
        state: {
          track,
        },
      });
    } else {
      const res = await axiosApi.post(`/api/quiz/${category}`, {
        musicId: track.id,
        quizType: category.toUpperCase(),
        lyric,
      });
      console.log(res.data);
    }
  };
  useEffect(() => {
    getLyric();
  }, []);

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col ">
        <BgCircle />
        {isLoading ? (
          <div className="flex items-center justify-center w-full h-screen">
            <Spinner animation="border" variant="primary"></Spinner>
          </div>
        ) : (
          <div className="z-10 ">
            <div className="px-4">
              <IoIosArrowRoundBack
                onClick={() => navigate(-1)}
                className="w-10 h-10 mt-4 fill-black hover:opacity-55"
              />
            </div>

            <img
              src="/images/image.png"
              alt="Category Cover"
              className="w-full mt-12 h-88"
            />

            <div className="fixed bottom-0 w-[430px] bg-white h-88 rounded-t-2xl flex justify-center flex-col items-center">
              <div className="flex justify-start w-full pt-4 pl-4">
                <span className="text-2xl font-extrabold text-black">
                  Category
                </span>
              </div>

              <Menu
                mode="vertical"
                className="flex flex-col items-center justify-center text-xl font-extrabold rounded-t-2xl"
                onSelect={handleMenuClick}
                items={categories?.map((category, index) => ({
                  key: category.name,
                  label: `${category.name}`,
                  disabled: !category.value,
                }))}
              />
              <div className="flex justify-center w-full ">
                <button
                  onClick={goSolveProblem}
                  className="bg-[#007AFF] w-[90%] h-12 rounded-xl mt-3 mb-3 text-white font-bold hover:opacity-60"
                >
                  다음
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default SelectCategory;
