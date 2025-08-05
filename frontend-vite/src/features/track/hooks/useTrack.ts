import { useQuery } from '@tanstack/react-query';
import { fetchTrack } from '../services/trackApi';

export default function useTrack(id: string) {
  const {
    data: track,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['track', id],
    queryFn: () => fetchTrack(id),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 25,
    refetchOnWindowFocus: false,
    retry: false,
    enabled: !!id,
  });

  return { track, isLoading, error };
}
