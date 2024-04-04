import React, { useState } from "react";
import { FaMagnifyingGlass } from "react-icons/fa6";
import { axiosSpotifyScraper } from "../api";
import { Link } from "react-router-dom";
import "../css/scroll.css";
import { useQuery } from "react-query";
import { FaPlay } from "react-icons/fa";
import { LuPencilLine } from "react-icons/lu";
import Spinner from "react-bootstrap/Spinner";
interface Album {
  items: {
    id: string;
    name: string;
    artists: {
      id: string;
      name: string;
    }[];
    cover: {
      url: string;
    }[];
  }[];
}

export const Main5 = () => {
  const [search, setSearch] = useState("");

  // TODO 작업중
  const getArtistAlbum = async (search: string) => {
    const response = await axiosSpotifyScraper.get("/search", {
      params: {
        term: search,
      },
    });
    return response.data.albums;
  };
  const {
    data: albums,
    isLoading,
    refetch,
  } = useQuery<Album>(["artistAlbums", search], () => getArtistAlbum(search), {
    enabled: false, // 초기에는 비활성화
  });
  const onChangeInput = (event: React.FormEvent<HTMLInputElement>) => {
    setSearch(event.currentTarget.value);
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault(); // 기본 동작 중지
    // Search 요청 을 보낸다.
    refetch();
  };

  return (
    <div className="flex justify-center w-full h-screen">
      <div className="flex flex-col items-center w-full bg-[black] max-w-[450px] px-8 py-12 ">
        {/* 검색 창 */}
        <form
          onSubmit={handleSubmit}
          className="flex items-center justify-between w-full px-2"
        >
          <div className="relative flex items-center rounded-md">
            <input
              value={search}
              onChange={onChangeInput}
              className="bg-[#282828] rounded-md h-10 p-3 w-72 text-[white]"
            />
            <button type="submit">
              <FaMagnifyingGlass className="w-5 h-5 fill-[white] hover:opacity-60 absolute right-2 bottom-2" />
            </button>
          </div>
          <Link to={"/home"} className="text-decoration-none">
            <span className="text-lg text-white hover:opacity-60">취소</span>
          </Link>
        </form>

        {/* 검색한 가수 앨범 목록*/}
        <div className="w-full px-2 mt-8 overflow-y-auto scrollbar">
          {isLoading ? (
            <div className="flex flex-col justify-center items-center w-full bg-black max-w-[450px] h-full px-3 py-4">
              <Spinner animation="border" role="status" />
            </div>
          ) : (
            albums?.items.map((album, index) => (
              <div key={index} className=" rounded-2xl bg-[#282828]  h-40 mb-4">
                <div className="flex items-center p-4">
                  <img
                    src={album.cover[1].url}
                    alt="Album Cover"
                    className="rounded-md"
                  />
                  <div className="flex flex-col justify-between ml-4 ">
                    <span className="text-bold text-[white] mb-1">
                      {album.name.length > 30
                        ? `${album.name.slice(0, 30)}...`
                        : album.name}
                    </span>
                    <span className="text-[#B3B3B3] text-sm font-semibold">
                      {album.artists[0].name}
                    </span>
                  </div>
                </div>
                <div className="flex justify-between px-4">
                  <button className="bg-[white] w-32 rounded-2xl h-8 hover:opacity-60 flex items-center justify-center">
                    <FaPlay className="mr-2" />
                    <span className="font-bold">재생</span>
                  </button>
                  <button className="bg-[white] w-32 rounded-2xl h-8 hover:opacity-60 flex items-center justify-center">
                    <LuPencilLine className="mr-2 fill-black" />
                    <span className="font-bold">공부</span>
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
