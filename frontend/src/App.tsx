import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import "./App.css";
import Login from "./login/Login";
import Join from "./login/Join";
import Setting from "./setting/Setting";
import {SpotifyLogo} from "./setting/SpotifyLogo";
import Callback from "./callback/Callback";

function App() {
  return (
    <div className="">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />}></Route>
          <Route path="/setting" element={<Setting />}></Route>
          <Route path="/join" element={<Join />}></Route>
          <Route path="/spotify" element={<SpotifyLogo />}></Route>
          <Route path="/callback" element={<Callback />}></Route>
          {/*<Route path="/callback/:error" element={<Callback />}></Route>*/}
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
