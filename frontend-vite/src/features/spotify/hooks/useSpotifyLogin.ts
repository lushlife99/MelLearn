import { createPKCEPair, generateRandomString } from '../utils/pkce';

export function useSpotifyLogin() {
  const loginWithSpotify = async () => {
    try {
      const { codeVerifier, codeChallenge } = await createPKCEPair();
      const state = generateRandomString(16); // CSRF 보호를 위한 state

      localStorage.setItem('spotify_code_verifier', codeVerifier);
      localStorage.setItem('spotify_state', state);

      const clientId = import.meta.env.VITE_SPOTIFY_CLIENT_ID;
      if (!clientId) {
        throw new Error('Spotify Client ID가 설정되지 않았습니다.');
      }

      const redirectUri =
        import.meta.env.VITE_SPOTIFY_REDIRECT_URI ||
        'http://localhost:3000/callback';

      const scope = [
        'user-read-private',
        'user-read-email',
        'user-read-playback-state',
        'user-modify-playback-state',
        'playlist-read-private',
        'streaming',
      ].join(' ');

      const params = new URLSearchParams({
        response_type: 'code',
        client_id: clientId,
        scope,
        code_challenge_method: 'S256',
        code_challenge: codeChallenge,
        redirect_uri: redirectUri,
        state,
      });

      const authUrl = `https://accounts.spotify.com/authorize?${params.toString()}`;
      window.location.href = authUrl;
    } catch (error) {
      console.error('Spotify 로그인 초기화 실패:', error);
      throw error;
    }
  };

  return { loginWithSpotify };
}
