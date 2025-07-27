import useHomeMusicData from '@/features/home/hooks/useHomeMusicData';
import { ArrowLeft, Filter, Search, Users } from 'lucide-react';
import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function ArtistListPage() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'popularity'>('popularity');

  const { artists, error, isLoading } = useHomeMusicData();

  const filteredAndSortedArtists = useMemo(() => {
    if (!artists) return [];

    const filtered = artists.filter((artist) =>
      artist.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return filtered.sort((a, b) => {
      return sortBy === 'name'
        ? a.name.localeCompare(b.name)
        : (b.popularity || 0) - (a.popularity || 0);
    });
  }, [artists, searchTerm, sortBy]);
  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8'>
          <div className='mb-8'>
            <div className='flex items-center mb-6'>
              <button
                onClick={() => navigate(-1)}
                className='mr-4 p-2 rounded-full bg-white/10 hover:bg-white/20 transition-all duration-300'
              >
                <ArrowLeft className='w-6 h-6 text-white' />
              </button>
              <div className='flex items-center space-x-3'>
                <div className='p-3 bg-gradient-to-r from-pink-500 to-violet-500 rounded-xl'>
                  <Users className='w-8 h-8 text-white' />
                </div>
                <div>
                  <h1 className='text-3xl sm:text-4xl font-bold text-white'>
                    인기 가수
                  </h1>
                  <p className='text-white/70 mt-1'>
                    팬들이 사랑하는 아티스트들을 만나보세요
                  </p>
                </div>
              </div>
            </div>

            <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 border border-white/10'>
              <div className='flex flex-col sm:flex-row gap-4'>
                <div className='relative flex-1'>
                  <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/50' />
                  <input
                    type='text'
                    placeholder='아티스트 검색...'
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className='w-full pl-10 pr-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all duration-300'
                  />
                </div>
                <div className='relative'>
                  <Filter className='absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/50' />
                  <select
                    value={sortBy}
                    onChange={(e) =>
                      setSortBy(e.target.value as 'name' | 'popularity')
                    }
                    className='pl-10 pr-8 py-3 bg-white/10 border border-white/20 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all duration-300 appearance-none cursor-pointer'
                  >
                    <option value='popularity' className='bg-gray-800'>
                      인기도순
                    </option>
                    <option value='name' className='bg-gray-800'>
                      이름순
                    </option>
                  </select>
                </div>
              </div>
            </div>
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
                {filteredAndSortedArtists.map((artist, index) => (
                  <div
                    key={index}
                    onClick={() => console.log('Artist clicked:', artist)}
                    className='group cursor-pointer transition-all duration-300 hover:scale-105'
                  >
                    <div className='bg-white/5 backdrop-blur-sm rounded-2xl p-4 border border-white/10 hover:border-white/20 hover:bg-white/10 transition-all duration-300'>
                      <div className='relative mb-4'>
                        <img
                          src={artist.images?.[0]?.url}
                          alt={artist.name}
                          className='w-full aspect-square object-cover rounded-xl group-hover:shadow-lg transition-all duration-300'
                        />
                        <div className='absolute inset-0 bg-gradient-to-t from-black/20 to-transparent rounded-xl opacity-0 group-hover:opacity-100 transition-all duration-300' />
                      </div>
                      <div className='text-center'>
                        <h3 className='text-white font-semibold text-sm mb-1 truncate group-hover:text-violet-300 transition-colors duration-300'>
                          {artist.name}
                        </h3>

                        {artist.popularity && (
                          <div className='mt-2 flex items-center justify-center space-x-1'>
                            <div className='w-full bg-white/20 rounded-full h-1'>
                              <div
                                className='bg-gradient-to-r from-pink-500 to-violet-500 h-1 rounded-full transition-all duration-300'
                                style={{ width: `${artist.popularity}%` }}
                              />
                            </div>
                            <span className='text-white/50 text-xs ml-2'>
                              {artist.popularity}
                            </span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
