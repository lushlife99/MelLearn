import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface PlayerState {
  spotifyPlyaer: Spotify.Player | null;
}
const initialState: PlayerState = {
  spotifyPlyaer: null,
};

const playerSlice = createSlice({
  name: "player",
  initialState,
  reducers: {
    setSpotifyPlayer: (state, action: PayloadAction<Spotify.Player>) => {
      state.spotifyPlyaer = action.payload;
    },
  },
});

export const { setSpotifyPlayer } = playerSlice.actions;
export default playerSlice.reducer;
