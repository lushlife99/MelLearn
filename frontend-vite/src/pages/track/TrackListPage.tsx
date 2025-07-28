import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { TrendingUp } from 'lucide-react';
import useHomeData from '@/features/home/hooks/useHomeData';
import { ROUTES } from '@/services/router';

import ArtistTrackLayout from '@/components/ArtistTrackLayout';
import TrackSection from '@/components/TrackSection';
import ArtistTrackHeader from '@/components/ArtistTrackHeader';
import SearchFilterBar from '@/components/SearchFilterBar';

const sortOptions = [
  { value: 'popularity', label: '인기도순' },
  { value: 'name', label: '이름순' },
  { value: 'duration', label: '재생시간순' },
];

export default function TrackListPage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'popularity' | 'duration'>(
    'popularity'
  );

  const { charts, error, isLoading } = useHomeData();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    setQuery(e.target.value);
  const handleSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) =>
    setSortBy(e.target.value as 'name' | 'popularity' | 'duration');

  const filteredAndSortedCharts = useMemo(() => {
    if (!charts) return [];

    const filtered = charts.filter(
      (track) =>
        track.name.toLowerCase().includes(query.toLowerCase()) ||
        track.artists.some((artist) =>
          artist.name.toLowerCase().includes(query.toLowerCase())
        )
    );

    return filtered.sort((a, b) => {
      if (sortBy === 'name') {
        return a.name.localeCompare(b.name);
      } else if (sortBy === 'duration') {
        return (a.duration_ms || 0) - (b.duration_ms || 0);
      } else {
        return (b.popularity || 0) - (a.popularity || 0);
      }
    });
  }, [charts, query, sortBy]);

  return (
    <ArtistTrackLayout>
      <div className='mb-8'>
        <ArtistTrackHeader
          Icon={TrendingUp}
          title='인기 차트'
          description='지금 가장 인기 있는 음악들을 만나보세요'
          onClick={() => navigate(-1)}
        />
        <SearchFilterBar
          placeholder='트랙이나 아티스트 검색...'
          query={query}
          sortBy={sortBy}
          onChange={handleChange}
          onSelectChange={handleSelectChange}
          sortOptions={sortOptions}
        />
      </div>

      {filteredAndSortedCharts && filteredAndSortedCharts.length > 0 ? (
        <TrackSection
          title={`전체 트랙 (${filteredAndSortedCharts.length})`}
          tracks={filteredAndSortedCharts}
          onClick={(id) => navigate(ROUTES.TRACK_DETAIL(id))}
        />
      ) : (
        <div className='text-center py-12'>
          <TrendingUp className='w-16 h-16 text-white/30 mx-auto mb-4' />
          <p className='text-white/50 text-lg'>검색 결과가 없습니다</p>
        </div>
      )}
    </ArtistTrackLayout>
  );
}
