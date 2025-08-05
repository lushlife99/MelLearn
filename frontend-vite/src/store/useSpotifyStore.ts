import { create } from 'zustand';

interface SpotifyState {
  deviceId: string | null;
  setDeviceId: (id: string) => void;

  isPlayerOpen: boolean;
  setIsPlayerOpen: (isPlayerOpen: boolean) => void;

  isPlaying: boolean;
  setIsPlaying: (isPlaying: boolean) => void;

  isStarted: boolean;
  setIsStarted: (isStart: boolean) => void;

  currentTrackId: string | null;
  setCurrentTrackId: (id: string) => void;

  player: Spotify.Player | null;
  setPlayer: (player: Spotify.Player) => void;
}

export const useSpotifyStore = create<SpotifyState>((set) => ({
  deviceId: null,
  isPlayerOpen: false,
  isPlaying: false,
  isStarted: false,
  currentTrackId: null,
  setDeviceId: (deviceId) => set({ deviceId }),
  setIsPlayerOpen: (isPlayerOpen) => set({ isPlayerOpen }),
  setIsPlaying: (isPlaying) => set({ isPlaying }),
  setIsStarted: (isStarted) => set({ isStarted }),
  setCurrentTrackId: (currentTrackId) => set({ currentTrackId }),
  player: null,
  setPlayer: (player) => set({ player }),
}));
