import { useQuery } from '@tanstack/react-query';
import { fetchTopArtists, fetchTopCharts } from '../services/musicApi';

export default function useHomeMusicData(lang: string = 'United%20States') {
  const {
    data: artists,
    isLoading: isArtistsLoading,
    error: isArtistsError,
  } = useQuery({
    queryKey: ['topArtists'],
    queryFn: fetchTopArtists,
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 25,
    refetchOnWindowFocus: false,
    retry: false,
  });

  const {
    data: charts,
    isLoading: isChartsLoading,
    error: chartsError,
  } = useQuery({
    queryKey: ['topCharts'],
    queryFn: () => fetchTopCharts(lang),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 25,
    refetchOnWindowFocus: false,
    retry: false,
  });
  const isLoading = isArtistsLoading || isChartsLoading;
  const error = isArtistsError || chartsError;
  return { artists, charts, isLoading, error };
}
