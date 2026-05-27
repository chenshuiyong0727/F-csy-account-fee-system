import { defineStore } from "pinia";
import { currentUser, login, logout } from "../api/auth";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    realName: localStorage.getItem("realName") || "",
    roleCode: localStorage.getItem("roleCode") || ""
  }),
  actions: {
    async login(username: string, password: string) {
      const data = await login({ username, password });
      this.token = data.token;
      this.realName = data.realName;
      this.roleCode = data.roleCode;
      localStorage.setItem("token", data.token);
      localStorage.setItem("realName", data.realName || "");
      localStorage.setItem("roleCode", data.roleCode || "");
    },
    async loadCurrent() {
      const data = await currentUser();
      this.realName = data.realName;
      this.roleCode = data.roleCode;
      localStorage.setItem("realName", data.realName || "");
      localStorage.setItem("roleCode", data.roleCode || "");
    },
    async logout() {
      await logout().catch(() => undefined);
      this.token = "";
      this.realName = "";
      this.roleCode = "";
      localStorage.removeItem("token");
      localStorage.removeItem("realName");
      localStorage.removeItem("roleCode");
    }
  }
});
