import axios, {
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";

const axiosApi = axios.create({
  baseURL: "localhost:8080",
  withCredentials: true,
});

const handleRequestInterceptor = (
  config: InternalAxiosRequestConfig
): InternalAxiosRequestConfig => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

const handleResponseInterceptor = (
  error: AxiosError
): Promise<AxiosResponse> => {
  if (error.response?.status === 401) {
    window.location.href = "/login";
  } else if (error.response?.status === 403) {
  } else if (error.response?.status === 404) {
    console.error("404err");
  } else {
    console.error("errr");
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
