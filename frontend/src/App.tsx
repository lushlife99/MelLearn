import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";
import Login from "./login/Login";
import Join from "./login/Join";
import Setting from "./settingview/Setting";
import { SpotifyLogo } from "./settingview/SpotifyLogo";
import Callback from "./callback/Callback";
import MusicHome from "./musichome/MusicHome";
import { PopularMusicList } from "./musichome/PoplularMusicList";
import { PoplularArtistList } from "./musichome/PoplularArtistList";
import { ArtistDetial } from "./musichome/ArtistDetail";
import { SearchMusic } from "./musichome/SearchMusic";
import { Provider } from "react-redux";
import { store } from "./redux/store";
import PlayMusic from "./musichome/PlayMusic";
import Speaking from "./learning/Speaking";
import { QuestionBoardV2 } from "./learning/QuestionBoardV2";
import { Score } from "./learning/Score";
import Commentary from "./learning/Commentary";
import { Rank } from "./learning/Rank";
import Listening from "./learning/Listening";
import SelectCategory from "./learning/SelectCategory";
import SpeakingScore from "./learning/SpeakingScore";
import ListeningScore from "./learning/ListeningScore";
import { RecommendMusicList } from "./musichome/RecommendMusicList";
import MockExam from "./mockExams/MockExam";
import SearchExam from "./mockExams/SearchExam";
import MockComment from "./mockExams/MockComment";
import History from "./problemHistory/History";

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
          <Route path="/home/main6" element={<RecommendMusicList />}></Route>
          <Route path="/home/main2" element={<PopularMusicList />}></Route>
          <Route path="/home/main3" element={<PoplularArtistList />}></Route>
          <Route path="/home/main5" element={<SearchMusic />}></Route>

          <Route path="/main4" element={<ArtistDetial />}></Route>
          <Route path="/playMusic" element={<PlayMusic />}></Route>
          <Route path="/category" element={<SelectCategory />}></Route>
          <Route path="/speaking" element={<Speaking />}></Route>

          <Route path="/question" element={<QuestionBoardV2 />}></Route>
          <Route path="/score" element={<Score />}></Route>
          <Route path="/comment" element={<Commentary />}></Route>
          <Route path="/rank" element={<Rank />}></Route>
          <Route path="/listening" element={<Listening />}></Route>
          <Route path="/lsScore" element={<ListeningScore />}></Route>
          <Route path="/speakingScore" element={<SpeakingScore />}></Route>
          <Route path="/compQuiz" element={<SearchExam />}></Route>
          <Route path="/mockExam" element={<MockExam />}></Route>
          <Route path="/mockComment" element={<MockComment />}></Route>
          <Route path="/history" element={<History />}></Route>
        </Routes>
      </BrowserRouter>
    </Provider>
  );
}

export default App;
