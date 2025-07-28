import { useMutation } from '@tanstack/react-query';
import {
  pausePlayback,
  resumePlayback,
  startPlayback,
} from '../services/spotifyApi';
import toast from 'react-hot-toast';

export default function useSpotifyPlayer() {
  const { mutate: play } = useMutation({
    mutationFn: ({
      deviceId,
      trackId,
    }: {
      deviceId: string;
      trackId: string;
    }) => startPlayback(deviceId, trackId),
    onSuccess: () => {},
    onError: () => {
      toast.error('재생에 실패했습니다.');
    },
  });

  const { mutate: resume } = useMutation({
    mutationFn: (deviceId: string) => resumePlayback(deviceId),
    onSuccess: () => {},
    onError: () => {
      toast.error('재생에 실패했습니다.');
    },
  });

  const { mutate: pause } = useMutation({
    mutationFn: (deviceId: string) => pausePlayback(deviceId),
    onSuccess: () => {},
    onError: () => {
      toast.error('일시정지가 실패했습니다.');
    },
  });
  return {
    play,
    resume,
    pause,
  };
}
