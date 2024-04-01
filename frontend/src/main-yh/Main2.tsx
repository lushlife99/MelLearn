import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import { useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";

export const Main2 = (): JSX.Element => {
  const { chartData } = useSelector((state: RootState) => state.chart);
  const navigation = useNavigate();

  const goHome = () => {
    navigation("/home");
  };

  return (
    <div className="container flex justify-center w-full overflow-y-auto">
      <div className="flex  flex-col items-center w-full  bg-black max-w-[450px] h-full px-3 ">
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

        <div className="grid w-full h-full grid-cols-3 gap-4 ">
          {chartData.tracks.slice(0, 21).map((track, index) => (
            <div
              key={index}
              className="flex flex-col items-start hover:opacity-60"
            >
              <img
                alt="Artist Cover"
                src={track.album.cover[0].url}
                className="w-[125px] h-[125px] rounded-md "
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
