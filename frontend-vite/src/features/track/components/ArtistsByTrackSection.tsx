import SectionLayout from '@/components/SectionLayout';
import type { Artist } from '@/features/home/types/home';
import { Music } from 'lucide-react';

interface Props {
  artists: Artist[];
  onClick: (id: string) => void;
}

export default function ArtistsByTrackSection({ artists, onClick }: Props) {
  return (
    <SectionLayout title='아티스트'>
      <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4'>
        {artists.map(({ id, name }) => (
          <button
            key={id}
            onClick={() => onClick(id)}
            className='group p-4 bg-white/5 hover:bg-white/10 rounded-xl border border-white/10 hover:border-white/20 transition-all duration-300 text-left'
          >
            <div className='flex items-center space-x-3'>
              <div className='p-3 bg-gradient-to-r from-pink-500/20 to-violet-500/20 rounded-xl group-hover:from-pink-500/30 group-hover:to-violet-500/30 transition-all duration-300'>
                <Music className='w-6 h-6 text-white/70' />
              </div>
              <div>
                <div className='text-white font-semibold group-hover:text-violet-300 transition-colors duration-300'>
                  {name}
                </div>
              </div>
            </div>
          </button>
        ))}
      </div>
    </SectionLayout>
  );
}
