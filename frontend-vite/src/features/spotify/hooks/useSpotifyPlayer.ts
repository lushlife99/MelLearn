import { useEffect, useRef, useState } from 'react';
import useSpotifyApi from './useSpotifyApi';
import { useSpotifyStore } from '@/store/useSpotifyStore';

export default function useSpotifyPlayer() {
  const [currentTime, setCurrentTime] = useState(0);
  const [isReady, setIsReady] = useState(false);
  const currentTimeRef = useRef(0);

  const { startPlayback, resumePlayback, pausePlayback } = useSpotifyApi();

  const player = useSpotifyStore((state) => state.player);
  const deviceId = useSpotifyStore((state) => state.deviceId);
  const isStarted = useSpotifyStore((state) => state.isStarted);
  const currentTrackId = useSpotifyStore((state) => state.currentTrackId);
  const setIsPlaying = useSpotifyStore((state) => state.setIsPlaying);
  const setIsStarted = useSpotifyStore((state) => state.setIsStarted);
  const setIsPlayerOpen = useSpotifyStore((state) => state.setIsPlayerOpen);
  const setCurrentTrackId = useSpotifyStore((state) => state.setCurrentTrackId);

  useEffect(() => {
    // 가사 싱크 동기화
    const interval = setInterval(() => {
      player?.getCurrentState().then((state) => {
        if (!state) return;

        currentTimeRef.current = state.position / 1000;
        setCurrentTime(state.position);
        if (!isReady) setIsReady(true);
      });
    }, 200);

    return () => clearInterval(interval);
  }, [player, isReady]);

  useEffect(() => {
    if (!player) return;
    // 노래 재생 종료 후 상태 변경
    const onStateChange = (state: Spotify.PlaybackState | null) => {
      if (!state) return;
      if (
        state.paused &&
        state.position === 0 &&
        state.track_window.previous_tracks.length > 0
      ) {
        setIsPlaying(false);
      }
    };

    player.addListener('player_state_changed', onStateChange);

    return () => {
      player.removeListener('player_state_changed', onStateChange);
    };
  }, [player]);

  const play = (trackId: string) => {
    if (!deviceId) return;
    const isSameTrack = currentTrackId === trackId;

    if (isStarted && isSameTrack) {
      resumePlayback(deviceId, {
        onSuccess: () => {
          setIsPlaying(true);
          setIsPlayerOpen(true);
        },
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
      onSuccess: () => {
        setIsPlaying(false);
      },
    });
  };
  return { play, pause, currentTime, currentTimeRef, isReady };
}
