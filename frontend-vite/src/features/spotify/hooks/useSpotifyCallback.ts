import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { fetchSpotifyToken } from '../services/spotifyApi';

export function useSpotifyCallback() {
  const location = useLocation();
  const navigate = useNavigate();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    'loading'
  );
  const [error, setError] = useState<string | null>(null);
  const didRun = useRef(false);

  useEffect(() => {
    if (didRun.current) return;
    didRun.current = true;

    const processCallback = async () => {
      try {
        const query = new URLSearchParams(location.search);
        const code = query.get('code');
        const errorParam = query.get('error');
        const state = query.get('state');

        const storedState = localStorage.getItem('spotify_state');
        if (state !== storedState) {
          throw new Error('잘못된 state 파라미터입니다.');
        }

        const redirectUri =
          import.meta.env.VITE_SPOTIFY_REDIRECT_URI ||
          'http://localhost:3000/callback';
        const codeVerifier = localStorage.getItem('spotify_code_verifier');

        if (errorParam) {
          throw new Error('사용자가 Spotify 인증을 거부했습니다.');
        }

        if (!code || !codeVerifier) {
          throw new Error('Spotify 인증 코드 또는 verifier가 누락되었습니다.');
        }

        const tokenResponse = await fetchSpotifyToken(
          code,
          codeVerifier,
          redirectUri
        );

        const expiresAt = Date.now() + tokenResponse.expires_in * 1000;
        localStorage.setItem(
          'spotify_access_token',
          tokenResponse.access_token
        );
        localStorage.setItem(
          'spotify_refresh_token',
          tokenResponse.refresh_token
        );
        localStorage.setItem('spotify_token_expires_at', expiresAt.toString());

        localStorage.removeItem('spotify_code_verifier');
        localStorage.removeItem('spotify_state');

        setStatus('success');
        setTimeout(() => navigate('/'), 1000);
      } catch (err) {
        console.error('Spotify callback error:', err);
        // setError(err.message || '인증 처리 중 오류가 발생했습니다.');
        setStatus('error');

        localStorage.removeItem('spotify_code_verifier');
        localStorage.removeItem('spotify_state');

        setTimeout(() => navigate('/spotify-login'), 2000);
      }
    };

    processCallback();
  }, [location, navigate]);

  return { status, error };
}
