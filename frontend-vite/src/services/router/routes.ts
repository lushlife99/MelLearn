export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  SIGNUP: '/signup',

  SPOTIFY_CALLBACK: '/callback',
  SPOTIFY_LOGIN: '/spotify-login',

  ARTISTS: '/artists',
  TRACKS: '/tracks',

  ARTIST_DETAIL: (id: string) => `/artists/${id}`,
  TRACK_DETAIL: (id: string) => `/tracks/${id}`,
};
