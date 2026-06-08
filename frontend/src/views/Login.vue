<template>
  <div class="login-page" :style="pageStyle">
    <div class="login-shade"></div>

    <el-card class="login-card">
      <div class="login-brand">
        <img v-if="!logoError" :src="system.config.logoUrl" alt="logo" @error="logoError = true" />
        <div v-else class="logo-fallback">{{ firstChar }}</div>
        <div>
          <strong>{{ system.config.companyName }}</strong>
          <span>{{ system.config.systemName }}</span>
        </div>
      </div>

      <h1>欢迎登录</h1>
      <p>{{ system.config.loginSlogan }}</p>

      <el-form :model="form" class="login-form" @keyup.enter="submit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="账号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" placeholder="密码" type="password" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">登录系统</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/user";
import { useSystemStore } from "../stores/system";

const router = useRouter();
const user = useUserStore();
const system = useSystemStore();
const loading = ref(false);
const logoError = ref(false);
const form = reactive({ username: "admin", password: "123456" });

const pageStyle = computed(() => ({
  backgroundImage: `url("${system.config.loginBackgroundUrl}")`
}));

const firstChar = computed(() => system.config.companyName?.slice(0, 1) || "账");

watch(
  () => system.config.logoUrl,
  () => {
    logoError.value = false;
  }
);

onMounted(() => {
  system.loadConfig();
});

async function submit() {
  if (!form.username || !form.password) {
    ElMessage.warning("请输入账号和密码");
    return;
  }
  loading.value = true;
  try {
    await user.login(form.username, form.password);
    router.push("/dashboard");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 72px 10vw;
  box-sizing: border-box;
  background-size: cover;
  background-position: center;
  overflow: hidden;
}

.login-shade {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(246, 248, 251, 0.92) 0%, rgba(246, 248, 251, 0.76) 34%, rgba(246, 248, 251, 0.2) 74%, rgba(246, 248, 251, 0.06) 100%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.2), rgba(15, 23, 42, 0.12));
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  border: 0;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 22px 70px rgba(15, 23, 42, 0.14);
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 26px;
}

.login-brand img,
.logo-fallback {
  width: 54px;
  height: 54px;
  border-radius: 12px;
  object-fit: cover;
  flex: none;
}

.logo-fallback {
  display: grid;
  place-items: center;
  background: #0f766e;
  color: #fff;
  font-size: 24px;
  font-weight: 700;
}

.login-brand strong,
.login-brand span {
  display: block;
}

.login-brand strong {
  color: #111827;
  font-size: 20px;
}

.login-brand span {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

h1 {
  margin: 0 0 8px;
  color: #111827;
  font-size: 28px;
}

p {
  margin: 0 0 26px;
  color: #64748b;
  line-height: 1.7;
}

.login-form {
  margin-top: 4px;
}

.login-button {
  width: 100%;
  border: 0;
  background: #1677ff;
}

@media (max-width: 900px) {
  .login-page {
    justify-content: center;
    padding: 28px;
  }

  .login-card {
    width: min(420px, 100%);
  }
}
</style>
