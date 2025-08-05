import ArtistTrackHeader from '@/components/ArtistTrackHeader';
import ArtistTrackLayout from '@/components/ArtistTrackLayout';
import SearchFilterBar from '@/components/SearchFilterBar';
import SearchNotFound from '@/components/SearchNotFound';
import ArtistsSection from '@/features/artist/components/ArtistsSection';
import useHomeData from '@/features/home/hooks/useHomeData';
import type { Artist } from '@/features/home/types/home';
import { ROUTES } from '@/services/router';
import { Users } from 'lucide-react';
import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const sortOptions = [
  { value: 'popularity', label: '인기도순' },
  { value: 'name', label: '이름순' },
];

export default function ArtistListPage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'popularity'>('popularity');

  const { artists, error, isLoading } = useHomeData();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    setQuery(e.target.value);

  const handleSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) =>
    setSortBy(e.target.value as 'name' | 'popularity');

  //TODO: 정렬로직 util로 분리

  const filteredAndSortedArtists: Artist[] = useMemo(() => {
    if (!artists) return [];

    const filtered = artists.filter((artist) =>
      artist.name.toLowerCase().includes(query.toLowerCase())
    );

    return filtered.sort((a, b) => {
      return sortBy === 'name'
        ? a.name.localeCompare(b.name)
        : (b.popularity || 0) - (a.popularity || 0);
    });
  }, [artists, query, sortBy]);
  return (
    <ArtistTrackLayout>
      <div className='mb-8'>
        <ArtistTrackHeader
          title='인기 가수'
          description='팬들이 사랑하는 아티스트들을 만나보세요'
          onClick={() => navigate(-1)}
        />
        <SearchFilterBar
          placeholder='아티스트 검색...'
          query={query}
          sortBy={sortBy}
          onChange={handleChange}
          onSelectChange={handleSelectChange}
          sortOptions={sortOptions}
        />
      </div>

      {filteredAndSortedArtists.length === 0 ? (
        <SearchNotFound Icon={Users} />
      ) : (
        <ArtistsSection
          artists={filteredAndSortedArtists}
          onClick={(id) => navigate(ROUTES.ARTIST_DETAIL(id))}
        />
      )}
    </ArtistTrackLayout>
  );
}
