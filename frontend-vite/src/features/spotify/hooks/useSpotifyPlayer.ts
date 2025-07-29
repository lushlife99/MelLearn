import useSpotifyApi from './useSpotifyApi';
import { useSpotifyStore } from '@/store/useSpotifyStore';

export default function useSpotifyPlayer() {
  const { startPlayback, resumePlayback, pausePlayback } = useSpotifyApi();

  const deviceId = useSpotifyStore((state) => state.deviceId);
  const isStarted = useSpotifyStore((state) => state.isStarted);
  const currentTrackId = useSpotifyStore((state) => state.currentTrackId);
  const setIsPlaying = useSpotifyStore((state) => state.setIsPlaying);
  const setIsStarted = useSpotifyStore((state) => state.setIsStarted);
  const setIsPlayerOpen = useSpotifyStore((state) => state.setIsPlayerOpen);
  const setCurrentTrackId = useSpotifyStore((state) => state.setCurrentTrackId);

  const play = (trackId: string) => {
    if (!deviceId) return;
    const isSameTrack = currentTrackId === trackId;

    if (isStarted && isSameTrack) {
      resumePlayback(deviceId, {
        onSuccess: () => setIsPlaying(true),
      });
    } else {
      startPlayback(
        { deviceId, trackId },
        {
          onSuccess: () => {
            setIsStarted(true);
            setIsPlayerOpen(true);
            setIsPlaying(true);
            setCurrentTrackId(trackId);
          },
        }
      );
    }
  };
  const pause = () => {
    if (!deviceId) return;
    pausePlayback(deviceId, {
      onSuccess: () => setIsPlaying(false),
    });
  };
  return { play, pause };
}
