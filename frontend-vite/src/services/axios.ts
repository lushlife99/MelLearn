import axios, {
  AxiosError,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios';
import toast from 'react-hot-toast';

interface SpotifyApiError {
  error: {
    status: number;
    message: string;
    reason?: string;
  };
}

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // 배포 시: "https://mel-learn.store/"
  withCredentials: true,
});

export const apiSpotify = axios.create({
  baseURL: 'https://api.spotify.com/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const apiLrc = axios.create({
  baseURL: 'https://lrclib.net/api',
});

// apiLrc 인터셉터 만들기

const handleRequestInterceptor = (
  config: InternalAxiosRequestConfig
): InternalAxiosRequestConfig => {
  const token = sessionStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

const handleResponseInterceptor = async (
  error: AxiosError
): Promise<AxiosResponse> => {
  const status = error.response?.status;

  if (status === 401) {
    window.location.href = '/';
  } else if (status === 403) {
    const res = await apiClient.get('/reIssueJwt');
    const newAccessToken = res.data.accessToken;
    localStorage.setItem('accessToken', newAccessToken);
  } else if (status === 409) {
    window.history.back();
  }

  return Promise.reject(error);
};

const handleSpotifyRequestInterceptor = (
  config: InternalAxiosRequestConfig
): InternalAxiosRequestConfig => {
  const token = localStorage.getItem('spotify_access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

const handleSpotifyResponseInterceptor = async (
  error: AxiosError<SpotifyApiError>
): Promise<AxiosResponse> => {
  const status = error.response?.status;
  const message = error.response?.data?.error?.message;

  if (status === 401) {
    toast.error('Spotify 인증이 만료되었습니다. 다시 로그인해주세요.');
    // 나중에 토큰 재발급 로직 구현
    window.location.href = '/login';
  } else if (status === 403) {
    if (message === 'Player command failed: Restriction violated') {
      toast.error('성인 인증이 필요한 곡입니다.');
    } else {
      toast.error('재생 권한이 없는 콘텐츠입니다.');
    }
    console.error(error);

    //window.location.href = '/';
  } else if (status === 404) {
    //alert('연결된 장치가 존재하지 않습니다. 다시 로그인 해주세요.');
    //window.location.href = '/';
  }

  return Promise.reject(error);
};

apiClient.interceptors.request.use(handleRequestInterceptor, Promise.reject);
apiClient.interceptors.response.use((res) => res, handleResponseInterceptor);

apiSpotify.interceptors.request.use(
  handleSpotifyRequestInterceptor,
  Promise.reject
);
apiSpotify.interceptors.response.use(
  (res) => res,
  handleSpotifyResponseInterceptor
);

/* ===== 🪄 (옵션) Spotify Scraper 예외 처리 예시 ===== */
// const handleSpotifyScraperResponseInterceptor = async (error: AxiosError) => {
//   const status = error.response?.status;
//   if (status === 401 || status === 404) {
//     alert('가사를 지원하지 않거나 권한이 없습니다.');
//     window.location.href = '/';
//     return new Promise(() => {});
//   }
//   return Promise.reject(error);
// };
