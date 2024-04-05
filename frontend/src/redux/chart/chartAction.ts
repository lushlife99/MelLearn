import { ChartData } from "../type";
import { axiosSpotify, axiosSpotifyScraper } from "../../api";

export const fetchChartData = async (): Promise<ChartData> => {
  const res = await axiosSpotifyScraper.get("/chart/tracks/top?region=us");
  const trackIds = res.data.tracks.slice(0, 50).map((track: any) => track.id);
  const trackReq = trackIds.map((id: string) =>
    axiosSpotify.get(`/tracks/${id}`)
  );

  const responses = await Promise.all(trackReq);
  const tracks = responses.map((res) => res.data);
  const playableTracks = tracks.filter((track) => track.is_playable);

  return { tracks: playableTracks };
};
