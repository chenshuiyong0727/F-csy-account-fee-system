import axios from "axios";
import { ElMessage } from "element-plus";

export interface ApiResult<T> {
  code: number;
  msg: string;
  data: T;
}

const request = axios.create({
  baseURL: "/api",
  timeout: 15000
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

(request.interceptors.response.use as any)(
  (response: any) => {
    const data = response.data as ApiResult<unknown>;
    if (response.config.responseType === "blob") {
      return response;
    }
    if (data.code !== 200) {
      ElMessage.error(data.msg || "请求失败");
      if (data.code === 401) {
        localStorage.removeItem("token");
        window.location.href = "/login";
      }
      return Promise.reject(new Error(data.msg));
    }
    return data.data;
  },
  (error: any) => {
    ElMessage.error(error.response?.data?.msg || "网络请求失败");
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default request;
