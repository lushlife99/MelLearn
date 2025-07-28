import type { Track } from '@/features/home/types/home';
import { formatDuration } from '@/pages/track/utils/format';
import { ExternalLink, Play } from 'lucide-react';

interface Props {
  title: string;
  tracks: Track[];
  onClick: (id: string) => void;
}

export default function TrackSection({ title, tracks, onClick }: Props) {
  return (
    <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10'>
      <h2 className='text-2xl font-bold text-white mb-6'>{title}</h2>
      <div className='space-y-4'>
        {tracks.slice(0, 10).map((track, index) => (
          <div
            key={track.id}
            onClick={() => onClick(track.id)}
            className='group flex items-center p-4 bg-white/5 hover:bg-white/10 rounded-xl border border-white/10 hover:border-white/20 transition-all duration-300 cursor-pointer'
          >
            <div className='flex-shrink-0 w-8 text-center mr-4'>
              <span className='text-white/70 text-lg font-semibold group-hover:text-white transition-colors duration-300'>
                {index + 1}
              </span>
            </div>

            <div className='flex-shrink-0 mr-4'>
              <div className='relative'>
                <img
                  src={track.album.images?.[0]?.url}
                  alt={track.album.name}
                  className='w-12 h-12 rounded-lg object-cover'
                />
                <div className='absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-lg flex items-center justify-center'>
                  <Play className='w-4 h-4 text-white' />
                </div>
              </div>
            </div>

            <div className='flex-1 min-w-0'>
              <div className='text-white font-semibold text-sm truncate group-hover:text-violet-300 transition-colors duration-300'>
                {track.name}
              </div>
              <div className='text-white/60 text-xs truncate mt-1'>
                {track.album.name}
              </div>
            </div>

            {track.duration_ms && (
              <div className='flex-shrink-0 ml-4'>
                <span className='text-white/60 text-sm'>
                  {formatDuration(track.duration_ms)}
                </span>
              </div>
            )}

            {/* External Link */}
            {track.external_urls?.spotify && (
              <div className='flex-shrink-0 ml-4'>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    window.open(track.external_urls.spotify, '_blank');
                  }}
                  className='p-2 rounded-lg bg-green-600/20 hover:bg-green-600/40 transition-colors duration-300'
                >
                  <ExternalLink className='w-4 h-4 text-green-400' />
                </button>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Show More Button */}
      {tracks.length > 10 && (
        <div className='text-center mt-6'>
          <button className='text-violet-400 hover:text-violet-300 font-semibold transition-colors duration-300'>
            더 많은 트랙 보기 ({tracks.length - 10}개 더)
          </button>
        </div>
      )}
    </div>
  );
}
