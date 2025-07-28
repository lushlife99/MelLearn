import ArtistTrackHeader from '@/components/ArtistTrackHeader';
import ArtistTrackLayout from '@/components/ArtistTrackLayout';
import SearchFilterBar from '@/components/SearchFilterBar';
import useHomeData from '@/features/home/hooks/useHomeData';
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
  const filteredAndSortedArtists = useMemo(() => {
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
          Icon={Users}
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

      <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10'>
        <div className='mb-6 flex justify-between items-center'>
          <h2 className='text-xl font-semibold text-white'>
            전체 아티스트 ({filteredAndSortedArtists.length})
          </h2>
        </div>

        {filteredAndSortedArtists.length === 0 ? (
          <div className='text-center py-12'>
            <Users className='w-16 h-16 text-white/30 mx-auto mb-4' />
            <p className='text-white/50 text-lg'>검색 결과가 없습니다</p>
          </div>
        ) : (
          <div className='grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 sm:gap-6'>
            {filteredAndSortedArtists.map(
              ({ id, name, popularity, images }) => (
                <div
                  key={id}
                  onClick={() => navigate(ROUTES.ARTIST_DETAIL(id))}
                  className='group cursor-pointer transition-all duration-300 hover:scale-105'
                >
                  <div className='bg-white/5 backdrop-blur-sm rounded-2xl p-4 border border-white/10 hover:border-white/20 hover:bg-white/10 transition-all duration-300'>
                    <div className='relative mb-4'>
                      <img
                        src={images?.[0]?.url}
                        alt={name}
                        className='w-full aspect-square object-cover rounded-xl group-hover:shadow-lg transition-all duration-300'
                      />
                      <div className='absolute inset-0 bg-gradient-to-t from-black/20 to-transparent rounded-xl opacity-0 group-hover:opacity-100 transition-all duration-300' />
                    </div>
                    <div className='text-center'>
                      <h3 className='text-white font-semibold text-sm mb-1 truncate group-hover:text-violet-300 transition-colors duration-300'>
                        {name}
                      </h3>

                      {popularity && (
                        <div className='mt-2 flex items-center justify-center space-x-1'>
                          <div className='w-full bg-white/20 rounded-full h-1'>
                            <div
                              className='bg-gradient-to-r from-pink-500 to-violet-500 h-1 rounded-full transition-all duration-300'
                              style={{ width: `${popularity}%` }}
                            />
                          </div>
                          <span className='text-white/50 text-xs ml-2'>
                            {popularity}
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              )
            )}
          </div>
        )}
      </div>
    </ArtistTrackLayout>
  );
}
