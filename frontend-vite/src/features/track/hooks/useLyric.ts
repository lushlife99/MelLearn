import type { Track } from '@/features/home/types/home';
import { useQuery } from '@tanstack/react-query';
import { fetchLrcByTrack } from '../services/lyricApi';

export default function useLyric(track?: Track) {
  const { id, artists, album, name, duration_ms } = track ?? {};
  const duration = duration_ms ? Math.floor(duration_ms / 1000) : 0;

  const { data: lyrics } = useQuery({
    queryKey: ['lyrics', id],
    queryFn: () =>
      fetchLrcByTrack(artists?.[0].name, name, album?.name, duration),
    enabled: !!track,
    staleTime: Infinity,
    gcTime: Infinity,
    refetchOnWindowFocus: false,
  });

  return { lyrics };
}
