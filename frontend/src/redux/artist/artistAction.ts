import { ArtistData } from "../type";
import { axiosSpotify } from "../../api";

export const fetchArtistData = async (): Promise<ArtistData> => {
  const response = await axiosSpotify.get("/chart/artists/top?region=us");
  return response.data;
};
