import { axiosSpotify, axiosSpotifyScraper } from "../../api";
import { RecommendData } from "../type";

export const fetchRecommendData = async (): Promise<RecommendData> => {
  const res = await axiosSpotifyScraper.get("/chart/tracks/viral");

  const trackIds = res.data.tracks.slice(0, 50).map((track: any) => track.id);
  const trackReq = trackIds.map((id: string) =>
    axiosSpotify.get(`/tracks/${id}`)
  );

  const responses = await Promise.all(trackReq);

  const tracks = responses.map((res) => res.data);

  const playableTracks = tracks.filter((track) => track.preview_url !== null);

  console.log(res.data);
  return { recommends: playableTracks };
};
