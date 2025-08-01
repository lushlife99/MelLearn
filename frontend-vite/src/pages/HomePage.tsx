import { useNavigate } from 'react-router-dom';
import { TrendingUp, Users } from 'lucide-react';
import CardList from '@/features/home/components/CardList';
import Header from '@/features/home/components/Header';
import SearchBar from '@/features/home/components/SearchBar';
import WelcomeSection from '@/features/home/components/WelcomeSection';
import SectionHeader from '@/features/home/components/SectionHeader';
import { ROUTES } from '@/services/router';
import useHomeData from '@/features/home/hooks/useHomeData';
import { useState } from 'react';
import SearchMusic from '@/features/home/components/SearchMusic';

export default function HomePage() {
  const { artists, charts, error, isLoading } = useHomeData();
  const [showSearch, setShowSearch] = useState(false);
  const navigate = useNavigate();

  const SkeletonLoader = () => (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
        <div className='flex justify-center items-center h-screen'>
          <div className='bg-white/10 backdrop-blur-lg rounded-2xl p-8 shadow-xl border border-white/20'>
            <div className='flex items-center space-x-3'>
              <div className='w-8 h-8 bg-gradient-to-r from-pink-500 to-violet-500 rounded-full animate-pulse'></div>
              <div className='text-white text-xl font-semibold'>로딩 중...</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  if (isLoading) return <SkeletonLoader />;

  if (error)
    return (
      <div className='min-h-screen bg-gradient-to-br from-red-900 via-purple-900 to-indigo-900 flex items-center justify-center'>
        <div className='bg-white/10 backdrop-blur-lg rounded-2xl p-8 shadow-xl border border-white/20 text-center'>
          <div className='text-red-400 text-lg font-medium'>
            음악 데이터를 불러오지 못했습니다
          </div>
          <button
            onClick={() => window.location.reload()}
            className='mt-4 px-6 py-2 bg-gradient-to-r from-pink-500 to-violet-500 text-white rounded-lg hover:shadow-lg transition-all duration-300'
          >
            다시 시도
          </button>
        </div>
      </div>
    );

  if (showSearch) {
    return (
      <SearchMusic onClose={() => setShowSearch(false)} charts={charts || []} />
    );
  }

  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8'>
          <Header onClick={() => navigate('/searchMusic')} />
          <SearchBar onClick={() => setShowSearch(true)} />
        </div>

        <main className='pb-8 sm:pb-12'>
          <WelcomeSection />

          <div className='space-y-12'>
            <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 hover:border-white/20 transition-all duration-300 group'>
              <SectionHeader
                Icon={TrendingUp}
                title='인기 차트'
                description='지금 가장 인기 있는 음악들'
                onClick={() => navigate(ROUTES.TRACKS)}
              />
              <CardList
                items={charts || []}
                type='track'
                onClick={(item) => navigate(ROUTES.TRACK_DETAIL(item.id))}
              />
            </div>
            <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 hover:border-white/20 transition-all duration-300 group'>
              <SectionHeader
                Icon={Users}
                title='인기 가수'
                description='팬들이 사랑하는 아티스트들'
                onClick={() => navigate(ROUTES.ARTISTS)}
              />
              <CardList
                items={artists || []}
                type='artist'
                onClick={(item) => navigate(ROUTES.ARTIST_DETAIL(item.id))}
              />
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
