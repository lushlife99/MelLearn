import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  TrendingUp,
  Search,
  Filter,
  ArrowLeft,
  Play,
  Clock,
} from 'lucide-react';
import useHomeMusicData from '@/features/home/hooks/useHomeMusicData';

export default function ChartListPage() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'popularity' | 'duration'>(
    'popularity'
  );

  const { charts, error, isLoading } = useHomeMusicData();

  const filteredAndSortedCharts = useMemo(() => {
    if (!charts) return [];

    const filtered = charts.filter(
      (track) =>
        track.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        track.artists.some((artist) =>
          artist.name.toLowerCase().includes(searchTerm.toLowerCase())
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
  }, [charts, searchTerm, sortBy]);

  const formatDuration = (ms: number) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8'>
          {/* Header */}
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
                  <TrendingUp className='w-8 h-8 text-white' />
                </div>
                <div>
                  <h1 className='text-3xl sm:text-4xl font-bold text-white'>
                    인기 차트
                  </h1>
                  <p className='text-white/70 mt-1'>
                    지금 가장 인기 있는 음악들을 만나보세요
                  </p>
                </div>
              </div>
            </div>

            {/* Search and Filter */}
            <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 border border-white/10'>
              <div className='flex flex-col sm:flex-row gap-4'>
                <div className='relative flex-1'>
                  <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/50' />
                  <input
                    type='text'
                    placeholder='트랙이나 아티스트 검색...'
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
                      setSortBy(
                        e.target.value as 'name' | 'popularity' | 'duration'
                      )
                    }
                    className='pl-10 pr-8 py-3 bg-white/10 border border-white/20 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all duration-300 appearance-none cursor-pointer'
                  >
                    <option value='popularity' className='bg-gray-800'>
                      인기도순
                    </option>
                    <option value='name' className='bg-gray-800'>
                      제목순
                    </option>
                    <option value='duration' className='bg-gray-800'>
                      재생시간순
                    </option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          {/* Charts List */}
          <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10'>
            <div className='mb-6 flex justify-between items-center'>
              <h2 className='text-xl font-semibold text-white'>
                전체 트랙 ({filteredAndSortedCharts.length})
              </h2>
            </div>

            {filteredAndSortedCharts.length === 0 ? (
              <div className='text-center py-12'>
                <TrendingUp className='w-16 h-16 text-white/30 mx-auto mb-4' />
                <p className='text-white/50 text-lg'>검색 결과가 없습니다</p>
              </div>
            ) : (
              <div className='space-y-3'>
                {filteredAndSortedCharts.map((track, index) => (
                  <div
                    key={index}
                    onClick={() => console.log('Track clicked:', track)}
                    className='group cursor-pointer transition-all duration-300 hover:scale-[1.02]'
                  >
                    <div className='bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10 hover:border-white/20 hover:bg-white/10 transition-all duration-300 flex items-center space-x-4'>
                      {/* Rank Number */}
                      <div className='flex-shrink-0 w-8 text-center'>
                        <span className='text-white/70 font-bold text-lg'>
                          {index + 1}
                        </span>
                      </div>

                      {/* Album Cover */}
                      <div className='relative flex-shrink-0'>
                        <img
                          src={track.album?.images?.[0]?.url}
                          alt={track.name}
                          className='w-16 h-16 object-cover rounded-lg group-hover:shadow-lg transition-all duration-300'
                        />
                        <div className='absolute inset-0 bg-black/40 rounded-lg opacity-0 group-hover:opacity-100 transition-all duration-300 flex items-center justify-center'>
                          <Play className='w-6 h-6 text-white' />
                        </div>
                      </div>

                      {/* Track Info */}
                      <div className='flex-1 min-w-0'>
                        <h3 className='text-white font-semibold text-base mb-1 truncate group-hover:text-violet-300 transition-colors duration-300'>
                          {track.name}
                        </h3>
                        <p className='text-white/70 text-sm truncate'>
                          {track.artists
                            .map((artist) => artist.name)
                            .join(', ')}
                        </p>
                        {track.album?.name && (
                          <p className='text-white/50 text-xs truncate mt-1'>
                            {track.album.name}
                          </p>
                        )}
                      </div>

                      {/* Popularity Bar */}
                      <div className='hidden sm:flex flex-col items-center space-y-2 flex-shrink-0'>
                        <div className='w-16 bg-white/20 rounded-full h-2'>
                          <div
                            className='bg-gradient-to-r from-pink-500 to-violet-500 h-2 rounded-full transition-all duration-300'
                            style={{ width: `${track.popularity || 0}%` }}
                          />
                        </div>
                        <span className='text-white/50 text-xs'>
                          {track.popularity || 0}
                        </span>
                      </div>

                      {/* Duration */}
                      <div className='flex items-center space-x-1 text-white/50 text-sm flex-shrink-0'>
                        <Clock className='w-4 h-4' />
                        <span>
                          {track.duration_ms
                            ? formatDuration(track.duration_ms)
                            : '0:00'}
                        </span>
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
