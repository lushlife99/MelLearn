import CategoryCard from '@/features/learning/components/CategoryCard';
import { CATEGORIES } from '@/features/learning/constants/learning';
import { TrendingUp } from 'lucide-react';
import { useParams } from 'react-router-dom';
import useQuiz from './hooks/useQuiz';
import useTrack from '@/features/track/hooks/useTrack';
import useLyric from '@/features/track/hooks/useLyric';

export default function QuizPage() {
  const { id } = useParams();
  const {
    track,
    isLoading: trackLoading,
    error: trackError,
  } = useTrack(id || '');
  const { plainLyrics } = useLyric(track);
  const {
    categories,
    create,
    quizLoading,
    categoryLoading,
    error: quizError,
  } = useQuiz(id || '', plainLyrics);
  const handleCategoryClick = (category: string) => {
    create(category);
  };

  if (trackLoading || categoryLoading) return <div> 로딩중...</div>;

  if (quizLoading)
    return <div>인공지능이 퀴즈를 만들고있어요 잠시만 기다려주세요</div>;

  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6'>
          {/* 헤더 섹션 */}
          <div className='text-center mb-12'>
            <div className='inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 backdrop-blur-sm border border-white/20 mb-6'>
              <TrendingUp className='w-4 h-4 text-pink-400' />
              <span className='text-white/80 text-sm font-medium'>
                English Learning
              </span>
            </div>

            <h1 className='text-4xl sm:text-6xl font-bold text-white mb-6'>
              어떤 영역을
              <span className='bg-gradient-to-r from-pink-400 to-violet-400 bg-clip-text text-transparent'>
                학습
              </span>
              하시겠어요?
            </h1>

            <p className='text-xl text-white/70 mb-4 max-w-3xl mx-auto leading-relaxed'>
              5가지 핵심 영역으로 나누어진 체계적인 학습 프로그램으로
              <br />
              여러분의 영어 실력을 한 단계 업그레이드해보세요
            </p>

            {/* 통계 정보 */}
            <div className='flex items-center justify-center gap-8 mt-8'>
              <div className='text-center'>
                <div className='text-2xl font-bold text-white'>256+</div>
                <div className='text-white/60 text-sm'>총 레슨</div>
              </div>
              <div className='w-px h-12 bg-white/20'></div>
              <div className='text-center'>
                <div className='text-2xl font-bold text-white'>50K+</div>
                <div className='text-white/60 text-sm'>학습자</div>
              </div>
              <div className='w-px h-12 bg-white/20'></div>
              <div className='text-center'>
                <div className='text-2xl font-bold text-white'>4.8★</div>
                <div className='text-white/60 text-sm'>평점</div>
              </div>
            </div>
          </div>

          {/* 카테고리 카드들 */}
          <div className='grid lg:grid-cols-2 xl:grid-cols-3 gap-8'>
            {CATEGORIES.map((category) => (
              <CategoryCard
                key={category}
                category={category}
                onClick={handleCategoryClick}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
