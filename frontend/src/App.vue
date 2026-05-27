<template>
  <router-view v-if="$route.path === '/login'" />
  <el-container v-else class="app-shell">
    <el-aside width="220px" class="app-aside">
      <div class="brand">
        <strong>会计服务费</strong>
        <span>收款登记系统</span>
      </div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/dashboard">首页统计</el-menu-item>
        <el-menu-item index="/customer">客户公司</el-menu-item>
        <el-menu-item index="/fee-record">收款记录</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <span>本地台账，简单好查，稳定可用</span>
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
import { useRouter } from "vue-router";
import { useUserStore } from "./stores/user";

const router = useRouter();
const user = useUserStore();

async function handleLogout() {
  await user.logout();
  router.push("/login");
}
</script>
