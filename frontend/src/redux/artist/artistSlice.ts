import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface ArtistImg {
  avatar: {
    url: string;
    width: number | null;
    height: number | null;
  }[];
}

interface Artist {
  type: string;
  id: string;
  name: string;
  shareUrl: string;
  visuals: ArtistImg;
}

interface ArtistData {
  artists: Artist[];
}

interface ArtistState {
  artistData: ArtistData;
  artistLoading: boolean;
  artistError: string | null;
}

const initialState: ArtistState = {
  artistData: { artists: [] },
  artistLoading: false,
  artistError: null,
};

const artistSlice = createSlice({
  name: "artist",
  initialState,
  reducers: {
    fetchArtistStart(state) {
      state.artistLoading = true;
      state.artistError = null;
    },
    fetchArtistSuccess(state, action: PayloadAction<ArtistData>) {
      state.artistData = action.payload;
      state.artistLoading = false;
    },
    fetchArtistError(state, action: PayloadAction<string>) {
      state.artistLoading = false;
      state.artistError = action.payload;
    },
  },
});
export const { fetchArtistStart, fetchArtistSuccess, fetchArtistError } =
  artistSlice.actions;
export default artistSlice.reducer;
