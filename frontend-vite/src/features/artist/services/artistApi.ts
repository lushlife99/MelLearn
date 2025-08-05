import type { Artist, Track } from '@/features/home/types/home';
import { apiSpotify } from '@/services/axios';

export async function fetchArtist(id: string): Promise<Artist> {
  const { data } = await apiSpotify.get(`/artists/${id}`);
  return data;
}

export async function fetchTopTrackByArtist(id: string): Promise<Track[]> {
  const { data } = await apiSpotify.get(`/artists/${id}/top-tracks`);
  return data.tracks;
}
