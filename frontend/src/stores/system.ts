import { defineStore } from "pinia";
import { getSystemConfig, type SystemConfig } from "../api/system";

const defaultConfig: SystemConfig = {
  systemName: "会计服务费收款登记系统",
  companyName: "轻舟财税服务中心",
  logoUrl: "/brand/logo.svg",
  loginBackgroundUrl: "/brand/login-bg.png",
  loginSlogan: "客户服务费收款、到期提醒与台账导出，一处登记，清楚可查。"
};

export const useSystemStore = defineStore("system", {
  state: () => ({
    config: { ...defaultConfig },
    loaded: false
  }),
  actions: {
    async loadConfig() {
      if (this.loaded) return;
      try {
        this.config = { ...defaultConfig, ...(await getSystemConfig()) };
      } catch {
        this.config = { ...defaultConfig };
      } finally {
        this.loaded = true;
      }
    }
  }
});
