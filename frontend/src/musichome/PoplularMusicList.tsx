import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import { useLocation, useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";
import "../css/scroll.css";

export const PopularMusicList = (): JSX.Element => {
  const { chartData } = useSelector((state: RootState) => state.chart);
  const navigation = useNavigate();
  const location = useLocation();

  const goHome = () => {
    navigation("/home");
  };
  const goPlayMusic = (track: any) => {
    navigation("/playMusic", {
      state: {
        track,
      },
    });
  };

  return (
    <div className="container flex justify-center w-full h-screen">
      <div className="flex  flex-col items-center w-full  bg-black max-w-[450px]  p-3">
        <div className="flex items-center justify-between w-full py-2 mb-3">
          <div className="w-[33%] h-10 flex justify-start items-center ">
            <IoIosArrowRoundBack
              onClick={goHome}
              className="w-12 h-12 fill-white hover:fill-gray-500"
            />
          </div>
          <span className="text-[18px] font-bold  text-white w-[33%] border border-black">
            인기 음악 목록
          </span>
          <div className="w-[33%]"></div>
        </div>

        <div className="grid w-full grid-cols-3 gap-4 px-3 overflow-y-auto scrollbar">
          {chartData.tracks.slice(0, 30).map((track, index) => (
            <div
              key={index}
              className="flex flex-col items-start hover:opacity-60"
              onClick={() => goPlayMusic(track)}
            >
              <img
                alt="Artist Cover"
                src={track.album.images[0].url}
                className="w-[125px] h-[110px] rounded-md "
              />
              <span className="text-[white] font-bold mt-2 overflow-hidden overflow-ellipsis ">
                {track.name}
              </span>
              <span className="text-bold text-[gray] text-[12px]">
                {track.artists[0].name}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
