import { Search, ArrowLeft } from 'lucide-react';
import { useState } from 'react';
import useMusicSearch from '../hooks/useMusicSearch';
import useDebounce from '../hooks/useDebounce';
import TrackSection from '@/components/TrackSection';
import { ROUTES } from '@/services/router';
import { useNavigate } from 'react-router-dom';
import type { Track } from '../types/home';
import SearchLoading from './SearchLoading';
import SearchNotFound from './SearchNotFound';

interface Props {
  onClose: () => void;
  charts: Track[];
}

export default function SearchMusic({ onClose, charts }: Props) {
  const [searchQuery, setSearchQuery] = useState('');
  const debounceQuery = useDebounce(searchQuery, 500);

  const navigate = useNavigate();
  const { data: tracks, isLoading, error } = useMusicSearch(debounceQuery);

  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6'>
          <div className='flex items-center gap-4 mb-8'>
            <button
              onClick={onClose}
              className='p-3 rounded-full bg-white/10 backdrop-blur-sm hover:bg-white/20 transition-all duration-300 hover:scale-110'
            >
              <ArrowLeft className='w-6 h-6 text-white' />
            </button>
            <div>
              <h1 className='text-3xl sm:text-4xl font-bold text-white'>
                음악 검색
              </h1>
              <p className='text-white/60 text-base sm:text-lg'>
                좋아하는 음악을 찾아보세요
              </p>
            </div>
          </div>

          <div className='mb-8'>
            <div className='relative mb-6'>
              <Search className='absolute left-6 top-1/2 transform -translate-y-1/2 w-6 h-6 text-white/50' />
              <input
                type='text'
                placeholder='노래, 아티스트, 앨범 검색...'
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className='w-full pl-16 pr-6 py-5 text-lg bg-white/10 backdrop-blur-lg border border-white/20 rounded-2xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-transparent transition-all duration-300'
                autoFocus
              />
            </div>
          </div>

          <main className='pb-8'>
            {!searchQuery ? (
              <TrackSection
                tracks={charts}
                title='지금 인기있는 음악'
                onClick={(id) => navigate(ROUTES.TRACK_DETAIL(id))}
              />
            ) : isLoading ? (
              <SearchLoading />
            ) : tracks && tracks?.length > 0 ? (
              <TrackSection
                tracks={tracks}
                title={`${searchQuery}에 대한 ${tracks?.length}개의 결과`}
                onClick={(id) => navigate(ROUTES.TRACK_DETAIL(id))}
              />
            ) : (
              <SearchNotFound searchQuery={searchQuery} />
            )}
          </main>
        </div>
      </div>
    </div>
  );
}
