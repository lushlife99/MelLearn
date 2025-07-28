import useTrack from '@/features/track/hooks/useTrack';
import { ROUTES } from '@/services/router';
import { Music, Calendar, Disc3 } from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import { formatReleaseDate } from './utils/format';
import ArrowBack from '@/components/ArrowBack';
import ArtistTrackLayout from '@/components/ArtistTrackLayout';
import HeroSection from '@/components/HeroSection';
import useLyric from '@/features/track/hooks/useLyric';

export default function TrackDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { track, isLoading, error } = useTrack(id || '');
  const { lyric } = useLyric(track);
  console.log(lyric);
  if (isLoading) {
    return (
      <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 flex items-center justify-center'>
        <div className='text-white text-xl'>로딩 중...</div>
      </div>
    );
  }

  if (error || !track) {
    return (
      <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 flex items-center justify-center'>
        <div className='text-center'>
          <Music className='w-16 h-16 text-white/30 mx-auto mb-4' />
          <p className='text-white/50 text-lg'>트랙을 찾을 수 없습니다</p>
        </div>
      </div>
    );
  }

  return (
    <ArtistTrackLayout>
      <div className='mb-8'>
        <ArrowBack onClick={() => navigate(-1)} />
      </div>

      <HeroSection
        src={track.album.images?.[0]?.url}
        alt={track.album.name}
        item={track}
        type='track'
      />

      {/* Album Info Section */}
      <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 mb-8'>
        <h2 className='text-2xl font-bold text-white mb-6'>앨범 정보</h2>
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
          <div className='flex items-center space-x-3'>
            <div className='p-3 bg-white/10 rounded-xl'>
              <Disc3 className='w-6 h-6 text-white/70' />
            </div>
            <div>
              <div className='text-white/70 text-sm'>앨범명</div>
              <div className='text-white font-semibold'>{track.album.name}</div>
            </div>
          </div>

          {track.album.release_date && (
            <div className='flex items-center space-x-3'>
              <div className='p-3 bg-white/10 rounded-xl'>
                <Calendar className='w-6 h-6 text-white/70' />
              </div>
              <div>
                <div className='text-white/70 text-sm'>발매일</div>
                <div className='text-white font-semibold'>
                  {formatReleaseDate(track.album.release_date)}
                </div>
              </div>
            </div>
          )}

          {track.album.total_tracks && (
            <div className='flex items-center space-x-3'>
              <div className='p-3 bg-white/10 rounded-xl'>
                <Music className='w-6 h-6 text-white/70' />
              </div>
              <div>
                <div className='text-white/70 text-sm'>총 트랙 수</div>
                <div className='text-white font-semibold'>
                  {track.album.total_tracks}곡
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Artists Section */}
      {track.artists && track.artists.length > 0 && (
        <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10'>
          <h2 className='text-2xl font-bold text-white mb-6'>아티스트</h2>
          <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4'>
            {track.artists.map((artist) => (
              <button
                key={artist.id}
                onClick={() => navigate(ROUTES.ARTIST_DETAIL(artist.id))}
                className='group p-4 bg-white/5 hover:bg-white/10 rounded-xl border border-white/10 hover:border-white/20 transition-all duration-300 text-left'
              >
                <div className='flex items-center space-x-3'>
                  <div className='p-3 bg-gradient-to-r from-pink-500/20 to-violet-500/20 rounded-xl group-hover:from-pink-500/30 group-hover:to-violet-500/30 transition-all duration-300'>
                    <Music className='w-6 h-6 text-white/70' />
                  </div>
                  <div>
                    <div className='text-white font-semibold group-hover:text-violet-300 transition-colors duration-300'>
                      {artist.name}
                    </div>
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </ArtistTrackLayout>
  );
}
