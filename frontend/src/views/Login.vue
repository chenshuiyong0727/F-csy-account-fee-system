<template>
  <div class="login-page">
    <el-card class="login-card">
      <h1>会计服务费收款登记系统</h1>
      <p>客户公司、联系人、收款记录和到期提醒统一登记。</p>
      <el-form :model="form" @keyup.enter="submit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="账号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" placeholder="密码" type="password" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <div class="login-tip">默认账号：admin / 123456</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/user";

const router = useRouter();
const user = useUserStore();
const loading = ref(false);
const form = reactive({ username: "admin", password: "123456" });

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
  min-height: 100vh;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 20% 20%, rgba(14, 165, 233, 0.25), transparent 32%),
    linear-gradient(135deg, #eef8ff 0%, #f8fafc 55%, #ecfdf5 100%);
}

.login-card {
  width: 420px;
  border: 0;
  border-radius: 24px;
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.12);
}

h1 {
  margin: 0 0 10px;
  font-size: 26px;
}

p {
  margin: 0 0 28px;
  color: #64748b;
}

.login-tip {
  margin-top: 18px;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}
</style>
