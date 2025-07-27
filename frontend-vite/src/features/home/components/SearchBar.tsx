import { Search } from 'lucide-react';

interface Props {
  onClick: () => void;
}

export default function SearchBar({ onClick }: Props) {
  return (
    <div className='hidden md:block mb-8 pt-4 lg:pt-8'>
      <div className='flex justify-center lg:justify-end'>
        <div className='relative w-96 max-w-md'>
          <input
            placeholder='노래를 검색해주세요'
            className='w-full h-12 pl-4 pr-12 bg-white/10 backdrop-blur-lg text-white placeholder-gray-300 rounded-xl border border-white/20 focus:border-pink-400 focus:outline-none focus:ring-2 focus:ring-pink-400/50 transition-all duration-300'
            onClick={onClick}
            readOnly
          />
          <Search className='absolute right-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-300' />
        </div>
      </div>
    </div>
  );
}
