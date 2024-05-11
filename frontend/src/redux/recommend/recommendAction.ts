import { axiosSpotify, axiosSpotifyScraper } from "../../api";
import { RecommendData } from "../type";

export const fetchRecommendData = async (
  langType: string | undefined
): Promise<RecommendData> => {
  if (langType === "en") {
    langType = "us";
  } else if (langType === "ja") {
    langType = "jp";
  }
  const res = await axiosSpotifyScraper.get(`/chart/tracks/viral?region=jp`);

  const trackIds = res.data.tracks.slice(0, 50).map((track: any) => track.id);
  const trackReq = trackIds.map((id: string) =>
    axiosSpotify.get(`/tracks/${id}`)
  );

  const responses = await Promise.all(trackReq);

  const tracks = responses.map((res) => res.data);

  const playableTracks = tracks.filter((track) => track.preview_url !== null);

  return { recommends: playableTracks };
};
