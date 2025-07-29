import type { LucideIcon } from 'lucide-react';

interface Props {
  Icon: LucideIcon;
  title: string;
  description: string;
}

export default function AlbumItem({ Icon, title, description }: Props) {
  return (
    <div className='flex items-center space-x-3'>
      <div className='p-3 bg-white/10 rounded-xl'>
        <Icon className='w-6 h-6 text-white/70' />
      </div>

      <div>
        <div className='text-white/70 text-sm'>{title}</div>
        <div className='text-white font-semibold'>{description}</div>
      </div>
    </div>
  );
}
