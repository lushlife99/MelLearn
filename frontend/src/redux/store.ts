import { configureStore } from "@reduxjs/toolkit";
import charReducer from "./chart/chartSlice";
import artistReducer from "./artist/artistSlice";
export const store = configureStore({
  reducer: {
    chart: charReducer,
    artist: artistReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
