import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function useSpotifyPlayer(token: string | null) {
  const navigate = useNavigate();
  const playerRef = useRef<Spotify.Player | null>(null); // 내부 제어용
  const [player, setPlayer] = useState<Spotify.Player | null>(null); // 렌더링용
  const isInitializedRef = useRef(false); // 중복 초기화 방지

  useEffect(() => {
    if (!token || isInitializedRef.current) return;
    isInitializedRef.current = true;

    const initSpotifyPlayer = async () => {
      try {
        await loadSpotifySDK();

        if (window.Spotify) {
          initializePlayer(token);
        } else {
          window.onSpotifyWebPlaybackSDKReady = () => {
            initializePlayer(token);
          };
        }
      } catch (err) {
        console.error('Spotify SDK 초기화 실패:', err);
        navigate('/');
      }
    };

    initSpotifyPlayer();

    return () => {
      if (playerRef.current) {
        playerRef.current.disconnect();
        playerRef.current = null;
        setPlayer(null);
      }
    };
  }, [token, navigate]);

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
    const player = new window.Spotify.Player({
      name: 'MelLearn',
      getOAuthToken: (cb) => cb(token),
      volume: 0.5,
    });

    playerRef.current = player;
    setPlayer(player); // 상태 업데이트

    player.addListener('ready', ({ device_id }) => {
      console.log('Spotify 플레이어 준비 완료:', device_id);
      localStorage.setItem('deviceId', device_id);
      transferDeviceAndLink(device_id, token);
    });

    player.addListener('account_error', ({ message }) =>
      console.error('계정 에러:', message)
    );
    player.addListener('initialization_error', ({ message }) =>
      console.error('초기화 에러:', message)
    );
    player.addListener('authentication_error', ({ message }) =>
      console.error('인증 에러:', message)
    );
    player.addListener('not_ready', ({ device_id }) =>
      console.warn('디바이스 연결 해제됨:', device_id)
    );
    player.addListener('autoplay_failed', () =>
      console.warn('iOS 자동재생 실패')
    );

    player.connect().then((success) => {
      if (success) {
        console.log('Spotify 플레이어 연결 성공');
      } else {
        console.error('Spotify 플레이어 연결 실패');
      }
    });
  };

  const transferDeviceAndLink = async (deviceId: string, token: string) => {
    try {
      // await apiSpotify.put('/me/player', {
      //   device_ids: [deviceId],
      //   play: false,
      // });

      console.log('Spotify 디바이스 연동 성공');
      navigate('/');
    } catch (err) {
      console.error('디바이스 연동 실패:', err);
      navigate('/');
    }
  };

  return {
    player,
  };
}
