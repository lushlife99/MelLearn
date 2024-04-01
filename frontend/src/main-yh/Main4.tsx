import { useQuery } from "react-query";
import { axiosSpotify } from "../api";
import { useLocation } from "react-router-dom";

interface ArtistImg {
  visuals: {
    avatar: { url: string }[];
  };
}
interface ArtistAlbum {
  albums: {
    items: { id: string; name: string; cover: { url: string }[] }[];
  };
}

export const Main4 = (): JSX.Element => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const artistId = searchParams.get("artistId");
  const artistName = searchParams.get("artistName");

  //Artist 커버 이미지 조회  -> 근데 확인해보니까 우리가 원래가지고있던 이미지랑 같아서 이것도 그냥 넘겨줄예정
  const getArtistImg = async () => {
    const response = await axiosSpotify.get("/artist/search", {
      params: {
        name: artistName,
      },
    });
    return response.data;
  };

  //Artist Album 조회
  const getArtistAlbum = async () => {
    const response = await axiosSpotify.get("/artist/albums", {
      params: {
        artistId,
      },
    });
    return response.data;
  };
  const { data: artistImg, isLoading: artistLoading } = useQuery<ArtistImg>(
    ["artistImg", artistId],
    () => getArtistImg()
  );

  const { data: artistAlbum, isLoading: artistAlbumLoading } =
    useQuery<ArtistAlbum>(["artistAlbum", artistId], () => getArtistAlbum());
  console.log(artistAlbum);
  return (
    <div className="flex justify-center w-full overflow-y-auto">
      <div className="flex flex-col items-center w-full bg-black max-w-[450px] h-full px-3 py-4">
        {/* Artist 커버 이미지, 이름*/}
        <div className="flex flex-col items-center justify-center">
          {artistAlbumLoading ? (
            <div>로딩중</div>
          ) : (
            <img
              className="w-[200px] h-[200px] rounded-md"
              src={artistImg?.visuals.avatar[0].url}
              alt="Artist Cover"
            />
          )}

          <span className="font-bold text-[white] text-3xl mt-4">
            {artistName}
          </span>
        </div>

        {/* Artist 앨범 목록 */}

        <div className="mt-16">
          {artistAlbum?.albums.items.map((album, index) => (
            <div
              key={album.id}
              className="flex items-center mb-3 hover:opacity-60"
            >
              <img
                className="w-[70px] h-[70px] rounded-md"
                src={album.cover[0].url}
                alt="Album Cover"
              />
              <div className="flex flex-col ml-3">
                <span className="text-[white] font-semibold">{album.name}</span>
                <span className="text-[gray] font-semibold">
                  {artistName} 곡 재생 시간
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
