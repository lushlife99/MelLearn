import { type LucideIcon } from 'lucide-react';

interface Props {
  title: string;
  description: string;
  onClick: () => void;
  Icon: LucideIcon;
}

export default function SectionHeader({
  title,
  description,
  onClick,
  Icon,
}: Props) {
  return (
    <div className='flex items-center justify-between mb-6'>
      <div className='flex items-center space-x-3'>
        <div className='w-12 h-12 bg-gradient-to-r from-red-500 to-pink-500 rounded-xl flex items-center justify-center'>
          <Icon className='w-6 h-6 text-white' />
        </div>
        <div>
          <h3 className='text-2xl font-bold text-white'>{title}</h3>
          <p className='text-gray-400'>{description}</p>
        </div>
      </div>
      <button
        onClick={onClick}
        className='px-4 py-2 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-lg hover:shadow-lg transform hover:scale-105 transition-all duration-300 opacity-0 group-hover:opacity-100'
      >
        더보기
      </button>
    </div>
  );
}
