import { Dispatch } from "redux";
import axios, { AxiosError } from "axios";
import {
  fetchChartError,
  fetchChartStart,
  fetchChartSuccess,
} from "./chartSlice";
import { AppDispatch } from "../store";

const options = {
  method: "GET",
  url: "https://spotify-scraper.p.rapidapi.com/v1/chart/tracks/top?region=us",
  headers: {
    "X-RapidAPI-Key": "API_KEY",
    "X-RapidAPI-Host": "spotify-scraper.p.rapidapi.com",
  },
};

export const fetchChartData = () => async (dispatch: AppDispatch) => {
  dispatch(fetchChartStart());
  try {
    const response = await axios.request(options);
    dispatch(fetchChartSuccess(response.data));
  } catch (error: any) {
    dispatch(fetchChartError(error.message));
  }
};
