<template>
  <el-card class="page-card">
    <div class="cash-header">
      <div>
        <h2>现金收支明细表</h2>
        <p>按收款记录生成账本视图，导出格式与页面保持一致。</p>
      </div>
      <el-button type="warning" @click="downloadCashDetail">导出明细表</el-button>
    </div>

    <div class="toolbar">
      <el-input v-model="query.companyName" placeholder="公司名称" clearable style="width: 220px" @keyup.enter="load" />
      <el-date-picker v-model="receiveRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
      <el-input v-model="query.receiverName" placeholder="经办人/收款人" clearable style="width: 160px" />
      <el-input-number v-model="query.openingBalance" :precision="2" placeholder="上月余额" style="width: 160px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="reset">重置</el-button>
    </div>

    <div class="ledger-wrap">
      <table class="ledger-table">
        <thead>
          <tr>
            <th colspan="8" class="title">现金收支明细表</th>
          </tr>
          <tr class="meta">
            <td>编制部门：</td>
            <td></td>
            <td>经办人：{{ realName }}</td>
            <td></td>
            <td></td>
            <td>日期：</td>
            <td colspan="2">{{ dateRangeText }}</td>
          </tr>
          <tr>
            <th>日期</th>
            <th>编号</th>
            <th>摘要</th>
            <th>上月余额</th>
            <th>收入</th>
            <th>支出</th>
            <th>余额</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td></td>
            <td></td>
            <td>上月余额</td>
            <td></td>
            <td></td>
            <td></td>
            <td class="amount">{{ money(openingBalance) }}</td>
            <td></td>
          </tr>
          <tr v-for="row in ledgerRows" :key="row.id">
            <td>{{ formatDate(row.receiveDate) }}</td>
            <td>{{ row.receiverName }}</td>
            <td class="summary">{{ summary(row) }}</td>
            <td></td>
            <td class="amount">{{ money(row.amount) }}</td>
            <td></td>
            <td class="amount">{{ money(row.balance) }}</td>
            <td>{{ row.remark }}</td>
          </tr>
          <tr v-if="ledgerRows.length === 0">
            <td colspan="8" class="empty">暂无明细</td>
          </tr>
          <tr class="bank-row">
            <td></td>
            <td></td>
            <td colspan="6">银行账号：</td>
          </tr>
        </tbody>
      </table>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { exportCashDetail, feeRecordPage } from "../../api/feeRecord";
import type { FeeRecord } from "../../types/common";

type LedgerRow = FeeRecord & { balance: number };

const records = ref<FeeRecord[]>([]);
const receiveRange = ref<string[]>([]);
const query = reactive<any>({
  companyName: "",
  receiverName: "",
  openingBalance: 0,
  pageNum: 1,
  pageSize: 500
});

const realName = computed(() => localStorage.getItem("realName") || "管理员");
const openingBalance = computed(() => Number(query.openingBalance || 0));

const ledgerRows = computed<LedgerRow[]>(() => {
  let balance = openingBalance.value;
  return records.value.map((record) => {
    balance += Number(record.amount || 0);
    return { ...record, balance };
  });
});

const dateRangeText = computed(() => {
  if (query.receiveDateStart && query.receiveDateEnd) {
    return `${query.receiveDateStart} - ${query.receiveDateEnd}`;
  }
  return "";
});

watch(receiveRange, (value) => {
  query.receiveDateStart = value?.[0] || "";
  query.receiveDateEnd = value?.[1] || "";
});

onMounted(load);

async function load() {
  const data = await feeRecordPage(query);
  records.value = data.records;
}

function reset() {
  Object.assign(query, {
    companyName: "",
    receiverName: "",
    openingBalance: 0,
    receiveDateStart: "",
    receiveDateEnd: "",
    pageNum: 1,
    pageSize: 500
  });
  receiveRange.value = [];
  load();
}

async function downloadCashDetail() {
  const response: any = await exportCashDetail(query);
  downloadBlob(response.data, "现金收支明细表.xlsx");
}

function downloadBlob(blob: Blob, fileName: string) {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  link.click();
  window.URL.revokeObjectURL(url);
}

function money(value?: number) {
  return Number(value || 0).toFixed(2);
}

function formatDate(value?: string) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return `${date.getMonth() + 1}月${date.getDate()}日`;
}

function summary(row: FeeRecord) {
  const parts = [`收款${row.companyName}`];
  if (row.payerName) parts.push(row.payerName);
  if (row.chargeEndDate) parts.push(`会计服务费收费到${row.chargeEndDate}`);
  return parts.join(" ");
}
</script>

<style scoped>
.cash-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.cash-header h2 {
  margin: 0;
}

.cash-header p {
  margin: 6px 0 0;
  color: #64748b;
}

.ledger-wrap {
  overflow: auto;
  background: #fff;
}

.ledger-table {
  min-width: 1180px;
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  font-size: 13px;
  color: #111827;
}

.ledger-table th,
.ledger-table td {
  border: 1px solid #1f2937;
  padding: 6px 8px;
  height: 28px;
  vertical-align: middle;
}

.ledger-table th:nth-child(1),
.ledger-table td:nth-child(1) {
  width: 90px;
  text-align: center;
}

.ledger-table th:nth-child(2),
.ledger-table td:nth-child(2) {
  width: 90px;
  text-align: center;
}

.ledger-table th:nth-child(3),
.ledger-table td:nth-child(3) {
  width: 520px;
}

.ledger-table th:nth-child(4),
.ledger-table td:nth-child(4),
.ledger-table th:nth-child(5),
.ledger-table td:nth-child(5),
.ledger-table th:nth-child(6),
.ledger-table td:nth-child(6),
.ledger-table th:nth-child(7),
.ledger-table td:nth-child(7) {
  width: 110px;
}

.ledger-table th:nth-child(8),
.ledger-table td:nth-child(8) {
  width: 180px;
}

.title {
  font-size: 22px;
  letter-spacing: 2px;
  text-align: center;
  height: 36px;
}

.meta td {
  border-color: #cbd5e1;
}

.summary {
  text-align: left;
  white-space: normal;
  word-break: break-all;
}

.amount {
  text-align: right;
  font-family: Consolas, "Microsoft YaHei", sans-serif;
}

.empty {
  color: #94a3b8;
  text-align: center;
}

.bank-row td {
  height: 42px;
}
</style>
