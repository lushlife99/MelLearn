import { ArtistData } from "../type";
import { axiosSpotifyScraper } from "../../api";

export const fetchArtistData = async (): Promise<ArtistData> => {
  const response = await axiosSpotifyScraper.get(
    "/chart/artists/top?region=us"
  );
  return response.data;
};
