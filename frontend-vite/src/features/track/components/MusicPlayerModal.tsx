import { useState, useEffect } from 'react';
import {
  X,
  Play,
  Pause,
  SkipBack,
  SkipForward,
  Volume2,
  Heart,
  Share2,
  MoreHorizontal,
  Repeat,
  Shuffle,
} from 'lucide-react';
import type { Track } from '@/features/home/types/home';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  track: Track;
  onPlay: () => void;
  onPause: () => void;
  isPlaying: boolean;
}

export default function MusicPlayerModal({
  isOpen,
  onClose,
  track,
  onPlay,
  onPause,
  isPlaying,
}: Props) {
  const [currentTime, setCurrentTime] = useState(0);
  const [volume, setVolume] = useState(70);
  const [isLiked, setIsLiked] = useState(false);
  const [showLyrics, setShowLyrics] = useState(false);

  // Mock lyrics - 실제로는 API에서 가져와야 함
  const mockLyrics = [
    { time: 0, text: '가사를 불러오는 중...' },
    { time: 10, text: '첫 번째 가사 라인' },
    { time: 20, text: '두 번째 가사 라인' },
    { time: 30, text: '세 번째 가사 라인' },
  ];

  useEffect(() => {
    if (isPlaying) {
      const interval = setInterval(() => {
        setCurrentTime((prev) => prev + 1);
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [isPlaying]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const totalDuration = Math.floor((track.duration_ms || 0) / 1000);
  const progress = totalDuration > 0 ? (currentTime / totalDuration) * 100 : 0;

  if (!isOpen) return null;

  return (
    <div className='fixed inset-0 z-50 bg-black/90 backdrop-blur-lg flex items-center justify-center p-4'>
      <div className='w-full max-w-6xl h-full max-h-[90vh] bg-gradient-to-br from-purple-900/95 via-blue-900/95 to-indigo-900/95 backdrop-blur-xl rounded-2xl border border-white/10 overflow-hidden'>
        {/* Header */}
        <div className='flex items-center justify-between p-6 border-b border-white/10'>
          <div className='flex items-center space-x-4'>
            <button
              onClick={onClose}
              className='p-2 hover:bg-white/10 rounded-full transition-colors'
            >
              <X className='w-6 h-6 text-white' />
            </button>
            <div>
              <h2 className='text-white font-semibold'>재생 중</h2>
              <p className='text-white/60 text-sm'>{track.artists[0]?.name}</p>
            </div>
          </div>

          <div className='flex items-center space-x-2'>
            <button className='p-2 hover:bg-white/10 rounded-full transition-colors'>
              <MoreHorizontal className='w-6 h-6 text-white' />
            </button>
          </div>
        </div>

        <div className='flex flex-1 h-full'>
          {/* Left Panel - Album Art & Info */}
          <div className='flex-1 flex flex-col items-center justify-center p-8 space-y-8'>
            {/* Album Cover */}
            <div className='relative group'>
              <div className='w-80 h-80 bg-gradient-to-br from-pink-500/20 to-violet-500/20 rounded-2xl shadow-2xl overflow-hidden'>
                {track.album?.images?.[0] ? (
                  <img
                    src={track.album.images[0].url}
                    alt={track.name}
                    className='w-full h-full object-cover'
                  />
                ) : (
                  <div className='w-full h-full flex items-center justify-center'>
                    <div className='w-24 h-24 bg-white/20 rounded-full flex items-center justify-center'>
                      <Play className='w-12 h-12 text-white' />
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Track Info */}
            <div className='text-center space-y-2'>
              <h1 className='text-3xl font-bold text-white'>{track.name}</h1>
              <div className='flex items-center justify-center space-x-2'>
                {track.artists.map((artist, index) => (
                  <span key={artist.id} className='text-xl text-white/80'>
                    {artist.name}
                    {index < track.artists.length - 1 && ', '}
                  </span>
                ))}
              </div>
              {track.album && (
                <p className='text-white/60'>{track.album.name}</p>
              )}
            </div>

            {/* Action Buttons */}
            <div className='flex items-center space-x-4'>
              <button
                onClick={() => setIsLiked(!isLiked)}
                className={`p-3 rounded-full transition-all ${
                  isLiked
                    ? 'bg-pink-500 text-white'
                    : 'bg-white/10 hover:bg-white/20 text-white'
                }`}
              >
                <Heart className='w-6 h-6' />
              </button>

              <button className='p-3 bg-white/10 hover:bg-white/20 rounded-full transition-colors'>
                <Share2 className='w-6 h-6 text-white' />
              </button>
            </div>

            {/* Progress Bar */}
            <div className='w-full max-w-md space-y-2'>
              <div className='relative h-2 bg-white/20 rounded-full overflow-hidden'>
                <div
                  className='absolute left-0 top-0 h-full bg-gradient-to-r from-pink-500 to-violet-500 transition-all duration-300'
                  style={{ width: `${progress}%` }}
                />
              </div>
              <div className='flex justify-between text-sm text-white/60'>
                <span>{formatTime(currentTime)}</span>
                <span>{formatTime(totalDuration)}</span>
              </div>
            </div>

            {/* Playback Controls */}
            <div className='flex items-center space-x-6'>
              <button className='p-2 hover:bg-white/10 rounded-full transition-colors'>
                <Shuffle className='w-5 h-5 text-white/60' />
              </button>

              <button className='p-3 hover:bg-white/10 rounded-full transition-colors'>
                <SkipBack className='w-6 h-6 text-white' />
              </button>

              <button
                onClick={isPlaying ? onPause : onPlay}
                className='p-4 bg-gradient-to-r from-pink-500 to-violet-500 hover:from-pink-600 hover:to-violet-600 rounded-full transition-all hover:scale-105'
              >
                {isPlaying ? (
                  <Pause className='w-8 h-8 text-white' />
                ) : (
                  <Play className='w-8 h-8 text-white ml-1' />
                )}
              </button>

              <button className='p-3 hover:bg-white/10 rounded-full transition-colors'>
                <SkipForward className='w-6 h-6 text-white' />
              </button>

              <button className='p-2 hover:bg-white/10 rounded-full transition-colors'>
                <Repeat className='w-5 h-5 text-white/60' />
              </button>
            </div>

            {/* Volume Control */}
            <div className='flex items-center space-x-3 w-full max-w-xs'>
              <Volume2 className='w-5 h-5 text-white/60' />
              <div className='flex-1 relative h-1 bg-white/20 rounded-full'>
                <div
                  className='absolute left-0 top-0 h-full bg-white/60 rounded-full'
                  style={{ width: `${volume}%` }}
                />
              </div>
              <span className='text-sm text-white/60 w-8'>{volume}</span>
            </div>
          </div>

          {/* Right Panel - Lyrics */}
          <div className='w-96 border-l border-white/10 bg-black/20'>
            <div className='p-6'>
              <div className='flex items-center justify-between mb-6'>
                <h3 className='text-xl font-semibold text-white'>가사</h3>
                <button
                  onClick={() => setShowLyrics(!showLyrics)}
                  className='text-sm text-white/60 hover:text-white transition-colors'
                >
                  {showLyrics ? '숨기기' : '보기'}
                </button>
              </div>

              {showLyrics && (
                <div className='space-y-4 max-h-96 overflow-y-auto'>
                  {mockLyrics.map((line, index) => (
                    <div
                      key={index}
                      className={`transition-all duration-300 ${
                        currentTime >= line.time
                          ? 'text-white font-medium'
                          : 'text-white/40'
                      }`}
                    >
                      {line.text}
                    </div>
                  ))}
                </div>
              )}

              {!showLyrics && (
                <div className='text-center py-12'>
                  <div className='w-16 h-16 bg-white/10 rounded-full flex items-center justify-center mx-auto mb-4'>
                    <Play className='w-8 h-8 text-white/60' />
                  </div>
                  <p className='text-white/60'>
                    가사를 보려면 '보기'를 클릭하세요
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
