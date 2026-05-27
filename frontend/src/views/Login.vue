<template>
  <div class="login-page" :style="pageStyle">
    <div class="login-overlay"></div>
    <section class="login-copy">
      <div class="copy-badge">本地部署 · Windows 开机自启</div>
      <h1>{{ system.config.systemName }}</h1>
      <p>{{ system.config.loginSlogan }}</p>
      <div class="copy-metrics">
        <span>客户公司</span>
        <span>多联系人</span>
        <span>到期提醒</span>
        <span>Excel 导出</span>
      </div>
    </section>

    <el-card class="login-card">
      <div class="login-brand">
        <img :src="system.config.logoUrl" alt="logo" />
        <div>
          <strong>{{ system.config.companyName }}</strong>
          <span>{{ system.config.systemName }}</span>
        </div>
      </div>
      <el-form :model="form" @keyup.enter="submit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="账号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" placeholder="密码" type="password" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">登录系统</el-button>
      </el-form>
      <div class="login-tip">默认账号：admin / 123456</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/user";
import { useSystemStore } from "../stores/system";

const router = useRouter();
const user = useUserStore();
const system = useSystemStore();
const loading = ref(false);
const form = reactive({ username: "admin", password: "123456" });

const pageStyle = computed(() => ({
  backgroundImage: `url("${system.config.loginBackgroundUrl}")`
}));

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
  display: grid;
  grid-template-columns: minmax(320px, 1fr) 460px;
  align-items: center;
  gap: 56px;
  padding: 56px 8vw;
  box-sizing: border-box;
  background-size: cover;
  background-position: center;
  overflow: hidden;
}

.login-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(8, 31, 38, 0.86) 0%, rgba(8, 31, 38, 0.62) 42%, rgba(255, 255, 255, 0.12) 100%),
    radial-gradient(circle at 18% 18%, rgba(20, 184, 166, 0.22), transparent 34%);
}

.login-copy,
.login-card {
  position: relative;
  z-index: 1;
}

.login-copy {
  max-width: 650px;
  color: #fff;
}

.copy-badge {
  display: inline-flex;
  padding: 8px 14px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  color: #ccfbf1;
  font-size: 14px;
}

.login-copy h1 {
  margin: 26px 0 18px;
  font-size: clamp(40px, 5vw, 68px);
  line-height: 1.05;
  letter-spacing: -1px;
}

.login-copy p {
  max-width: 560px;
  margin: 0;
  color: rgba(255, 255, 255, 0.78);
  font-size: 18px;
  line-height: 1.9;
}

.copy-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 34px;
}

.copy-metrics span {
  padding: 10px 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.14);
  color: #ecfeff;
}

.login-card {
  width: 100%;
  border: 0;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 30px 90px rgba(2, 6, 23, 0.28);
  backdrop-filter: blur(18px);
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}

.login-brand img {
  width: 58px;
  height: 58px;
  border-radius: 18px;
  object-fit: cover;
  box-shadow: 0 14px 30px rgba(15, 118, 110, 0.26);
}

.login-brand strong,
.login-brand span {
  display: block;
}

.login-brand strong {
  color: #0f172a;
  font-size: 22px;
}

.login-brand span {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.login-button {
  width: 100%;
  border: 0;
  background: linear-gradient(135deg, #0f766e, #155e75);
}

.login-tip {
  margin-top: 18px;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
    padding: 28px;
  }

  .login-copy {
    display: none;
  }
}
</style>
