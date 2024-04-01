import React, { useEffect, useState } from "react";

import BgCircle from "../components/BgCircle";
import { FaMagnifyingGlass } from "react-icons/fa6";
import BottomNavigation from "@mui/material/BottomNavigation";
import BottomNavigationAction from "@mui/material/BottomNavigationAction";
import HomeIcon from "@mui/icons-material/Home";
import SettingsIcon from "@mui/icons-material/Settings";
import HistoryIcon from "@mui/icons-material/History";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import { useNavigate } from "react-router-dom";
import { Swiper, SwiperSlide } from "swiper/react";
import { Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/pagination";
import "../css/slider.css";
import { useDispatch, useSelector } from "react-redux";
import { fetchChartData } from "../redux/chart/chartAction";
import { RootState } from "../redux/store";
import { UnknownAction } from "redux";
import { Link } from "react-router-dom";
import Spinner from "react-bootstrap/Spinner";
import axios from "axios";
import { fetchArtistData } from "../redux/artist/artistAction";

function MusicHome() {
  const [page, setPage] = useState(0);
  const navigation = useNavigate();

  const dispatch = useDispatch();
  const { chartData, chartLoading, chartError } = useSelector(
    (state: RootState) => state.chart
  );
  const { artistData, artistLoading, artistError } = useSelector(
    (state: RootState) => state.artist
  );
  console.log(artistData);

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setPage(newValue);
    switch (newValue) {
      case 0:
        navigation("/home");
        break;
      case 1:
        //navigation("/word");
        break;
      case 2:
        //navigation("/history");
        break;
      case 3:
        navigation("/setting");
        break;
      default:
        break;
    }
  };

  //리액트 쿼리 사용 -> 메인화면 올때 멤버 정보를 받아서 langtype으로 en ,jp 구분해서
  //플레이리스트를 보여줘야함
  // 메인환경올때마다 이걸 요청? -> 리액트 쿼리 이용해서 불필요한 호출 방지 되면 환경설정에서도 적용

  useEffect(() => {
    dispatch(fetchChartData() as unknown as UnknownAction); //인기 노래
    dispatch(fetchArtistData() as unknown as UnknownAction); // 인기 가수
  }, [dispatch]);

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="relative bg-[#9bd1e5] overflow-hidden w-full max-w-[450px] h-screen  flex flex-col ">
        <BgCircle />

        {/* 타이틀*/}
        <div className="z-10 flex items-center justify-between h-16 px-3 mb-2 bg-white">
          <span className="text-[22px] font-bold">MelLearn</span>
          <FaMagnifyingGlass className="w-5 h-5 hover:text-gray-300" />
        </div>

        {/* 사용자 추천 음악*/}
        <div className="z-10 px-3 mt-4">
          <div className="flex justify-between">
            <span className="text-[22px] font-extrabold px-2">
              사용자 추천 음악
            </span>
            <Link
              to={""}
              className="text-[#4B8E96] hover:text-[#6BC2B9] text-decoration-none"
            >
              See all
            </Link>
          </div>
          <Swiper
            spaceBetween={10}
            slidesPerView={1.5}
            onSlideChange={() => console.log("slide change")}
            onSwiper={(swiper: any) => console.log(swiper)}
            modules={[Pagination]}
            loop={true}
            className="mySwiper"
          >
            <SwiperSlide>
              <img src="" className="w-[200px]" />
            </SwiperSlide>
            <SwiperSlide>Slide 2</SwiperSlide>
            <SwiperSlide>Slide 3</SwiperSlide>
            <SwiperSlide>Slide 4</SwiperSlide>
          </Swiper>
        </div>

        {/* 인기 가수*/}
        <div className="z-10 px-3 mt-4">
          <div className="flex justify-between ">
            <span className="text-[22px] font-extrabold px-2">인기 가수</span>

            <Link
              to={"/home/main3"}
              className="text-[#4B8E96] hover:text-[#6BC2B9] text-decoration-none"
            >
              See all
            </Link>
          </div>
          {!artistLoading ? (
            <Swiper
              spaceBetween={10}
              slidesPerView={3.3}
              //onSlideChange={() => console.log("slide change")}
              //onSwiper={(swiper: any) => console.log(swiper)}
              modules={[Pagination]}
              loop={true}
              className="mySwiper"
            >
              {artistData.artists.slice(0, 10).map((artist, index) => (
                <SwiperSlide
                  key={index}
                  className="swiper-slide-mini hover:bg-slate-400 "
                >
                  <img
                    src={artist.visuals.avatar[0].url}
                    alt="Artist Cover"
                    className="p-4 img-circle"
                  />
                  <span className="pb-3 font-bold text-[15px]">
                    {artist.name}
                  </span>
                </SwiperSlide>
              ))}
            </Swiper>
          ) : (
            <div className="flex items-center justify-center h-[300%]">
              <Spinner className="border" variant="primary" />
            </div>
          )}
        </div>

        {/* 인기 음악*/}
        <div className="z-10 px-3 mt-4">
          <div className="flex justify-between ">
            <span className="text-[22px] font-extrabold px-2">인기 음악</span>

            <Link
              to={"/home/main2"}
              className="text-[#4B8E96] hover:text-[#6BC2B9] text-decoration-none"
            >
              See all
            </Link>
          </div>

          {!chartLoading ? (
            <Swiper
              spaceBetween={10}
              slidesPerView={2.1}
              //onSlideChange={() => console.log("slide change")}
              //onSwiper={(swiper: any) => console.log(swiper)}
              modules={[Pagination]}
              loop={true}
              className="mySwiper"
            >
              {chartData.tracks.slice(0, 10).map((track, index) => (
                <SwiperSlide
                  key={index}
                  className="swiper-slide-mid hover:bg-slate-400"
                  onClick={() => console.log("click")}
                >
                  <img
                    src={track.album.cover[0]?.url}
                    className="p-2"
                    alt="Album Cover"
                  />
                  <span className="px-3 overflow-hidden font-extrabold whitespace-nowrap overflow-ellipsis">
                    {track.name}
                  </span>
                  <div className="flex px-3 overflow-hidden overflow-ellipsis">
                    {track.artists.length < 4 ? (
                      track.artists.map((artist, index) => (
                        <span
                          key={index}
                          className=" text-[#93989D] font-bold text-[15px] whitespace-nowrap "
                        >
                          {artist.name}
                          {index !== track.artists.length - 1 && ", "}
                        </span>
                      ))
                    ) : (
                      <span className=" text-[#93989D] font-bold text-[15px]">
                        Various Artists
                      </span>
                    )}
                  </div>
                </SwiperSlide>
              ))}
            </Swiper>
          ) : (
            <div className="flex items-center justify-center h-[300%]">
              <Spinner className="border" variant="primary" />
            </div>
          )}
        </div>

        {/* <div className="bottom-0 left-0 right-0 z-10 w-full "> */}
        <BottomNavigation
          className="fixed bottom-0 w-[450px]"
          showLabels
          value={page}
          onChange={handleChange}
        >
          <BottomNavigationAction
            className="hover:bg-slate-400"
            label="홈"
            icon={<HomeIcon />}
          />
          <BottomNavigationAction
            className="hover:bg-slate-400"
            label="단어장"
            icon={<MenuBookIcon />}
          />
          <BottomNavigationAction
            className="hover:bg-slate-400"
            label="히스토리"
            icon={<HistoryIcon />}
          />
          <BottomNavigationAction
            className="hover:bg-slate-400"
            label="설정"
            icon={<SettingsIcon />}
          />
          ;
        </BottomNavigation>
        {/* </div> */}
      </div>
    </div>
  );
}

export default MusicHome;
