import { Music, Search } from 'lucide-react';

interface Props {
  onClick: () => void;
}

export default function Header({ onClick }: Props) {
  return (
    <header className='md:hidden sticky top-0 z-50 backdrop-blur-lg bg-white/10 border-b border-white/20 mb-8'>
      <div className='flex justify-between items-center py-4'>
        <div className='flex items-center space-x-3'>
          <div className='w-10 h-10 bg-gradient-to-r from-pink-500 to-violet-500 rounded-xl flex items-center justify-center'>
            <Music className='w-6 h-6 text-white' />
          </div>
          <h1 className='text-2xl font-bold bg-gradient-to-r from-pink-400 to-violet-400 bg-clip-text text-transparent'>
            MelLearn
          </h1>
        </div>

        <button
          onClick={onClick}
          className='w-12 h-12 bg-white/10 backdrop-blur-lg rounded-xl border border-white/20 flex items-center justify-center hover:bg-white/20 transition-all duration-300'
        >
          <Search className='w-5 h-5 text-white' />
        </button>
      </div>
    </header>
  );
}
