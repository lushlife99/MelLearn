import { ArrowRight } from 'lucide-react';

interface Props {
  category: string;
  onClick: (category: string) => void;
}

export default function CategoryCard({ category, onClick }: Props) {
  return (
    <div
      className='group relative bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 hover:border-white/20 transition-all duration-500 cursor-pointer transform hover:scale-105'
      onClick={() => onClick(category)}
    >
      <div className='mb-6'>
        <h3 className='text-2xl font-bold text-white'>{category}</h3>
      </div>

      <div className='flex items-center justify-between'>
        <div className='text-white/50 text-sm'>지금 시작해보세요</div>
        <div className='flex items-center gap-2 text-white group-hover:text-pink-300 transition-colors duration-300'>
          <span className='font-medium'>학습하기</span>
          <ArrowRight className='w-5 h-5 transform group-hover:translate-x-1 transition-transform duration-300' />
        </div>
      </div>
    </div>
  );
}
