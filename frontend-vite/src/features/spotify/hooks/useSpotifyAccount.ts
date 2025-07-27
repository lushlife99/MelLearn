import { useQuery } from '@tanstack/react-query';
import { fetchSpotifyProfile } from '../services/spotifyApi';

export function useSpotifyAccount(enabled = true) {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['spotifyProfile'],
    queryFn: fetchSpotifyProfile,
    enabled,
    staleTime: 1000 * 60 * 5,
  });

  return {
    spotifyId: data?.id ?? null,
    profile: data ?? null,
    isLoading,
    isError,
    error,
  };
}
