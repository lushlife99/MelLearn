import { useQuery } from "react-query";
import { axiosSpotifyScraper } from "../api";
import { useLocation, useNavigate } from "react-router-dom";
import { IoIosArrowRoundBack } from "react-icons/io";
import Spinner from "react-bootstrap/Spinner";
import "../css/scroll.css";
interface Artist {
  id: string;
  type: string;
  name: string;
  shareUrl: string;
  visuals: {
    avatar: {
      url: string;
      width: number | null;
      height: number | null;
    }[];
  };
}
interface ArtistAlbum {
  albums: {
    items: { id: string; name: string; cover: { url: string }[] }[];
  };
}
interface LocationState {
  prevPath: string;
  artist: Artist;
}

export const Main4 = (): JSX.Element => {
  const location = useLocation();
  const navigate = useNavigate();
  const searchParams = new URLSearchParams(location.search);
  const artistId = searchParams.get("artistId");
  const { prevPath, artist } = location.state as LocationState;
  const goBack = () => {
    navigate(prevPath);
  };

  //Artist Album 조회
  const getArtistAlbum = async () => {
    const response = await axiosSpotifyScraper.get("/artist/albums", {
      params: {
        artistId,
      },
    });
    return response.data;
  };

  const { data: artistAlbum, isLoading: artistAlbumLoading } =
    useQuery<ArtistAlbum>(["artistAlbum", artistId], () => getArtistAlbum(), {
      staleTime: 10800000, //캐싱기간 3시간 설정
    });

  if (artistAlbumLoading) {
    return (
      <div className="flex justify-center w-full h-screen">
        <div className="flex flex-col justify-center items-center w-full bg-black max-w-[450px] h-full px-3 py-4">
          <Spinner animation="border" role="status" />
        </div>
      </div>
    );
  }

  return (
    <div className="flex justify-center w-full h-screen ">
      <div className="flex flex-col items-center w-full bg-black max-w-[450px]  px-3 py-4">
        {/* Artist 커버 이미지, 이름*/}
        <div className="flex flex-col items-center justify-center w-full ">
          <div className="flex items-start justify-around w-full">
            <div className="w-[21%] flex justify-start ">
              <IoIosArrowRoundBack
                onClick={goBack}
                className="w-12 h-12 fill-white hover:fill-gray-500"
              />
            </div>

            <img
              src={artist.visuals.avatar[0].url}
              className="w-[58%] h-[200px] rounded-md "
              alt="Artist Cover"
            />
            <div className="w-[21%] "></div>
          </div>

          <span className="font-bold text-[white] text-3xl mt-4">
            {artist.name}
          </span>
        </div>

        {/* Artist 앨범 목록 */}
        <div className="mt-12 overflow-y-auto scrollbar">
          {artistAlbum?.albums.items.map((album, index) => (
            <div
              key={album.id}
              className="flex items-center mb-4 hover:opacity-60"
            >
              <img
                className="w-[70px] h-[70px] rounded-md"
                src={album.cover[0].url}
                alt="Album Cover"
              />
              <div className="flex flex-col ml-3">
                <span className="text-[white] font-semibold">{album.name}</span>
                <span className="text-[gray] font-semibold">
                  {artist.name} 재생 시간
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
