import axios, {
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";

const axiosApi = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
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

export default axiosApi;
