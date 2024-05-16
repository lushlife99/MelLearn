import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface PlayerState {
  player: Spotify.Player | null;
}
const initialState: PlayerState = {
  player: null,
};

const playerSlice = createSlice({
  name: "player",
  initialState,
  reducers: {
    setSpotifyPlayer: (state, action: PayloadAction<Spotify.Player>) => {
      state.player = action.payload;
    },
  },
});

export const { setSpotifyPlayer } = playerSlice.actions;
export default playerSlice.reducer;
