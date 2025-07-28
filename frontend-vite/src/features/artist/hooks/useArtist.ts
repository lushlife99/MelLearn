import { useQuery } from '@tanstack/react-query';
import { fetchArtist, fetchTopTrackByArtist } from '../services/artistApi';

export default function useArtist(id: string) {
  const {
    data: artist,
    isLoading: isArtistLoading,
    error: artistError,
  } = useQuery({
    queryKey: ['artistDetail', id],
    queryFn: () => fetchArtist(id),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 25,
    refetchOnWindowFocus: false,
    retry: false,
    enabled: !!id,
  });

  const {
    data: topTracksByArtist,
    isLoading: isTopTracksByArtist,
    error: topTracksByArtistError,
  } = useQuery({
    queryKey: ['top-tracks', id],
    queryFn: () => fetchTopTrackByArtist(id),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 25,
    refetchOnWindowFocus: false,
    retry: false,
    enabled: !!id,
  });
  const isLoading = isArtistLoading || isTopTracksByArtist;
  const error = artistError || topTracksByArtistError;
  return { artist, isLoading, error, topTracksByArtist };
}
