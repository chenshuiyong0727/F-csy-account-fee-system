<template>
  <el-dialog v-model="visible" :title="form.id ? '编辑收款记录' : '新增收款记录'" width="720px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="客户公司" prop="customerId">
        <el-select v-model="form.customerId" filterable remote :remote-method="loadCustomers" placeholder="输入公司名称搜索" style="width: 100%">
          <el-option v-for="item in customers" :key="item.id" :label="item.companyName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="收款日期" prop="receiveDate">
            <el-date-picker v-model="form.receiveDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收费到日期" prop="chargeEndDate">
            <el-date-picker v-model="form.chargeEndDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="付款联系人">
            <el-select v-model="form.payerContactId" clearable placeholder="可为空" style="width: 100%">
              <el-option v-for="item in contacts" :key="item.id" :label="item.contactName" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="付款人">
            <el-input v-model="form.payerName" placeholder="付款人/转账人" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="收款方式" prop="payMethod">
            <el-select v-model="form.payMethod" style="width: 100%">
              <el-option label="微信" value="WECHAT" />
              <el-option label="支付宝" value="ALIPAY" />
              <el-option label="对公" value="BANK" />
              <el-option label="现金" value="CASH" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收款人" prop="receiverName">
            <el-input v-model="form.receiverName" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="收款金额" prop="amount">
        <el-input-number v-model="form.amount" :precision="2" :min="0" style="width: 220px" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit(false)">保存</el-button>
      <el-button v-if="!form.id" type="success" @click="submit(true)">保存并继续新增</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import { ElMessage, type FormInstance } from "element-plus";
import { contactList } from "../api/contact";
import { customerPage, type Customer } from "../api/customer";
import { createFeeRecord, updateFeeRecord } from "../api/feeRecord";
import type { Contact, FeeRecord } from "../types/common";

const props = defineProps<{ modelValue: boolean; record?: Partial<FeeRecord>; customerId?: number }>();
const emit = defineEmits<{ "update:modelValue": [value: boolean]; saved: [] }>();

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value)
});

const formRef = ref<FormInstance>();
const customers = ref<Customer[]>([]);
const contacts = ref<Contact[]>([]);
const form = reactive<Partial<FeeRecord> & { amount: number }>({
  receiveDate: new Date().toISOString().slice(0, 10),
  payMethod: "WECHAT",
  receiverName: "",
  amount: 0,
  chargeEndDate: ""
});

const rules = {
  customerId: [{ required: true, message: "请选择客户公司", trigger: "change" }],
  receiveDate: [{ required: true, message: "请选择收款日期", trigger: "change" }],
  payMethod: [{ required: true, message: "请选择收款方式", trigger: "change" }],
  receiverName: [{ required: true, message: "请输入收款人", trigger: "blur" }],
  amount: [{ required: true, message: "请输入金额", trigger: "blur" }],
  chargeEndDate: [{ required: true, message: "请选择收费到日期", trigger: "change" }]
};

watch(
  () => props.modelValue,
  async (open) => {
    if (!open) return;
    Object.assign(form, {
      id: props.record?.id,
      customerId: props.record?.customerId || props.customerId,
      receiveDate: props.record?.receiveDate || new Date().toISOString().slice(0, 10),
      payerName: props.record?.payerName || "",
      payerContactId: props.record?.payerContactId,
      payMethod: props.record?.payMethod || "WECHAT",
      receiverName: props.record?.receiverName || localStorage.getItem("realName") || "",
      amount: props.record?.amount || 0,
      chargeEndDate: props.record?.chargeEndDate || "",
      remark: props.record?.remark || ""
    });
    await loadCustomers("");
    if (form.customerId) await loadContacts(form.customerId);
  }
);

watch(
  () => form.customerId,
  (customerId) => {
    form.payerContactId = undefined;
    if (customerId) loadContacts(customerId);
  }
);

async function loadCustomers(keyword: string) {
  const data = await customerPage({ companyName: keyword, pageNum: 1, pageSize: 30, status: 1 });
  customers.value = data.records;
}

async function loadContacts(customerId: number) {
  contacts.value = await contactList(customerId);
}

async function submit(continueAdd: boolean) {
  await formRef.value?.validate();
  const payload = {
    customerId: form.customerId,
    receiveDate: form.receiveDate || "",
    payerName: form.payerName,
    payerContactId: form.payerContactId,
    payMethod: form.payMethod || "",
    receiverName: form.receiverName || "",
    amount: form.amount || 0,
    chargeEndDate: form.chargeEndDate || "",
    remark: form.remark
  };
  if (form.id) {
    await updateFeeRecord(form.id, payload);
  } else {
    await createFeeRecord(payload);
  }
  ElMessage.success("收款记录已保存");
  emit("saved");
  if (continueAdd) {
    Object.assign(form, { id: undefined, payerName: "", payerContactId: undefined, amount: 0, remark: "" });
  } else {
    visible.value = false;
  }
}
</script>
