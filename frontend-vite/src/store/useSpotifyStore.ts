import { create } from 'zustand';

interface SpotifyState {
  deviceId: string | null;
  setDeviceId: (id: string) => void;
}

export const useSpotifyStore = create<SpotifyState>((set) => ({
  deviceId: null,
  setDeviceId: (deviceId) => set({ deviceId }),
}));
