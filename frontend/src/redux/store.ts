import { configureStore } from "@reduxjs/toolkit";
import charReducer from "./chart/chartSlice";
import artistReducer from "./artist/artistSlice";
import playerReducer from "./player/playerSlice";
import trackMetaReducer from "./trackMeta/trackMetaSlice";
import recommendReducer from "./recommend/recommendSlice";
import speakingDataReducer from "./mockSpeaking/mockSpeakingSlice";

export const store = configureStore({
  reducer: {
    chart: charReducer,
    artist: artistReducer,
    recommend: recommendReducer,
    player: playerReducer,
    trackMeta: trackMetaReducer,
    speakingData: speakingDataReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
