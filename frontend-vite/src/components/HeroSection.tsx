import type { Artist, Track } from '@/features/home/types/home';
import HeroImage from './HeroImage';
import ArtistHeroInfo from '@/features/artist/components/ArtistHeroInfo';
import TrackHeroInfo from '@/features/track/components/TrackHeroInfo';
interface Props {
  src: string;
  alt: string;
  item: Artist | Track;
  type: 'artist' | 'track';
}

export default function HeroSection({ src, alt, item, type }: Props) {
  return (
    <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 mb-8'>
      <div className='flex flex-col lg:flex-row gap-8'>
        <HeroImage src={src} alt={alt} />
        {type === 'artist' ? (
          <ArtistHeroInfo artist={item as Artist} />
        ) : (
          <TrackHeroInfo track={item as Track} />
        )}
      </div>
    </div>
  );
}
