import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface FormDataState {
  speakingData: FormData | null;
}

const initialState: FormDataState = {
  speakingData: null,
};

const speakingDataSlice = createSlice({
  name: "speakingData",
  initialState,
  reducers: {
    setSpeakingData: (state, action: PayloadAction<FormData>) => {
      state.speakingData = action.payload;
    },
  },
});

export const { setSpeakingData } = speakingDataSlice.actions;
export default speakingDataSlice.reducer;
