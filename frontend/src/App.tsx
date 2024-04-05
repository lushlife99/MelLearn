import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";
import Login from "./login/Login";
import Join from "./login/Join";
import Setting from "./setting/Setting";
import { SpotifyLogo } from "./setting/SpotifyLogo";
import Callback from "./callback/Callback";
import MusicHome from "./musichome/MusicHome";
import { PopularMusicList } from "./musichome/PoplularMusicList";
import { PoplularArtistList } from "./musichome/PoplularArtistList";
import { ArtistDetial } from "./musichome/ArtistDetail";
import { SearchMusic } from "./musichome/SearchMusic";
import { Provider } from "react-redux";
import { store } from "./redux/store";
import PlayMusic from "./musichome/PlayMusic";

function App() {
  return (
    <Provider store={store}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />}></Route>
          <Route path="/setting" element={<Setting />}></Route>
          <Route path="/join" element={<Join />}></Route>
          <Route path="/spotify" element={<SpotifyLogo />}></Route>
          <Route path="/callback" element={<Callback />}></Route>
          <Route path="/home" element={<MusicHome />}></Route>
          <Route path="/home/main2" element={<PopularMusicList />}></Route>
          <Route path="/home/main3" element={<PoplularArtistList />}></Route>
          <Route path="/home/main5" element={<SearchMusic />}></Route>

          {/* TODO frontend -main 작업 시작 확인 후 지울것 */}

          <Route path="/main4" element={<ArtistDetial />}></Route>
          <Route path="/playMusic" element={<PlayMusic />}></Route>
        </Routes>
      </BrowserRouter>
    </Provider>
  );
}

export default App;
