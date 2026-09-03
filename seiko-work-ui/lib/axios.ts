import axios, { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from "axios";

export interface Result<T = unknown> {
  code: number;
  message: string;
  data: T;
}

const ERROR_CODE_MAP: Record<number, string> = {
  400: "请求参数错误",
  401: "登录已过期，请重新登录",
  403: "没有权限访问",
  404: "请求的资源不存在",
  405: "请求方法不允许",
  409: "资源冲突",
  429: "请求过于频繁",
  500: "服务器内部错误",
  502: "网关错误",
  503: "服务暂不可用",
};

const BUSINESS_CODE_MAP: Record<number, string> = {
  1001: "用户名或密码错误",
  1002: "登录已过期，请重新登录",
  1003: "用户已被禁用",
  1004: "用户名已存在",
  1005: "原密码错误",
  1006: "验证码发送失败，请稍后重试",
  1007: "验证码错误或已过期",
  1008: "验证码发送过于频繁，请稍后再试",
  2001: "文件上传失败",
  2002: "文件类型不允许",
  2003: "文件大小超出限制",
};

export const http: AxiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:1001",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (typeof window !== "undefined") {
      const token = localStorage.getItem("token");
      if (token && config.headers) {
        config.headers.set("token", token);
      }
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

function redirectToLogin() {
  if (typeof window === "undefined") return;
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  if (window.location.hash !== "#login") {
    window.location.hash = "#login";
  }
}

http.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const { data } = response;
    if (data.code !== 200) {
      if (data.code === 1002) redirectToLogin();
      const message = BUSINESS_CODE_MAP[data.code] || data.message || "业务处理失败";
      return Promise.reject(new Error(message));
    }
    return response;
  },
  (error: AxiosError<Result>) => {
    const status = error.response?.status;
    const message =
      error.response?.data?.message ||
      (status ? ERROR_CODE_MAP[status] : undefined) ||
      error.message ||
      "网络请求异常";

    if (status === 401 || error.response?.data?.code === 1002) {
      redirectToLogin();
    }

    return Promise.reject(new Error(message));
  }
);

export async function request<T>(config: Parameters<typeof http.request>[0]): Promise<T> {
  const response = await http.request<Result<T>>(config);
  return response.data.data;
}

export default http;
