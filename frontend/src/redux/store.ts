import { configureStore } from "@reduxjs/toolkit";
import charReducer from "./chart/chartSlice";
import artistReducer from "./artist/artistSlice";
import playerReducer from "./player/playerSlice";

export const store = configureStore({
  reducer: {
    chart: charReducer,
    artist: artistReducer,
    player: playerReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
