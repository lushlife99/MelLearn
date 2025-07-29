import { useSpotifyStore } from '@/store/useSpotifyStore';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchPlaybackState } from '../services/spotifyApi';

declare global {
  interface Window {
    __SPOTIFY_PLAYER_INITIALIZED__?: boolean;
  }
}

export default function useSpotifySDKSetup(
  token: string | null,
  enabled = true
) {
  const navigate = useNavigate();
  const setDeviceId = useSpotifyStore((state) => state.setDeviceId);
  const setPlayer = useSpotifyStore((state) => state.setPlayer);

  useEffect(() => {
    if (!token || !enabled) return;

    const initSpotifyPlayer = async () => {
      try {
        await loadSpotifySDK();

        const runOnce = () => {
          if (window.__SPOTIFY_PLAYER_INITIALIZED__) return;

          window.__SPOTIFY_PLAYER_INITIALIZED__ = true;
          initializePlayer(token);
        };

        if (window.Spotify) {
          runOnce();
        } else {
          window.onSpotifyWebPlaybackSDKReady = runOnce;
        }
      } catch (err) {
        console.error('Spotify SDK 초기화 실패:', err);
        navigate('/');
      }
    };

    initSpotifyPlayer();
  }, [token, enabled, navigate, setDeviceId]);

  const loadSpotifySDK = (): Promise<void> => {
    return new Promise((resolve, reject) => {
      if (window.Spotify) {
        resolve();
        return;
      }

      if (document.querySelector('#spotify-sdk')) {
        const checkLoaded = setInterval(() => {
          if (window.Spotify) {
            clearInterval(checkLoaded);
            resolve();
          }
        }, 100);

        setTimeout(() => {
          clearInterval(checkLoaded);
          reject(new Error('SDK 로딩 타임아웃'));
        }, 10000);
        return;
      }

      const script = document.createElement('script');
      script.id = 'spotify-sdk';
      script.src = 'https://sdk.scdn.co/spotify-player.js';
      script.async = true;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('SDK 로딩 실패'));
      document.body.appendChild(script);
    });
  };

  const initializePlayer = (token: string) => {
    const playerInstance = new window.Spotify.Player({
      name: 'MelLearn',
      getOAuthToken: (cb) => cb(token),
      volume: 0.5,
    });
    setPlayer(playerInstance);

    playerInstance.addListener('ready', ({ device_id }) => {
      console.log('🎧 Spotify 플레이어 준비 완료:', device_id);
      setDeviceId(device_id);
      transferDeviceAndLink(device_id);
    });

    playerInstance.addListener('account_error', ({ message }) =>
      console.error('계정 에러:', message)
    );
    playerInstance.addListener('initialization_error', ({ message }) =>
      console.error('초기화 에러:', message)
    );
    playerInstance.addListener('authentication_error', ({ message }) =>
      console.error('인증 에러:', message)
    );
    playerInstance.addListener('not_ready', ({ device_id }) =>
      console.warn('디바이스 연결 해제됨:', device_id)
    );
    playerInstance.addListener('autoplay_failed', () =>
      console.warn('iOS 자동재생 실패')
    );

    playerInstance.connect().then((success) => {
      if (success) {
        console.log('✅ Spotify 플레이어 연결 성공');
      } else {
        console.error('❌ Spotify 플레이어 연결 실패');
      }
    });
  };

  const transferDeviceAndLink = async (deviceId: string) => {
    try {
      await fetchPlaybackState(deviceId);

      console.log('📡 Spotify 디바이스 연동 성공');
    } catch (err) {
      console.error('❌ 디바이스 연동 실패:', err);
      navigate('/');
    }
  };
}
