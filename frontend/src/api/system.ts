import request from "../utils/request";

export interface SystemConfig {
  systemName: string;
  companyName: string;
  logoUrl: string;
  loginBackgroundUrl: string;
  loginSlogan: string;
}

export function getSystemConfig() {
  return request.get<unknown, SystemConfig>("/system/config");
}
