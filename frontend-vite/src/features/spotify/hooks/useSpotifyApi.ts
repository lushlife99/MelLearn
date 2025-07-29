import { useMutation } from '@tanstack/react-query';
import {
  requestPausePlayback,
  requestResumePlayback,
  requestStartPlayback,
} from '../services/spotifyApi';
import toast from 'react-hot-toast';

export default function useSpotifyApi() {
  const { mutate: startPlayback } = useMutation({
    mutationFn: ({
      deviceId,
      trackId,
    }: {
      deviceId: string;
      trackId: string;
    }) => requestStartPlayback(deviceId, trackId),
    onSuccess: () => {},
    onError: () => {
      toast.error('재생에 실패했습니다.');
    },
  });

  const { mutate: resumePlayback } = useMutation({
    mutationFn: (deviceId: string) => requestResumePlayback(deviceId),
    onSuccess: () => {},
    onError: () => {
      toast.error('재생에 실패했습니다.');
    },
  });

  const { mutate: pausePlayback } = useMutation({
    mutationFn: (deviceId: string) => requestPausePlayback(deviceId),
    onSuccess: () => {},
    onError: () => {
      toast.error('일시정지가 실패했습니다.');
    },
  });
  return {
    startPlayback,
    resumePlayback,
    pausePlayback,
  };
}
