import { Dispatch } from "redux";
import axios, { AxiosError } from "axios";
import {
  fetchArtistError,
  fetchArtistStart,
  fetchArtistSuccess,
} from "./artistSlice";
import { AppDispatch } from "../store";

const options = {
  method: "GET",
  url: "https://spotify-scraper.p.rapidapi.com/v1/chart/artists/top?region=us",
  headers: {
    "X-RapidAPI-Key": "API_KEY",
    "X-RapidAPI-Host": "spotify-scraper.p.rapidapi.com",
  },
};

export const fetchArtistData = () => async (dispatch: AppDispatch) => {
  dispatch(fetchArtistStart());
  try {
    const response = await axios.request(options);
    dispatch(fetchArtistSuccess(response.data));
  } catch (error: any) {
    dispatch(fetchArtistError(error.message));
  }
};
