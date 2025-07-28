import type { LucideIcon } from 'lucide-react';

interface Props {
  Icon: LucideIcon;
  title: string;
  name: string;
}

export default function HeroTitle({ Icon, title, name }: Props) {
  return (
    <>
      <div className='flex items-center justify-center lg:justify-start space-x-3 mb-4'>
        <div className='p-3 bg-gradient-to-r from-pink-500 to-violet-500 rounded-xl'>
          <Icon className='w-8 h-8 text-white' />
        </div>
        <span className='text-white/70 text-lg'>{title}</span>
      </div>
      <h1 className='text-4xl sm:text-5xl lg:text-6xl font-bold text-white mb-4'>
        {name}
      </h1>
    </>
  );
}
