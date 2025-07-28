import { apiLrc } from '@/services/axios';
import type { Lyric } from '../types/track';

export async function fetchLrcByTrack(
  artist_name?: string,
  track_name?: string,
  album_name?: string,
  duration?: number
): Promise<Lyric> {
  const { data } = await apiLrc.get(`/get`, {
    params: {
      artist_name,
      track_name,
      album_name,
      duration,
    },
  });
  return data;
}
