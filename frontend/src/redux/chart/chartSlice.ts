import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface Artist {
  type: string;
  id: string;
  name: string;
  shareUrl: string;
}
interface Album {
  cover: {
    url: string;
    width: number | null;
    height: number | null;
  }[];
  type: string;
}
interface Track {
  album: Album;
  artists: Artist[];
  id: string;
  name: string;
  shareUrl: string;
  type: string;
}

interface ChartData {
  tracks: Track[];
}

interface ChartState {
  data: ChartData;
  chartLoading: boolean;
  error: string | null;
}

const initialState: ChartState = {
  data: { tracks: [] },
  chartLoading: false,
  error: null,
};

const charSlice = createSlice({
  name: "chart",
  initialState,
  reducers: {
    fetchChartStart(state) {
      state.chartLoading = true;
      state.error = null;
    },
    fetchChartSuccess(state, action: PayloadAction<ChartData>) {
      state.data = action.payload;
      state.chartLoading = false;
    },
    fetchChartError(state, action: PayloadAction<string>) {
      state.chartLoading = false;
      state.error = action.payload;
    },
  },
});
export const { fetchChartStart, fetchChartSuccess, fetchChartError } =
  charSlice.actions;
export default charSlice.reducer;
