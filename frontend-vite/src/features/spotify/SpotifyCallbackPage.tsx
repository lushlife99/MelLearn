import SpotifyLayout from './components/SpotifyLayout';
import SpotifyCallback from './components/SpotifyCallback';

export default function SpotifyCallbackPage() {
  return (
    <SpotifyLayout
      title='Spotify 인증 진행 중'
      description='잠시만 기다려주세요.'
    >
      <SpotifyCallback />
    </SpotifyLayout>
  );
}
