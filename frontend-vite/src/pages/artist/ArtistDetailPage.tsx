import ArrowBack from '@/components/ArrowBack';
import ArtistTrackLayout from '@/components/ArtistTrackLayout';
import HeroSection from '@/components/HeroSection';
import useArtist from '@/features/artist/hooks/useArtist';
import { ROUTES } from '@/services/router';
import { Users } from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import GenreSection from '@/features/artist/components/GenreSection';
import TrackSection from '@/components/TrackSection';

export default function ArtistDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { artist, isLoading, error, topTracksByArtist } = useArtist(id || '');

  if (isLoading) {
    return (
      <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 flex items-center justify-center'>
        <div className='text-white text-xl'>로딩 중...</div>
      </div>
    );
  }

  if (error || !artist) {
    return (
      <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 flex items-center justify-center'>
        <div className='text-center'>
          <Users className='w-16 h-16 text-white/30 mx-auto mb-4' />
          <p className='text-white/50 text-lg'>아티스트를 찾을 수 없습니다</p>
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
        src={artist.images?.[0]?.url}
        alt={artist.name}
        item={artist}
        type='artist'
      />

      {artist.genres && artist.genres.length > 0 && (
        <GenreSection genres={artist.genres} />
      )}

      {topTracksByArtist && topTracksByArtist.length > 0 && (
        <TrackSection
          title='인기 트랙'
          tracks={topTracksByArtist}
          onClick={(id) => navigate(ROUTES.TRACK_DETAIL(id))}
        />
      )}
    </ArtistTrackLayout>
  );
}
