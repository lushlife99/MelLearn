import {
  Play,
  Pause,
  SkipBack,
  SkipForward,
  Volume2,
  X,
  Heart,
  Repeat,
  Shuffle,
} from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import type { Lyric } from '../types/track';
import type { Track } from '@/features/home/types/home';
import { useSpotifyStore } from '@/store/useSpotifyStore';
import TrackActionButton from './TrackActionButton';
import useSpotifyPlayer from '@/features/spotify/hooks/useSpotifyPlayer';
import SyncedLyrics from './SyncedLyrics';

interface Props {
  track: Track;
  onClose: () => void;
  lyrics?: Lyric[];
}

export default function MusicPlayer({ track, onClose, lyrics }: Props) {
  const [currentTime, setCurrentTime] = useState(0);
  const [volume, setVolume] = useState(70);
  const [isLiked, setIsLiked] = useState(false);
  const [isShuffled, setIsShuffled] = useState(false);
  const [repeatMode, setRepeatMode] = useState<'off' | 'all' | 'one'>('off');

  const player = useSpotifyStore((state) => state.player);
  const currentTimeRef = useRef(0);

  useEffect(() => {
    const interval = setInterval(() => {
      player?.getCurrentState().then((state) => {
        if (state) {
          currentTimeRef.current = state.position / 1000;
          setCurrentTime(state.position / 1000); // 진행바용
        }
      });
    }, 200);

    return () => clearInterval(interval);
  }, [player]);

  const isPlaying = useSpotifyStore((state) => state.isPlaying);
  const currentTrackId = useSpotifyStore((state) => state.currentTrackId);
  const { play, pause } = useSpotifyPlayer();

  const isThisPlaying = currentTrackId === track.id && isPlaying;
  const formatTime = (ms: number) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  const progress = (currentTime / track.duration_ms) * 100;
  const artistNames = track.artists.map((artist) => artist.name).join(', ');

  return (
    <div className='space-y-10'>
      {/* 헤더 */}
      <div className='flex justify-between items-center'>
        <h1 className='text-2xl font-bold text-white'>음악 플레이어</h1>
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
        <div className='flex-shrink-0 mx-auto lg:mx-0'>
          <div className='relative w-72 h-72 rounded-2xl overflow-hidden shadow-2xl'>
            {track.album.images?.[0]?.url ? (
              <img
                src={track.album.images[0].url}
                alt={track.album.name}
                className='w-full h-full object-cover'
              />
            ) : (
              <div className='w-full h-full bg-gradient-to-br from-purple-600 to-pink-600 flex items-center justify-center'>
                <Play className='w-20 h-20 text-white/50' />
              </div>
            )}
          </div>
        </div>

        {/* 가사 영역 */}
        <SyncedLyrics
          lyrics={lyrics}
          getCurrentTime={() => currentTimeRef.current}
        />
      </div>

      {/* 재생 컨트롤 */}
      <div className='space-y-6'>
        {/* 트랙 정보 */}
        <div className='text-center'>
          <h2 className='text-3xl font-bold text-white mb-2 truncate'>
            {track.name}
          </h2>
          <p className='text-xl text-white/70 mb-4 truncate'>{artistNames}</p>
        </div>

        {/* 진행 바 */}
        <div className='space-y-2'>
          <div className='w-full bg-white/20 rounded-full h-2 cursor-pointer'>
            <div
              className='bg-gradient-to-r from-purple-400 to-pink-400 h-2 rounded-full transition-all duration-300'
              style={{ width: `${progress}%` }}
            />
          </div>
          <div className='flex justify-between text-sm text-white/60'>
            <span>{formatTime(currentTime)}</span>
            <span>{formatTime(track.duration_ms)}</span>
          </div>
        </div>

        {/* 버튼들 */}
        <div className='flex items-center justify-center gap-6'>
          <button
            onClick={() => setIsShuffled(!isShuffled)}
            className={`p-3 rounded-full transition-colors ${
              isShuffled
                ? 'bg-purple-500 text-white'
                : 'bg-white/10 text-white/70 hover:bg-white/20'
            }`}
          >
            <Shuffle className='w-5 h-5' />
          </button>

          <button className='p-3 rounded-full bg-white/10 hover:bg-white/20 transition-colors'>
            <SkipBack className='w-6 h-6 text-white' />
          </button>
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

          <button className='p-3 rounded-full bg-white/10 hover:bg-white/20 transition-colors'>
            <SkipForward className='w-6 h-6 text-white' />
          </button>

          <button
            onClick={() => {
              const modes: Array<'off' | 'all' | 'one'> = ['off', 'all', 'one'];
              const currentIndex = modes.indexOf(repeatMode);
              const nextIndex = (currentIndex + 1) % modes.length;
              setRepeatMode(modes[nextIndex]);
            }}
            className={`relative p-3 rounded-full transition-colors ${
              repeatMode !== 'off'
                ? 'bg-purple-500 text-white'
                : 'bg-white/10 text-white/70 hover:bg-white/20'
            }`}
          >
            <Repeat className='w-5 h-5' />
            {repeatMode === 'one' && (
              <span className='absolute -mt-1 -mr-1 text-xs'>1</span>
            )}
          </button>
        </div>

        {/* 하단 좋아요 + 볼륨 */}
        <div className='flex items-center justify-between'>
          <button
            onClick={() => setIsLiked(!isLiked)}
            className={`p-2 rounded-full transition-colors ${
              isLiked ? 'text-red-500' : 'text-white/70 hover:text-white'
            }`}
          >
            <Heart className={`w-5 h-5 ${isLiked ? 'fill-current' : ''}`} />
          </button>

          <div className='flex items-center gap-3'>
            <Volume2 className='w-5 h-5 text-white/70' />
            <div className='w-24 bg-white/20 rounded-full h-1'>
              <div
                className='bg-white h-1 rounded-full'
                style={{ width: `${volume}%` }}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
