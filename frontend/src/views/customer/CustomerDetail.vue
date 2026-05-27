<template>
  <div>
    <el-card class="page-card">
      <template #header>
        <div class="detail-head">
          <span>{{ customer?.companyName }}</span>
          <div>
            <el-button @click="router.back()">返回</el-button>
            <el-button type="success" @click="feeVisible = true">新增收款</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="税号">{{ customer?.taxNo || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ customer?.status === 1 ? "正常" : "停用" }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ customer?.address || "-" }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ customer?.remark || "-" }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="page-card section-card">
      <template #header>
        <div class="detail-head">
          <span>联系人</span>
          <el-button type="primary" @click="openContact()">新增联系人</el-button>
        </div>
      </template>
      <el-table :data="contacts" border>
        <el-table-column prop="contactName" label="姓名" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="contactWechat" label="微信号" />
        <el-table-column prop="positionName" label="职位/身份" />
        <el-table-column label="主要联系人">
          <template #default="{ row }"><el-tag v-if="row.isPrimary === 1" type="success">是</el-tag><span v-else>否</span></template>
        </el-table-column>
        <el-table-column label="操作" width="210">
          <template #default="{ row }">
            <el-button link @click="openContact(row)">编辑</el-button>
            <el-button link type="success" @click="primary(row.id)">设为主要</el-button>
            <el-popconfirm title="确认删除联系人？" @confirm="removeContact(row.id)">
              <template #reference><el-button link type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="page-card section-card">
      <template #header><span>收款记录</span></template>
      <el-table :data="feeRecords" border>
        <el-table-column prop="receiveDate" label="收款日期" width="120" />
        <el-table-column prop="payerName" label="付款人" />
        <el-table-column prop="payMethod" label="方式" width="100" />
        <el-table-column prop="receiverName" label="收款人" width="100" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="chargeEndDate" label="收费到日期" width="130" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-card>

    <el-dialog v-model="contactVisible" :title="contactForm.id ? '编辑联系人' : '新增联系人'" width="560px">
      <el-form :model="contactForm" label-width="110px">
        <el-form-item label="联系人姓名" required><el-input v-model="contactForm.contactName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="contactForm.contactPhone" /></el-form-item>
        <el-form-item label="微信号"><el-input v-model="contactForm.contactWechat" /></el-form-item>
        <el-form-item label="职位/身份"><el-input v-model="contactForm.positionName" /></el-form-item>
        <el-form-item label="主要联系人"><el-switch v-model="contactForm.isPrimary" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="contactForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactVisible = false">取消</el-button>
        <el-button type="primary" @click="saveContact">保存</el-button>
      </template>
    </el-dialog>

    <FeeRecordForm v-model="feeVisible" :customer-id="customerId" @saved="loadFeeRecords" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import FeeRecordForm from "../../components/FeeRecordForm.vue";
import { customerDetail, type Customer } from "../../api/customer";
import { contactList, createContact, deleteContact, setPrimaryContact, updateContact } from "../../api/contact";
import { feeRecordPage } from "../../api/feeRecord";
import type { Contact, FeeRecord } from "../../types/common";

const route = useRoute();
const router = useRouter();
const customerId = Number(route.params.id);
const customer = ref<Customer>();
const contacts = ref<Contact[]>([]);
const feeRecords = ref<FeeRecord[]>([]);
const contactVisible = ref(false);
const feeVisible = ref(false);
const contactForm = reactive<any>({ isPrimary: 0 });

onMounted(async () => {
  customer.value = await customerDetail(customerId);
  await loadContacts();
  await loadFeeRecords();
});

async function loadContacts() {
  contacts.value = await contactList(customerId);
}

async function loadFeeRecords() {
  const data = await feeRecordPage({ customerId, pageNum: 1, pageSize: 100 });
  feeRecords.value = data.records;
}

function openContact(row?: Contact) {
  Object.assign(contactForm, row || { id: undefined, contactName: "", contactPhone: "", contactWechat: "", positionName: "", isPrimary: 0, remark: "" });
  contactVisible.value = true;
}

async function saveContact() {
  if (!contactForm.contactName) {
    ElMessage.warning("请输入联系人姓名");
    return;
  }
  if (contactForm.id) {
    await updateContact(contactForm.id, contactForm);
  } else {
    await createContact(customerId, contactForm);
  }
  ElMessage.success("联系人已保存");
  contactVisible.value = false;
  loadContacts();
}

async function primary(id: number) {
  await setPrimaryContact(id);
  ElMessage.success("已设置主要联系人");
  loadContacts();
}

async function removeContact(id: number) {
  await deleteContact(id);
  ElMessage.success("联系人已删除");
  loadContacts();
}
</script>

<style scoped>
.detail-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-card {
  margin-top: 16px;
}
</style>
