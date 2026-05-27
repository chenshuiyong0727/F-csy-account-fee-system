import request from "../utils/request";

export function login(data: { username: string; password: string }) {
  return request.post<unknown, { token: string; realName: string; roleCode: string }>("/auth/login", data);
}

export function currentUser() {
  return request.get<unknown, { username: string; realName: string; roleCode: string }>("/auth/current");
}

export function logout() {
  return request.post("/auth/logout");
}
