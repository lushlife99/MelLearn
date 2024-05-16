import React, { useState } from "react";
import { FaMagnifyingGlass } from "react-icons/fa6";
import { axiosSpotify } from "../api";
import { Link, useNavigate } from "react-router-dom";
import "../css/scroll.css";
import { useQuery } from "react-query";
import Spinner from "react-bootstrap/Spinner";
import MenuBookIcon from "@mui/icons-material/MenuBook";

interface SearchTrack {
  items: {
    album: {
      images: {
        url: string;
      }[];
    };
    artists: {
      id: string;
      name: string;
    }[];
    is_playable: boolean;
    name: string;
    type: string;
    uri: string;
    duration_ms: number;
  }[];
}

function SearchExam() {
  const [search, setSearch] = useState("");
  const navigate = useNavigate();

  const getArtistAlbum = async (search: string) => {
    const response = await axiosSpotify.get(`/search?q=${search}&type=track`);
    return response.data.tracks;
  };
  const {
    data: tracks,
    isLoading,
    refetch,
  } = useQuery<SearchTrack>(
    ["artistAlbums", search],
    () => getArtistAlbum(search),
    {
      enabled: false, // 초기에는 비활성화
    }
  );
  const onChangeInput = (event: React.FormEvent<HTMLInputElement>) => {
    setSearch(event.currentTarget.value);
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault(); // 기본 동작 중지
    // Search 요청 을 보낸다.
    refetch();
  };

  const goQuiz = (track: any) => {
    navigate("/mockExam", {
      state: {
        track,
      },
    });
  };

  return (
    <div className="flex justify-center w-full h-screen">
      <div
        className={`flex flex-col items-center w-full bg-[black]  px-8 py-12 `}
      >
        {/* 검색 창 */}
        <form
          onSubmit={handleSubmit}
          className="flex items-center justify-between w-full px-2"
        >
          <div className="relative flex items-center w-full rounded-md">
            <input
              value={search}
              onChange={onChangeInput}
              className="bg-[#282828] rounded-md h-10 p-3 w-[85%] text-[white]"
              placeholder="노래를 검색해주세요"
            />
            <button type="submit">
              <FaMagnifyingGlass className="w-5 h-5 fill-[white] hover:opacity-60 absolute sm:right-14 bottom-2 right-56" />
            </button>
            <Link to={"/home"} className="text-decoration-none">
              <span className="ml-2 text-white text-md hover:opacity-60">
                취소
              </span>
            </Link>
          </div>
        </form>

        {/* 검색한 가수 앨범 목록*/}
        <div className="w-full px-2 mt-8 overflow-y-auto scrollbar">
          {isLoading ? (
            <div className="flex flex-col justify-center items-center w-full bg-black max-w-[450px] h-full px-3 py-4">
              <Spinner animation="border" role="status" />
            </div>
          ) : (
            tracks?.items
              .filter((track) => track.is_playable)
              .map((track, index) => (
                <div
                  key={index}
                  className=" rounded-2xl bg-[#282828]  h-40 mb-4"
                >
                  <div className="flex items-center p-4">
                    <img
                      src={track.album.images[2].url}
                      alt="Album Cover"
                      className="rounded-md"
                    />
                    <div className="flex flex-col justify-between ml-4 ">
                      <span className="text-bold text-[white] ">
                        {track.name.length > 30
                          ? `${track.name.slice(0, 25)}...`
                          : track.name}
                      </span>
                      <span className="text-[#B3B3B3] text-sm font-semibold">
                        {track.artists[0].name}
                      </span>
                    </div>
                  </div>
                  <div className="flex justify-between px-4">
                    <button
                      onClick={() => goQuiz(track)}
                      className="bg-[white] w-32 rounded-2xl h-8 hover:opacity-60 flex items-center justify-center"
                    >
                      <MenuBookIcon className="mr-2" />
                      <span className="font-bold">모의고사 </span>
                    </button>
                  </div>
                </div>
              ))
          )}
        </div>
      </div>
    </div>
  );
}
export default SearchExam;
