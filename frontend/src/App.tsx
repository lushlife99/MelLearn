import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import "./App.css";
import Login from "./login/Login";
import Join from "./login/Join";
import Setting from "./setting/Setting";
import { SpotifyLogo } from "./setting/SpotifyLogo";
import Callback from "./callback/Callback";
import MusicHome from "./musichome/MusicHome";

import {Main2} from "./main-yh/Main2";
import {Main3} from "./main-yh/Main3";
import {Main4} from "./main-yh/Main4";
import {Main5} from "./main-yh/Main5";
import { Provider } from "react-redux";
import { store } from "./redux/store";


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


          {/* TODO frontend -main 작업 시작 확인 후 지울것 */}
          <Route path="/home/main2" element={<Main2 />}></Route>
          <Route path="/home/main3" element={<Main3 />}></Route>
          <Route path="/main4" element={<Main4 />}></Route>
          <Route path="/main5" element={<Main5 />}></Route>
        </Routes>
      </BrowserRouter>
    </Provider>
  );
}

export default App;
