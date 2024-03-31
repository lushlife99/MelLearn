import { configureStore } from "@reduxjs/toolkit";
import charReducer from "./chart/chartSlice";
export const store = configureStore({
  reducer: {
    chart: charReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
