import type { Track } from '@/features/home/types/home';
import { apiSpotify } from '@/services/axios';

export async function fetchTrack(id: string): Promise<Track> {
  const { data } = await apiSpotify.get(`/tracks/${id}`);
  return data;
}
