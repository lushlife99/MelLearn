import type { Type } from '@/features/home/types/home';
import useSpotifyPlayer from '@/features/spotify/hooks/useSpotifyPlayer';
import MusicPlayerModal from '@/features/track/components/MusicPlayerModal';
import useTrack from '@/features/track/hooks/useTrack';
import { useSpotifyStore } from '@/store/useSpotifyStore';
import { ExternalLink, Heart, Pause, Play, Share2 } from 'lucide-react';
import { useState } from 'react';
import TrackActionButton from '../features/track/components/TrackActionButton';

interface Props {
  spotify: string;
  trackId?: string;
  type: Type;
}

export default function ActionButton({ spotify, trackId, type }: Props) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [isStarted, setIsStarted] = useState(false);

  const [isPlaying, setIsPlaying] = useState(false);

  const deviceId = useSpotifyStore((state) => state.deviceId);
  const { play, resume, pause } = useSpotifyPlayer();
  const { track } = useTrack(trackId || '');

  const handlePlay = async () => {
    if (!deviceId || !trackId) return;
    if (isStarted) {
      resume(deviceId, {
        onSuccess: () => setIsPlaying(true),
      });
      return;
    }
    play(
      { deviceId, trackId },
      {
        onSuccess: () => {
          setIsStarted(true);

          setIsPlaying(true);
          setIsModalOpen(true);
        },
      }
    );
  };

  const handlePause = async () => {
    if (!deviceId) return;

    pause(deviceId, {
      onSuccess: () => setIsPlaying(false),
    });
  };

  if (!deviceId) return <div>로딩중</div>;
  return (
    <div className='flex flex-wrap justify-center lg:justify-start gap-4'>
      {type === 'track' ? (
        isPlaying ? (
          <TrackActionButton
            onClick={handlePause}
            Icon={Pause}
            label='일시정지'
          />
        ) : (
          <TrackActionButton onClick={handlePlay} Icon={Play} label='재생' />
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
      {isModalOpen && track && (
        <MusicPlayerModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          track={track}
          onPlay={handlePlay}
          onPause={handlePause}
          isPlaying={isPlaying}
        />
      )}
    </div>
  );
}
