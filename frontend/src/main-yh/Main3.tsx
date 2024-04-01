import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import { IoIosArrowRoundBack } from "react-icons/io";
import { useNavigate } from "react-router-dom";

interface Artist {
  id: string;
  type: string;
  name: string;
  shareUrl: string;
}

export const Main3 = (): JSX.Element => {
  const { artistData } = useSelector((state: RootState) => state.artist);
  const navigation = useNavigate();

  const goHome = () => {
    navigation("/home");
  };
  const goDetailArtist = (artist: Artist) => {
    navigation(`/main4?artistId=${artist.id}&artistName=${artist.name}`);
  };

  return (
    <div className="container flex justify-center w-full overflow-y-auto">
      <div className="flex  flex-col items-center w-full  bg-black max-w-[450px] h-full px-3 ">
        <div className="flex items-center justify-between w-full py-2">
          <div className="w-[33%] h-10 flex justify-start items-center ">
            <IoIosArrowRoundBack
              onClick={goHome}
              className="w-12 h-12 fill-white hover:fill-gray-500"
            />
          </div>
          <span className="text-[18px] font-bold  text-white w-[33%] border border-black">
            인기 가수 목록
          </span>
          <div className="w-[33%]"></div>
        </div>

        <div className="grid w-full h-full grid-cols-3 gap-4 py-4">
          {artistData.artists.slice(0, 21).map((artist, index) => (
            <div
              key={index}
              className="flex flex-col items-center hover:opacity-60"
              onClick={() => goDetailArtist(artist)}
            >
              <img
                alt="Artist Cover"
                src={artist.visuals.avatar[0].url}
                className="w-[125px] h-[125px] rounded-full hover:opacity-70"
              />
              <span className="text-[white] font-bold mt-2">{artist.name}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
