import React, { useEffect, useState } from "react";
import BgCircle from "../components/BgCircle";
import { useLocation, useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";
import axiosApi, { axiosSpotifyScraper } from "../api";
import { Menu } from "antd";

interface Category {
  name: string;
  value: boolean;
}

function SelectCategory() {
  const navigate = useNavigate();
  const location = useLocation();
  const { track } = location.state;

  const [categories, setCategories] = useState<Category[]>([]);
  const [category, setCategory] = useState("");
  const [lyric, setLyric] = useState();

  const fetchAvailableCategory = async () => {
    const res0 = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}`
    );
    console.log(res0.data);
    setLyric(res0.data);
    const res = await axiosSpotifyScraper.get(
      `/track/lyrics?trackId=${track.id}&format=json`
    );

    const res2 = await axiosApi.post(`/api/support/quiz/category`, res.data);
    const categoriesArray: Category[] = Object.entries(res2.data).map(
      ([name, value]) => ({
        name,
        value: value as boolean,
      })
    );
    setCategories(categoriesArray);
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
      console.log(res.data);
    }
  };
  useEffect(() => {
    fetchAvailableCategory();
  }, []);

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col ">
        <BgCircle />
        <div className="z-10 ">
          <IoIosArrowRoundBack
            onClick={() => navigate(-1)}
            className="w-10 h-10 mt-4 fill-black hover:opacity-60"
          />
          <img />

          <div className="fixed bottom-0 w-[430px] bg-white h-80 rounded-t-2xl">
            <Menu
              mode="vertical"
              className="flex flex-col items-center justify-center text-xl font-bold rounded-t-2xl"
              onSelect={handleMenuClick}
              items={categories.map((category, index) => ({
                key: category.name,
                label: `${index + 1}. ${category.name}`,
                disabled: !category.value,
              }))}
            />
            <div
              className="flex justify-center w-full bg-[
            #007AFF]
               "
            >
              <button onClick={goSolveProblem} className="">
                다음
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SelectCategory;
