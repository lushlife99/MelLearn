import { ChartData } from "../type";
import { axiosSpotify } from "../../api";

export const fetchChartData = async (): Promise<ChartData> => {
  const response = await axiosSpotify.get("/chart/tracks/top?region=us");
  return response.data;
};
