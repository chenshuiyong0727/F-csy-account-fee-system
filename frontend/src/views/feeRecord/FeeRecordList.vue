<template>
  <el-card class="page-card">
    <div class="toolbar">
      <el-input v-model="query.companyName" placeholder="公司名称" clearable style="width: 220px" @keyup.enter="load" />
      <el-date-picker v-model="receiveRange" type="daterange" range-separator="至" start-placeholder="收款开始" end-placeholder="收款结束" value-format="YYYY-MM-DD" />
      <el-input v-model="query.payerName" placeholder="付款人" clearable style="width: 140px" />
      <el-select v-model="query.payMethod" clearable placeholder="收款方式" style="width: 140px">
        <el-option label="微信" value="WECHAT" />
        <el-option label="支付宝" value="ALIPAY" />
        <el-option label="对公" value="BANK" />
        <el-option label="现金" value="CASH" />
        <el-option label="其他" value="OTHER" />
      </el-select>
      <el-input v-model="query.receiverName" placeholder="收款人" clearable style="width: 140px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="success" @click="openForm()">新增收款</el-button>
      <el-button @click="download">导出</el-button>
      <el-button type="warning" @click="downloadCashDetail">导出明细表</el-button>
    </div>
    <el-alert v-if="query.dateType === 'month'" title="当前筛选：本月收款记录" type="success" show-icon :closable="false" class="filter-alert" />
    <el-alert v-if="query.dateType === 'year'" title="当前筛选：本年收款记录" type="success" show-icon :closable="false" class="filter-alert" />
    <el-table :data="records" border>
      <el-table-column prop="companyName" label="公司名称" min-width="220" />
      <el-table-column prop="receiveDate" label="收款日期" width="120" />
      <el-table-column prop="payerName" label="付款人" width="120" />
      <el-table-column prop="payerContactName" label="付款联系人" width="120" />
      <el-table-column label="方式" width="100">
        <template #default="{ row }">{{ payMethodName(row.payMethod) }}</template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收款人" width="100" />
      <el-table-column label="金额" width="120">
        <template #default="{ row }"><span class="money">￥{{ Number(row.amount || 0).toFixed(2) }}</span></template>
      </el-table-column>
      <el-table-column prop="chargeEndDate" label="收费到日期" width="130" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="createBy" label="登记人" width="100" />
      <el-table-column prop="createTime" label="登记时间" width="170" />
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button link @click="openForm(row)">编辑</el-button>
          <el-popconfirm title="确认删除该收款记录？" @confirm="remove(row.id)">
            <template #reference><el-button link type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      class="pager"
      layout="total, sizes, prev, pager, next"
      :total="total"
      @current-change="load"
      @size-change="load"
    />
    <FeeRecordForm v-model="formVisible" :record="editing" @saved="load" />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import FeeRecordForm from "../../components/FeeRecordForm.vue";
import { deleteFeeRecord, exportCashDetail, exportFeeRecord, feeRecordPage } from "../../api/feeRecord";
import type { FeeRecord } from "../../types/common";

const route = useRoute();
const records = ref<FeeRecord[]>([]);
const total = ref(0);
const receiveRange = ref<string[]>([]);
const formVisible = ref(false);
const editing = ref<Partial<FeeRecord>>();
const query = reactive<any>({
  companyName: "",
  payerName: "",
  payMethod: "",
  receiverName: "",
  dateType: "",
  pageNum: 1,
  pageSize: 10
});

watch(receiveRange, (value) => {
  query.receiveDateStart = value?.[0] || "";
  query.receiveDateEnd = value?.[1] || "";
});

watch(
  () => route.query.dateType,
  (value) => {
    query.dateType = typeof value === "string" ? value : "";
    query.pageNum = 1;
    load();
  }
);

onMounted(() => {
  query.dateType = typeof route.query.dateType === "string" ? route.query.dateType : "";
  load();
});

async function load() {
  const data = await feeRecordPage(query);
  records.value = data.records;
  total.value = data.total;
}

function reset() {
  Object.assign(query, { companyName: "", payerName: "", payMethod: "", receiverName: "", dateType: "", pageNum: 1 });
  receiveRange.value = [];
  load();
}

function openForm(row?: FeeRecord) {
  editing.value = row ? { ...row } : undefined;
  formVisible.value = true;
}

async function remove(id: number) {
  await deleteFeeRecord(id);
  ElMessage.success("收款记录已删除");
  load();
}

async function download() {
  const response: any = await exportFeeRecord(query);
  downloadBlob(response.data, "收款记录.xlsx");
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

function payMethodName(value: string) {
  return ({ WECHAT: "微信", ALIPAY: "支付宝", BANK: "对公", CASH: "现金", OTHER: "其他" } as Record<string, string>)[value] || value;
}
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}

.filter-alert {
  margin-bottom: 12px;
}
</style>
