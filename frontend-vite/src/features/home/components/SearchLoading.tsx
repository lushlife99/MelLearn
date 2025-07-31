import { Music } from 'lucide-react';

export default function SearchLoading() {
  return (
    <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-8 border border-white/10'>
      <div className='flex flex-col items-center justify-center py-12'>
        <div className='relative mb-6'>
          <div className='w-16 h-16 border-4 border-white/20 border-t-pink-500 rounded-full animate-spin' />
          <Music className='w-8 h-8 text-pink-400 absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2' />
        </div>
        <p className='text-white/60 text-lg'>음악을 검색하는 중...</p>
      </div>
    </div>
  );
}
