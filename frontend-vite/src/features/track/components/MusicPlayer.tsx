import { Play, Pause, Volume2, X, VolumeOff } from 'lucide-react';
import { useState } from 'react';
import type { Lyric } from '../types/track';
import type { Track } from '@/features/home/types/home';
import { useSpotifyStore } from '@/store/useSpotifyStore';
import TrackActionButton from './TrackActionButton';
import useSpotifyPlayer from '@/features/spotify/hooks/useSpotifyPlayer';
import SyncedLyrics from './SyncedLyrics';
import { formatDuration } from '../utils/format';
import HeroImage from '@/components/HeroImage';

interface Props {
  track: Track;
  onClose: () => void;
  lyrics?: Lyric[];
}

export default function MusicPlayer({ track, onClose, lyrics }: Props) {
  const [volume, setVolume] = useState(70);

  const player = useSpotifyStore((state) => state.player);

  const isPlaying = useSpotifyStore((state) => state.isPlaying);
  const currentTrackId = useSpotifyStore((state) => state.currentTrackId);
  const { play, pause, currentTime, currentTimeRef, isReady } =
    useSpotifyPlayer();

  const isThisPlaying = currentTrackId === track.id && isPlaying;

  const progress = (currentTime / track.duration_ms) * 100;
  const artistNames = track.artists.map((artist) => artist.name).join(', ');

  const handleSeek = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!player || !track.duration_ms) return;

    const bar = e.currentTarget.getBoundingClientRect();
    const clickX = e.clientX - bar.left;
    const ratio = clickX / bar.width;
    const seekMs = ratio * track.duration_ms;

    player.seek(seekMs);
  };

  const handleLyricClick = (time: number) => {
    if (!player) return;
    player.seek(time * 1000);
  };

  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newVolume = parseInt(e.target.value);
    setVolume(newVolume);
    player?.setVolume(newVolume / 100);
  };

  return (
    <div className='space-y-10'>
      {/* 헤더 */}
      <div className='flex justify-end'>
        <button
          onClick={onClose}
          className='p-2 rounded-full bg-white/10 backdrop-blur-sm hover:bg-white/20 transition-colors'
        >
          <X className='w-6 h-6 text-white' />
        </button>
      </div>

      {/* 앨범 + 가사 */}
      <div className='flex flex-col lg:flex-row gap-8'>
        {/* 앨범 아트 */}
        <HeroImage src={track.album.images?.[0]?.url} alt={track.album.name} />

        <SyncedLyrics
          lyrics={lyrics}
          getCurrentTime={() => currentTimeRef.current}
          onLyricClick={handleLyricClick}
        />
      </div>

      <div className='space-y-6'>
        <div className='text-center'>
          <h2 className='text-3xl font-bold text-white mb-2 truncate'>
            {track.name}
          </h2>
          <p className='text-xl text-white/70 mb-4 truncate'>{artistNames}</p>
        </div>

        <div className='space-y-2'>
          <div
            onClick={handleSeek}
            className='w-full bg-white/20 rounded-full h-2 cursor-pointer'
          >
            <div
              className={`bg-gradient-to-r from-purple-400 to-pink-400 h-2 rounded-full ${
                isReady ? 'transition-all duration-300' : ''
              }`}
              style={{ width: `${progress}%` }}
            />
          </div>
          <div className='flex justify-between text-sm text-white/60'>
            <span>{formatDuration(currentTime)}</span>
            <span>{formatDuration(track.duration_ms)}</span>
          </div>
        </div>

        {/* 버튼들 */}
        <div className='flex justify-center'>
          {isThisPlaying ? (
            <TrackActionButton
              onClick={pause}
              Icon={Pause}
              buttonClass='p-4 rounded-full'
              iconClass='w-8 h-8'
            />
          ) : (
            <TrackActionButton
              onClick={() => play(track.id)}
              Icon={Play}
              buttonClass='p-4 rounded-full'
              iconClass='w-8 h-8'
            />
          )}
        </div>

        <div className='flex justify-end'>
          <div className='flex items-center gap-3'>
            {volume === 0 ? (
              <VolumeOff className='w-5 h-5 text-white/70' />
            ) : (
              <Volume2 className='w-5 h-5 text-white/70' />
            )}

            <input
              type='range'
              min={0}
              max={100}
              step={1}
              value={volume}
              onChange={handleVolumeChange}
              className='w-24 accent-white/90 cursor-pointer'
            />
          </div>
        </div>
      </div>
    </div>
  );
}
