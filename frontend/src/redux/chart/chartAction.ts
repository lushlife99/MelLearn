import { ChartData } from "../type";
import { axiosSpotifyScraper } from "../../api";

export const fetchChartData = async (): Promise<ChartData> => {
  const response = await axiosSpotifyScraper.get("/chart/tracks/top?region=us");
  return response.data;
};
