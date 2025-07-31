import { useQuery } from '@tanstack/react-query';
import { fetchMusicSearch } from '../services/musicApi';
import type { Track } from '../types/home';

export default function useMusicSearch(query: string) {
  const { data, isLoading, error } = useQuery<Track[]>({
    queryKey: ['music-search', query],
    queryFn: () => fetchMusicSearch(query),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 25,
    refetchOnWindowFocus: false,
    retry: false,
    enabled: !!query,
  });
  return { data, isLoading, error };
}
