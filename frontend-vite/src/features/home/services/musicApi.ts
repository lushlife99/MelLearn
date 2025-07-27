import { apiSpotify } from '@/services/axios';
import axios from 'axios';
import type { Artist, Track } from '../types/home';

const TOTAL = 18;

export async function fetchTopCharts(
  lang = 'United%20States'
): Promise<Track[]> {
  const { data } = await axios.get(
    `https://ws.audioscrobbler.com/2.0/?method=geo.gettoptracks&country=${lang}&api_key=${
      import.meta.env.VITE_LASTFM_API_KEY
    }&format=json&limit=${TOTAL}`
  );

  const tracksData = data.tracks.track;
  if (!tracksData || tracksData.length === 0) {
    return [];
  }
  const results = await Promise.allSettled(
    tracksData.map(({ name }: { name: string }) =>
      apiSpotify
        .get('/search', { params: { q: name, type: 'track' } })
        .then((res) => res.data.tracks.items[0])
    )
  );

  const tracks: Track[] = results
    .filter(
      (r): r is PromiseFulfilledResult<Track | undefined> =>
        r.status === 'fulfilled' && !!r.value
    )
    .map((r) => r.value!)
    .filter((t): t is Track => !!t);

  return tracks;
}

export async function fetchTopArtists(): Promise<Artist[]> {
  const { data } = await axios.get(
    `https://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key=${
      import.meta.env.VITE_LASTFM_API_KEY
    }&format=json&limit=${TOTAL}`
  );

  const artistsData = data.artists.artist;

  const results = await Promise.allSettled(
    artistsData.map(({ name }: { name: string }) =>
      apiSpotify
        .get('/search', { params: { q: name, type: 'artist' } })
        .then((res) => res.data.artists.items[0])
    )
  );

  const artists: Artist[] = results
    .filter(
      (r): r is PromiseFulfilledResult<Artist | undefined> =>
        r.status === 'fulfilled' && !!r.value
    )
    .map((r) => r.value!)
    .filter((a): a is Artist => !!a);

  return artists;
}

export async function fetchRecommend() {}
