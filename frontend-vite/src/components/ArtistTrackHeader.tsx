import ArrowBack from './ArrowBack';
import type { LucideIcon } from 'lucide-react';

interface Props {
  Icon: LucideIcon;
  title: string;
  description: string;
  onClick: () => void;
}

export default function ArtistTrackHeader({
  Icon,
  title,
  description,
  onClick,
}: Props) {
  return (
    <div className='flex items-center mb-6'>
      <ArrowBack onClick={onClick} />

      <div className='flex items-center space-x-3'>
        <div className='p-3 bg-gradient-to-r from-pink-500 to-violet-500 rounded-xl'>
          <Icon className='w-8 h-8 text-white' />
        </div>
        <div>
          <h1 className='text-3xl sm:text-4xl font-bold text-white'>{title}</h1>
          <p className='text-white/70 mt-1'>{description}</p>
        </div>
      </div>
    </div>
  );
}
