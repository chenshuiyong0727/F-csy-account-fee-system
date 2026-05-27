<template>
  <el-card class="page-card">
    <div class="toolbar">
      <el-input v-model="query.companyName" placeholder="公司名称" clearable style="width: 240px" @keyup.enter="load" />
      <el-select v-model="query.expireStatus" clearable placeholder="到期状态" style="width: 180px">
        <el-option label="正常" value="NORMAL" />
        <el-option label="60天内到期" value="WARNING_60" />
        <el-option label="30天内到期" value="WARNING_30" />
        <el-option label="5天内到期" value="WARNING_5" />
        <el-option label="已过期/欠费" value="EXPIRED" />
        <el-option label="无收费记录" value="NO_RECORD" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="success" @click="openCustomer()">新增客户</el-button>
      <el-button @click="downloadExpire">导出到期列表</el-button>
    </div>
    <el-table :data="records" border>
      <el-table-column prop="companyName" label="公司名称" min-width="220" />
      <el-table-column prop="latestChargeEndDate" label="最新收费到日期" width="140" />
      <el-table-column prop="remainDays" label="剩余天数" width="100" />
      <el-table-column label="到期状态" width="130">
        <template #default="{ row }"><ExpireStatusTag :status="row.expireStatus" /></template>
      </el-table-column>
      <el-table-column prop="primaryContactName" label="主要联系人" width="120" />
      <el-table-column prop="primaryContactPhone" label="联系电话" width="140" />
      <el-table-column prop="primaryContactWechat" label="微信号" width="140" />
      <el-table-column prop="latestReceiveDate" label="最近收款日期" width="130" />
      <el-table-column label="操作" width="270" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/customer/detail/${row.customerId}`)">详情</el-button>
          <el-button link type="success" @click="openFee(row.customerId)">新增收款</el-button>
          <el-button link @click="openCustomer(row)">编辑</el-button>
          <el-popconfirm title="确认删除该客户？" @confirm="remove(row.customerId)">
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
  </el-card>

  <el-dialog v-model="customerVisible" :title="customerForm.id ? '编辑客户' : '新增客户'" width="620px">
    <el-form :model="customerForm" label-width="90px">
      <el-form-item label="公司名称" required><el-input v-model="customerForm.companyName" /></el-form-item>
      <el-form-item label="税号"><el-input v-model="customerForm.taxNo" /></el-form-item>
      <el-form-item label="地址"><el-input v-model="customerForm.address" /></el-form-item>
      <el-form-item label="状态"><el-switch v-model="customerForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="customerForm.remark" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="customerVisible = false">取消</el-button>
      <el-button type="primary" @click="saveCustomer">保存</el-button>
    </template>
  </el-dialog>

  <FeeRecordForm v-model="feeVisible" :customer-id="selectedCustomerId" @saved="load" />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import ExpireStatusTag from "../../components/ExpireStatusTag.vue";
import FeeRecordForm from "../../components/FeeRecordForm.vue";
import { createCustomer, deleteCustomer, exportCustomerExpire, updateCustomer } from "../../api/customer";
import { customerExpirePage } from "../../api/customer";
import type { CustomerExpire } from "../../types/common";

const route = useRoute();
const router = useRouter();
const records = ref<CustomerExpire[]>([]);
const total = ref(0);
const feeVisible = ref(false);
const selectedCustomerId = ref<number>();
const customerVisible = ref(false);
const customerForm = reactive<any>({ status: 1 });
const query = reactive({
  companyName: "",
  expireStatus: "",
  pageNum: 1,
  pageSize: 10
});

watch(
  () => route.query.expireStatus,
  (value) => {
    query.expireStatus = typeof value === "string" ? value : "";
    query.pageNum = 1;
    load();
  }
);

onMounted(() => {
  query.expireStatus = typeof route.query.expireStatus === "string" ? route.query.expireStatus : "";
  load();
});

async function load() {
  const data = await customerExpirePage(query);
  records.value = data.records;
  total.value = data.total;
}

function reset() {
  query.companyName = "";
  query.expireStatus = "";
  query.pageNum = 1;
  load();
}

function openFee(customerId: number) {
  selectedCustomerId.value = customerId;
  feeVisible.value = true;
}

function openCustomer(row?: CustomerExpire) {
  Object.assign(customerForm, row ? {
    id: row.customerId,
    companyName: row.companyName,
    taxNo: row.taxNo,
    address: row.address,
    remark: row.remark,
    status: 1
  } : { id: undefined, companyName: "", taxNo: "", address: "", remark: "", status: 1 });
  customerVisible.value = true;
}

async function saveCustomer() {
  if (!customerForm.companyName) {
    ElMessage.warning("请输入公司名称");
    return;
  }
  if (customerForm.id) {
    await updateCustomer(customerForm.id, customerForm);
  } else {
    await createCustomer(customerForm);
  }
  ElMessage.success("客户已保存");
  customerVisible.value = false;
  load();
}

async function remove(id: number) {
  await deleteCustomer(id);
  ElMessage.success("客户已删除");
  load();
}

async function downloadExpire() {
  const response: any = await exportCustomerExpire(query);
  downloadBlob(response.data, "客户到期列表.xlsx");
}

function downloadBlob(blob: Blob, fileName: string) {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  link.click();
  window.URL.revokeObjectURL(url);
}
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
