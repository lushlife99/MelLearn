import useTrack from '@/features/track/hooks/useTrack';
import { ROUTES } from '@/services/router';
import { Music } from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import ArrowBack from '@/components/ArrowBack';
import ArtistTrackLayout from '@/components/ArtistTrackLayout';
import HeroSection from '@/components/HeroSection';
import useLyric from '@/features/track/hooks/useLyric';
import AlbumSection from '@/features/track/components/AlbumSection';
import ArtistsByTrackSection from '@/features/track/components/ArtistsByTrackSection';
import MusicPlayer from '@/features/track/components/MusicPlayer';
import { useSpotifyStore } from '@/store/useSpotifyStore';

export default function TrackDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { track, isLoading, error } = useTrack(id || '');
  const { lyrics } = useLyric(track);
  const isPlayerOpen = useSpotifyStore((state) => state.isPlayerOpen);
  const setIsPlayerOpen = useSpotifyStore((state) => state.setIsPlayerOpen);
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
      {isPlayerOpen ? (
        <MusicPlayer
          track={track}
          onClose={() => setIsPlayerOpen(false)}
          lyrics={lyrics}
        />
      ) : (
        <>
          <div className='mb-8'>
            <ArrowBack onClick={() => navigate(-1)} />
          </div>

          <HeroSection
            src={track.album.images?.[0]?.url}
            alt={track.album.name}
            item={track}
            type='track'
          />

          <AlbumSection album={track.album} />

          <ArtistsByTrackSection
            artists={track.artists}
            onClick={(id) => navigate(ROUTES.ARTIST_DETAIL(id))}
          />
        </>
      )}
    </ArtistTrackLayout>
  );
}
