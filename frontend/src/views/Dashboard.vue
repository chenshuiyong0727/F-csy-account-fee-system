<template>
  <div>
    <div class="dashboard-title">
      <div>
        <h2>首页统计</h2>
        <p>点击卡片可直接下钻到对应客户或收款记录。</p>
      </div>
      <el-button type="primary" @click="router.push('/fee-record')">查看收款记录</el-button>
    </div>
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.title" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="stat-card" :class="card.tone" @click="card.onClick">
          <span>{{ card.title }}</span>
          <strong>{{ card.value }}</strong>
          <em>{{ card.desc }}</em>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { getDashboardSummary, type DashboardSummary } from "../api/dashboard";

const router = useRouter();
const summary = ref<DashboardSummary>({
  customerTotal: 0,
  normalCount: 0,
  warning60Count: 0,
  warning30Count: 0,
  warning5Count: 0,
  expiredCount: 0,
  noRecordCount: 0,
  monthAmount: 0,
  monthCount: 0,
  yearAmount: 0,
  yearCount: 0
});

const money = (value: number) => `￥${Number(value || 0).toFixed(2)}`;
const goCustomer = (expireStatus?: string) => router.push({ path: "/customer", query: expireStatus ? { expireStatus } : {} });
const goFeeRecord = (dateType: string) => router.push({ path: "/fee-record", query: { dateType } });

const cards = computed(() => [
  { title: "客户总数", value: summary.value.customerTotal, desc: "全部正常客户", tone: "blue", onClick: () => goCustomer() },
  { title: "正常客户", value: summary.value.normalCount, desc: "收费到期超过60天", tone: "green", onClick: () => goCustomer("NORMAL") },
  { title: "60天内到期", value: summary.value.warning60Count, desc: "31到60天", tone: "yellow", onClick: () => goCustomer("WARNING_60") },
  { title: "30天内到期", value: summary.value.warning30Count, desc: "6到30天", tone: "red", onClick: () => goCustomer("WARNING_30") },
  { title: "5天内到期", value: summary.value.warning5Count, desc: "0到5天", tone: "purple", onClick: () => goCustomer("WARNING_5") },
  { title: "已过期/欠费", value: summary.value.expiredCount, desc: "剩余天数小于0", tone: "dark", onClick: () => goCustomer("EXPIRED") },
  { title: "无收费记录", value: summary.value.noRecordCount, desc: "待补登记", tone: "slate", onClick: () => goCustomer("NO_RECORD") },
  { title: "本月收款金额", value: money(summary.value.monthAmount), desc: `${summary.value.monthCount} 笔`, tone: "money", onClick: () => goFeeRecord("month") },
  { title: "本年收款金额", value: money(summary.value.yearAmount), desc: `${summary.value.yearCount} 笔`, tone: "money", onClick: () => goFeeRecord("year") }
]);

onMounted(async () => {
  summary.value = await getDashboardSummary();
});
</script>

<style scoped>
.dashboard-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

h2 {
  margin: 0;
}

p {
  margin: 6px 0 0;
  color: #64748b;
}

.stat-card {
  margin-bottom: 16px;
  border: 0;
  border-radius: 18px;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.stat-card span,
.stat-card em {
  display: block;
  color: #64748b;
  font-style: normal;
}

.stat-card strong {
  display: block;
  margin: 12px 0 8px;
  font-size: 30px;
}

.blue { background: #eff6ff; }
.green { background: #ecfdf5; }
.yellow { background: #fffbeb; }
.red { background: #fef2f2; }
.purple { background: #f5f3ff; }
.dark { background: #f1f5f9; }
.slate { background: #f8fafc; }
.money { background: #f0fdfa; }
</style>
