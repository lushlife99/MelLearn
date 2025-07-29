import type { Type } from '@/features/home/types/home';
import { useSpotifyStore } from '@/store/useSpotifyStore';
import { ExternalLink, Heart, Pause, Play, Share2 } from 'lucide-react';
import TrackActionButton from '../features/track/components/TrackActionButton';
import useSpotifyPlayer from '@/features/spotify/hooks/useSpotifyPlayer';

interface Props {
  spotify: string;
  trackId?: string;
  type: Type;
}

export default function ActionButton({ spotify, trackId, type }: Props) {
  const isPlaying = useSpotifyStore((state) => state.isPlaying);
  const currentTrackId = useSpotifyStore((state) => state.currentTrackId);

  const deviceId = useSpotifyStore((state) => state.deviceId);
  const isThisPlaying = currentTrackId === trackId && isPlaying;

  const { play, pause } = useSpotifyPlayer();

  if (!deviceId) return <div>로딩중</div>;
  return (
    <div className='flex flex-wrap justify-center lg:justify-start gap-4'>
      {type === 'track' ? (
        isThisPlaying ? (
          <TrackActionButton
            onClick={pause}
            Icon={Pause}
            label='일시정지'
            buttonClass='space-x-2 px-6 py-3 rounded-xl'
            iconClass='w-5 h-5'
          />
        ) : (
          <TrackActionButton
            onClick={() => play(trackId || '')}
            Icon={Play}
            label='재생'
            buttonClass='space-x-2 px-6 py-3 rounded-xl'
            iconClass='w-5 h-5'
          />
        )
      ) : null}

      <button className='flex items-center space-x-2 bg-white/10 hover:bg-white/20 text-white px-6 py-3 rounded-xl font-semibold transition-all duration-300 border border-white/20'>
        <Heart className='w-5 h-5' />
        <span>좋아요</span>
      </button>

      <button className='flex items-center space-x-2 bg-white/10 hover:bg-white/20 text-white px-6 py-3 rounded-xl font-semibold transition-all duration-300 border border-white/20'>
        <Share2 className='w-5 h-5' />
        <span>공유</span>
      </button>

      {spotify && (
        <button
          onClick={() => window.open(spotify, '_blank')}
          className='flex items-center space-x-2 bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-xl font-semibold transition-all duration-300'
        >
          <ExternalLink className='w-5 h-5' />
          <span>Spotify</span>
        </button>
      )}
    </div>
  );
}
