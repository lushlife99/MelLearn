import ActionButton from '@/components/ActionButton';
import HeroTitle from '@/components/HeroTitle';
import type { Track } from '@/features/home/types/home';
import { formatDuration } from '@/features/track/utils/format';
import { ROUTES } from '@/services/router';
import { useNavigate } from 'react-router-dom';

interface Props {
  track: Track;
}

export default function TrackHeroInfo({ track }: Props) {
  const { id, name, artists, duration_ms, external_urls } = track;
  const navigate = useNavigate();
  return (
    <div className='flex-1 flex flex-col justify-center text-center lg:text-left'>
      <div className='mb-4'>
        <HeroTitle name={name} />

        <div className='mb-6'>
          <div className='flex flex-wrap justify-center lg:justify-start gap-2'>
            {artists.map((artist, index) => (
              <button
                key={artist.id}
                onClick={() => navigate(ROUTES.ARTIST_DETAIL(artist.id))}
                className='text-white/80 hover:text-white text-xl font-semibold transition-colors duration-300'
              >
                {artist.name}
                {index < artists.length - 1 && ', '}
              </button>
            ))}
          </div>
        </div>

        <div className='flex flex-wrap justify-center lg:justify-start gap-6 mb-6'>
          {duration_ms && (
            <div className='text-center lg:text-left'>
              <div className='text-2xl font-bold text-white'>
                {formatDuration(duration_ms)}
              </div>
            </div>
          )}
        </div>
      </div>

      <ActionButton type='track' spotify={external_urls.spotify} trackId={id} />
    </div>
  );
}
