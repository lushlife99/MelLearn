import { useQuery } from '@tanstack/react-query';
import { fetchTopArtists, fetchTopCharts } from '../services/musicApi';

export default function useHomeData(lang: string = 'United%20States') {
  const {
    data: artists,
    isLoading: isArtistsLoading,
    error: artistsError,
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
  const error = artistsError || chartsError;
  return { artists, charts, isLoading, error };
}
