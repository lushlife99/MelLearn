import { useSpotifyLogin } from '../hooks/useSpotifyLogin';

export default function SpotifyLoginButton() {
  const { loginWithSpotify } = useSpotifyLogin();

  return (
    <button
      onClick={loginWithSpotify}
      type='button'
      className='flex items-center px-5 py-2 bg-spotify hover:bg-spotify-hover rounded-full shadow transition font-medium text-white'
    >
      <img
        src='/spotify-logo.png'
        alt='Spotify'
        width={28}
        height={28}
        className='mr-3'
        style={{ filter: 'drop-shadow(0 1px 2px rgba(0,0,0,0.12))' }}
      />
      <span className='text-base tracking-tight'>Spotify로 로그인</span>
    </button>
  );
}
