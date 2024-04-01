/* 인기 가수  */
interface ArtistImg {
  avatar: {
    url: string;
    width: number | null;
    height: number | null;
  }[];
}

interface Artist {
  type: string;
  id: string;
  name: string;
  shareUrl: string;
  visuals: ArtistImg;
}
/* 인기 차트 */
interface ChartArtist {
  type: string;
  id: string;
  name: string;
  shareUrl: string;
}
interface Album {
  cover: {
    url: string;
    width: number | null;
    height: number | null;
  }[];
  type: string;
}
interface Track {
  album: Album;
  artists: ChartArtist[];
  id: string;
  name: string;
  shareUrl: string;
  type: string;
}

export interface ChartData {
  tracks: Track[];
}

export interface ArtistData {
  artists: Artist[];
}
