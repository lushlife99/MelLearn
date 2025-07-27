// // types/spotify.d.ts 파일 생성
// declare global {
//   interface Window {
//     onSpotifyWebPlaybackSDKReady: () => void;
//     Spotify: {
//       Player: new (options: {
//         name: string;
//         getOAuthToken: (cb: (token: string) => void) => void;
//         volume?: number;
//       }) => SpotifyPlayer;
//     };
//   }
// }

// interface SpotifyPlayer {
//   addListener(
//     event: 'ready' | 'not_ready',
//     listener: (data: { device_id: string }) => void
//   ): void;
//   addListener(
//     event: 'initialization_error' | 'authentication_error' | 'account_error',
//     listener: (data: { message: string }) => void
//   ): void;
//   addListener(event: 'autoplay_failed', listener: () => void): void;
//   addListener(
//     event: 'player_state_changed',
//     listener: (state: SpotifyPlayerState | null) => void
//   ): void;
//   connect(): Promise<boolean>;
//   disconnect(): void;
//   getCurrentState(): Promise<SpotifyPlayerState | null>;
//   getVolume(): Promise<number>;
//   nextTrack(): Promise<void>;
//   pause(): Promise<void>;
//   previousTrack(): Promise<void>;
//   resume(): Promise<void>;
//   seek(position_ms: number): Promise<void>;
//   setVolume(volume: number): Promise<void>;
//   togglePlay(): Promise<void>;
// }

// interface SpotifyPlayerState {
//   context: {
//     uri: string;
//     metadata: any;
//   };
//   disallows: {
//     pausing: boolean;
//     peeking_next: boolean;
//     peeking_prev: boolean;
//     resuming: boolean;
//     seeking: boolean;
//     skipping_next: boolean;
//     skipping_prev: boolean;
//   };
//   paused: boolean;
//   position: number;
//   repeat_mode: number;
//   shuffle: boolean;
//   track_window: {
//     current_track: SpotifyTrack;
//     next_tracks: SpotifyTrack[];
//     previous_tracks: SpotifyTrack[];
//   };
// }

// interface SpotifyTrack {
//   id: string;
//   uri: string;
//   name: string;
//   artists: Array<{ name: string; uri: string }>;
//   album: {
//     name: string;
//     uri: string;
//     images: Array<{ url: string; height: number; width: number }>;
//   };
//   duration_ms: number;
// }

// export {};
