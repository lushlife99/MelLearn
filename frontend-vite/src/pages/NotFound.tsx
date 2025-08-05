import { useNavigate } from 'react-router-dom';
import { Home, ArrowLeft, AlertTriangle } from 'lucide-react';
import { ROUTES } from '@/services/router';

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='min-h-screen flex items-center justify-center px-4 sm:px-6 lg:px-8'>
          <div className='max-w-2xl mx-auto text-center'>
            <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-8 sm:p-12 border border-white/10 mb-8'>
              <div className='mb-8'>
                <div className='relative inline-block'>
                  <div className='p-4 bg-gradient-to-r from-pink-500 to-violet-500 rounded-2xl'>
                    <AlertTriangle className='w-16 h-16 text-white' />
                  </div>
                  <div className='absolute -top-2 -right-2 bg-red-500 text-white text-sm font-bold px-2 py-1 rounded-lg shadow-lg'>
                    404
                  </div>
                </div>
              </div>

              <div className='mb-8'>
                <h1 className='text-3xl sm:text-4xl lg:text-5xl font-bold text-white mb-4'>
                  페이지를 찾을 수 없어요
                </h1>
                <p className='text-white/70 text-lg leading-relaxed max-w-md mx-auto'>
                  요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
                </p>
              </div>

              <div className='flex flex-col sm:flex-row gap-4 justify-center mb-8'>
                <button
                  onClick={() => navigate(-1)}
                  className='flex items-center justify-center space-x-2 bg-white/10 hover:bg-white/20 text-white px-8 py-3 rounded-xl font-semibold transition-all duration-300 border border-white/20 hover:border-white/30'
                >
                  <ArrowLeft className='w-5 h-5' />
                  <span>이전 페이지</span>
                </button>

                <button
                  onClick={() => navigate(ROUTES.HOME)}
                  className='flex items-center justify-center space-x-2 bg-gradient-to-r from-pink-500 to-violet-500 hover:from-pink-600 hover:to-violet-600 text-white px-8 py-3 rounded-xl font-semibold transition-all duration-300 hover:scale-105'
                >
                  <Home className='w-5 h-5' />
                  <span>홈으로 가기</span>
                </button>
              </div>
            </div>

            <p className='text-white/50 text-sm'>
              문제가 지속되면 홈페이지로 돌아가서 다시 시도해보세요.
            </p>
          </div>

          <div className='fixed top-20 left-20 w-64 h-64 bg-pink-500/5 rounded-full blur-3xl animate-pulse pointer-events-none' />
          <div
            className='fixed bottom-20 right-20 w-80 h-80 bg-violet-500/5 rounded-full blur-3xl animate-pulse pointer-events-none'
            style={{ animationDelay: '2s' }}
          />
        </div>
      </div>
    </div>
  );
}
