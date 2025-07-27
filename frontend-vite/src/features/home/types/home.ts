interface Image {
  url: string;
  height: number;
  width: number;
}

export interface Artist {
  id: string;
  name: string;
  images: Image[];
  followers: { total: number };
  genres: string[];
  popularity: number;
  uri: string;
  type: 'artist';
}

export interface Track {
  id: string;
  name: string;
  artists: Pick<Artist, 'id' | 'name' | 'uri'>[];
  duration_ms: number;
  is_playable: boolean;
  album: {
    images: Image[];
  };
  popularity: number;
  type: 'track';
}
