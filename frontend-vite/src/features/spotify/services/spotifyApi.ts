import { apiSpotify } from '@/services/axios';

export interface SpotifyTokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  scope: string;
  token_type: string;
}

export async function fetchSpotifyToken(
  code: string,
  codeVerifier: string,
  redirectUri: string
): Promise<SpotifyTokenResponse> {
  const clientId = import.meta.env.VITE_SPOTIFY_CLIENT_ID || '';
  const params = new URLSearchParams({
    client_id: clientId,
    grant_type: 'authorization_code',
    code,
    redirect_uri: redirectUri,
    code_verifier: codeVerifier,
  });

  const res = await fetch('https://accounts.spotify.com/api/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: params,
  });

  if (!res.ok) {
    const errText = await res.text();
    throw new Error(
      `Spotify 토큰 발급 실패 [${res.status}]: ${res.statusText} / ${errText}`
    );
  }
  return await res.json();
}
export async function fetchSpotifyProfile() {
  const { data } = await apiSpotify.get('/me');
  return data;
}
