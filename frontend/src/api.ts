import axios, {
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";

const axiosApi = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

/* Spotify API 호출용 */
export const axiosSpotify = axios.create({
  baseURL: "https://spotify-scraper.p.rapidapi.com/v1",
  withCredentials: true,
  headers: {
    "X-RapidAPI-Key": "7255ad630cmshdf9fda3f9dea3b0p1e9379jsn615bba3b42d1",
    "X-RapidAPI-Host": "spotify-scraper.p.rapidapi.com",
  },
});

const handleRequestInterceptor = (
  config: InternalAxiosRequestConfig
): InternalAxiosRequestConfig => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

const handleResponseInterceptor = async (
  error: AxiosError
): Promise<AxiosResponse> => {
  if (error.response?.status === 401) {
    window.location.href = "/"; //로그인 창으로 리다이렉션
  } else if (error.response?.status === 403) {
    const res = await axiosApi.get("/reIssueJwt"); //토큰 재발급
    const accessToken = res.data.accessToken;

    localStorage.setItem("accessToken", accessToken);
  } else if (error.response?.status === 404) {
    console.error("404err");
  } else {
    console.error("errrsss");
  }
  return Promise.reject(error.toJSON());
};
axiosApi.interceptors.request.use(
  handleRequestInterceptor,
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);
axiosApi.interceptors.response.use(
  (response) => response,
  handleResponseInterceptor
);

/* 특정 아티스트 정보 */

export default axiosApi;