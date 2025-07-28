interface Image {
  url: string;
  height: number;
  width: number;
}

export type Type = 'artist' | 'track';

export interface Artist {
  id: string;
  name: string;
  images: Image[];
  followers: { total: number };
  genres: string[];
  popularity: number;
  uri: string;
  type: 'artist';
  external_urls: {
    spotify: string;
  };
}

export interface Track {
  id: string;
  name: string;
  artists: Pick<Artist, 'id' | 'name' | 'uri'>[];
  duration_ms: number;
  is_playable: boolean;
  album: {
    images: Image[];
    name: string;
    release_date: string;
    total_tracks: number;
  };
  external_urls: {
    spotify: string;
  };
  popularity: number;
  type: 'track';
  track_number: number;
}
