<template>
  <router-view v-if="$route.path === '/login'" />
  <el-container v-else class="app-shell">
    <el-aside width="236px" class="app-aside">
      <div class="brand">
        <img :src="system.config.logoUrl" alt="logo" />
        <div>
          <strong>{{ system.config.companyName }}</strong>
          <span>{{ system.config.systemName }}</span>
        </div>
      </div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/dashboard">首页统计</el-menu-item>
        <el-menu-item index="/customer">客户公司</el-menu-item>
        <el-menu-item index="/fee-record">收款记录</el-menu-item>
        <el-menu-item index="/cash-detail">现金收支明细表</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <span>{{ system.config.loginSlogan }}</span>
        <div>
          <span class="user-name">{{ user.realName || "管理员" }}</span>
          <el-button link @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "./stores/user";
import { useSystemStore } from "./stores/system";

const router = useRouter();
const user = useUserStore();
const system = useSystemStore();

onMounted(() => {
  system.loadConfig();
});

async function handleLogout() {
  await user.logout();
  router.push("/login");
}
</script>
