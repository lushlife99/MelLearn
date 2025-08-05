import SectionLayout from '@/components/SectionLayout';
import type { Artist } from '@/features/home/types/home';
interface Props {
  artists: Artist[];
  onClick: (id: string) => void;
}

export default function ArtistsSection({ artists, onClick }: Props) {
  return (
    <SectionLayout title={`전체 아티스트 (${artists.length})`}>
      <div className='grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 sm:gap-6'>
        {artists.map(({ id, name, images }) => (
          <div
            key={id}
            onClick={() => onClick(id)}
            className='group cursor-pointer transition-all duration-300 hover:scale-105'
          >
            <div className='bg-white/5 backdrop-blur-sm rounded-2xl p-4 border border-white/10 hover:border-white/20 hover:bg-white/10 transition-all duration-300'>
              <div className='relative mb-4'>
                <img
                  src={images?.[0]?.url}
                  alt={name}
                  className='w-full aspect-square object-cover rounded-xl group-hover:shadow-lg transition-all duration-300'
                />
                <div className='absolute inset-0 bg-gradient-to-t from-black/20 to-transparent rounded-xl opacity-0 group-hover:opacity-100 transition-all duration-300' />
              </div>
              <div className='text-center'>
                <h3 className='text-white font-semibold text-sm mb-1 truncate group-hover:text-violet-300 transition-colors duration-300'>
                  {name}
                </h3>
              </div>
            </div>
          </div>
        ))}
      </div>
    </SectionLayout>
  );
}
