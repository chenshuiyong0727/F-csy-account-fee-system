<template>
  <div class="weight-page" v-loading="loading">
    <div class="page-heading">
      <div>
        <h2>多人健康管理</h2>
        <p>为每个人独立维护资料、体重记录和手机展示页。</p>
      </div>
      <div class="person-switcher">
        <span>录入单位</span>
        <el-radio-group v-model="inputUnit" size="small">
          <el-radio-button value="JIN">斤</el-radio-button>
          <el-radio-button value="KG">kg</el-radio-button>
        </el-radio-group>
        <span>当前人员</span>
        <el-select v-model="selectedProfileId" placeholder="请选择人员" style="width: 190px">
          <el-option v-for="item in profiles" :key="item.id" :label="`${item.nickname} · ${genderName(item.gender)}`" :value="item.id" />
        </el-select>
        <el-button type="success" @click="openCreateProfile">新增人员</el-button>
      </div>
    </div>

    <el-empty v-if="!loading && !selectedProfile" description="还没有人员档案">
      <el-button type="primary" @click="openCreateProfile">创建第一位人员</el-button>
    </el-empty>

    <template v-if="selectedProfile">
      <div class="overview-grid">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <div>
                <span>个人信息</span>
                <el-tag :type="profileForm.gender === 'M' ? 'primary' : 'danger'" size="small">{{ genderName(profileForm.gender) }}</el-tag>
              </div>
              <div class="header-actions">
                <el-button @click="copyShareLink">复制手机链接</el-button>
                <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存信息</el-button>
              </div>
            </div>
          </template>

          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="92px">
            <div class="avatar-row">
              <el-avatar :size="82" :src="avatarPreviewUrl">{{ profileForm.nickname?.slice(0, 1) }}</el-avatar>
              <div>
                <strong>{{ profileForm.nickname }}</strong>
                <el-upload :show-file-list="false" accept="image/jpeg,image/png,image/webp" :http-request="handleAvatarUpload">
                  <el-button size="small" :loading="uploadingAvatar">更换头像</el-button>
                </el-upload>
                <p>支持 JPG、PNG、WebP，最大 8MB</p>
              </div>
            </div>

            <div class="form-grid">
              <el-form-item label="昵称" prop="nickname"><el-input v-model="profileForm.nickname" maxlength="50" /></el-form-item>
              <el-form-item label="生日" prop="birthDate"><el-date-picker v-model="profileForm.birthDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="profileForm.gender"><el-radio value="M">男</el-radio><el-radio value="F">女</el-radio></el-radio-group>
              </el-form-item>
              <el-form-item label="身高" prop="heightCm"><el-input-number v-model="profileForm.heightCm" :min="50" :max="260" :precision="1" :step="0.1" /><span class="unit-text">cm</span></el-form-item>
              <el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="profileForm.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
              <el-form-item label="起始体重" prop="startWeightJin"><el-input-number v-model="profileForm.startWeightJin" :min="1" :max="unitMax" :precision="1" :step="0.1" /><span class="unit-text">{{ unitLabel }}</span></el-form-item>
              <el-form-item label="目标体重" prop="targetWeightJin"><el-input-number v-model="profileForm.targetWeightJin" :min="1" :max="unitMax" :precision="1" :step="0.1" /><span class="unit-text">{{ unitLabel }}</span></el-form-item>
            </div>
          </el-form>
        </el-card>

        <el-card class="status-card">
          <template #header><span>当前状态</span></template>
          <div v-if="latestRecord" class="status-content">
            <div class="current-weight"><span>{{ Number(latestRecord.weightJin).toFixed(1) }}</span><small>斤</small></div>
            <p>最新记录 · {{ latestRecord.recordDate }}</p>
            <div class="metric-grid">
              <div><strong>{{ currentBmi }}</strong><span>BMI</span></div>
              <div><strong>{{ totalLoss }}</strong><span>已减重/斤</span></div>
              <div><strong>{{ distanceTarget }}</strong><span>距目标/斤</span></div>
              <div><strong>{{ currentFat }}</strong><span>体脂率</span></div>
            </div>
          </div>
          <el-empty v-else description="还没有体重记录" />
          <div class="status-actions">
            <el-button plain @click="openSharePage">预览手机页</el-button>
            <el-button plain type="danger" @click="removeProfile">删除人员</el-button>
          </div>
        </el-card>
      </div>

      <el-card class="records-card">
        <template #header>
          <div class="card-header">
            <div><span>{{ selectedProfile.nickname }}的体重记录</span><small>批量登记时只需逐行填写体重</small></div>
            <div class="header-actions">
              <el-button type="primary" plain @click="openBatchDialog">批量登记</el-button>
              <el-button type="success" @click="openRecordDialog()">单条登记</el-button>
            </div>
          </div>
        </template>
        <el-table :data="records" border>
          <el-table-column prop="recordDate" label="登记日期" width="130" />
          <el-table-column label="体重" width="130"><template #default="{ row }"><strong>{{ Number(row.weightJin).toFixed(1) }} 斤</strong></template></el-table-column>
          <el-table-column label="BMI" width="100"><template #default="{ row }">{{ calculateBmi(row.weightJin) }}</template></el-table-column>
          <el-table-column label="体脂率" width="150">
            <template #default="{ row }">{{ bodyFatText(row) }} <el-tag v-if="row.bodyFatPercent == null" size="small" type="info">估算</el-tag><el-tag v-else size="small" type="success">实测</el-tag></template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="130" fixed="right">
            <template #default="{ row }">
              <el-button link @click="openRecordDialog(row)">编辑</el-button>
              <el-popconfirm title="确认删除这条体重记录？" @confirm="removeRecord(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <el-dialog v-model="profileDialogVisible" title="新增人员" width="560px" destroy-on-close>
      <div class="dialog-unit-choice"><span>重量录入单位</span><el-radio-group v-model="inputUnit" size="small"><el-radio-button value="JIN">斤</el-radio-button><el-radio-button value="KG">kg</el-radio-button></el-radio-group></div>
      <el-form ref="newProfileFormRef" :model="newProfileForm" :rules="profileRules" label-width="92px">
        <div class="form-grid">
          <el-form-item label="昵称" prop="nickname"><el-input v-model="newProfileForm.nickname" /></el-form-item>
          <el-form-item label="生日" prop="birthDate"><el-date-picker v-model="newProfileForm.birthDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          <el-form-item label="性别" prop="gender"><el-radio-group v-model="newProfileForm.gender"><el-radio value="M">男</el-radio><el-radio value="F">女</el-radio></el-radio-group></el-form-item>
          <el-form-item label="身高" prop="heightCm"><el-input-number v-model="newProfileForm.heightCm" :min="50" :max="260" :precision="1" /><span class="unit-text">cm</span></el-form-item>
          <el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="newProfileForm.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          <el-form-item label="起始体重" prop="startWeightJin"><el-input-number v-model="newProfileForm.startWeightJin" :min="1" :max="unitMax" :precision="1" /><span class="unit-text">{{ unitLabel }}</span></el-form-item>
          <el-form-item label="目标体重" prop="targetWeightJin"><el-input-number v-model="newProfileForm.targetWeightJin" :min="1" :max="unitMax" :precision="1" /><span class="unit-text">{{ unitLabel }}</span></el-form-item>
        </div>
      </el-form>
      <template #footer><el-button @click="profileDialogVisible=false">取消</el-button><el-button type="primary" :loading="creatingProfile" @click="createProfile">创建人员</el-button></template>
    </el-dialog>

    <el-dialog v-model="recordDialogVisible" :title="editingRecordId ? '编辑体重记录' : '单条登记'" width="480px" destroy-on-close>
      <div class="dialog-unit-choice"><span>本次录入单位</span><el-radio-group v-model="inputUnit" size="small"><el-radio-button value="JIN">斤</el-radio-button><el-radio-button value="KG">kg</el-radio-button></el-radio-group></div>
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="96px">
        <el-form-item label="登记日期" prop="recordDate"><el-date-picker v-model="recordForm.recordDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="体重" prop="weightJin"><el-input-number v-model="recordForm.weightJin" :min="1" :max="unitMax" :precision="1" :step="0.1" /><span class="unit-text">{{ unitLabel }}</span></el-form-item>
        <el-form-item label="实测体脂率"><el-input-number v-model="recordForm.bodyFatPercent" :min="1" :max="75" :precision="1" /><span class="unit-text">%</span><div class="field-tip">可不填，系统将自动估算。</div></el-form-item>
        <el-form-item label="备注"><el-input v-model="recordForm.remark" type="textarea" :rows="3" maxlength="500" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="recordDialogVisible=false">取消</el-button><el-button type="primary" :loading="savingRecord" @click="saveRecord">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" title="批量登记体重" width="680px" destroy-on-close>
      <el-alert title="日期从人员的开始记录日自动生成，你只需要填写体重。" type="success" :closable="false" show-icon />
      <div class="dialog-unit-choice batch-unit-choice"><span>本次批量录入单位</span><el-radio-group v-model="inputUnit" size="small"><el-radio-button value="JIN">斤</el-radio-button><el-radio-button value="KG">kg</el-radio-button></el-radio-group><em>保存时自动换算成斤</em></div>
      <div class="batch-settings">
        <div><span>开始日期</span><el-date-picker :model-value="profileForm.startDate" type="date" value-format="YYYY-MM-DD" disabled /></div>
        <div><span>截止日期</span><el-date-picker v-model="batchForm.endDate" type="date" value-format="YYYY-MM-DD" @change="generateBatchRows" /></div>
        <div><span>每隔</span><el-input-number v-model="batchForm.intervalDays" :min="1" :max="365" @change="generateBatchRows" /><em>天记录一次</em></div>
      </div>
      <div class="quick-interval"><span>快捷选择：</span><el-button size="small" @click="setIntervalDays(7)">每7天</el-button><el-button size="small" @click="setIntervalDays(15)">每15天</el-button></div>
      <el-table :data="batchRows" border max-height="360" class="batch-table">
        <el-table-column type="index" label="#" width="55" />
        <el-table-column prop="date" label="自动生成日期" width="160" />
        <el-table-column :label="`体重（${unitLabel}）`">
          <template #default="{ row }"><el-input-number v-model="row.weight" :min="1" :max="unitMax" :precision="1" :step="0.1" placeholder="填写体重" /></template>
        </el-table-column>
        <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag v-if="row.existing" type="warning">已有记录</el-tag><el-tag v-else type="success">待新增</el-tag></template></el-table-column>
      </el-table>
      <div class="batch-footer-tip"><el-switch v-model="batchForm.overwriteExisting" active-text="覆盖已有日期" inactive-text="跳过已有日期" /><span>共生成 {{ batchRows.length }} 个日期</span></div>
      <template #footer><el-button @click="batchDialogVisible=false">取消</el-button><el-button type="primary" :loading="savingBatch" @click="saveBatch">批量保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import {
  batchCreateWeightRecords, createWeightProfile, createWeightRecord, deleteWeightProfile,
  deleteWeightRecord, getWeightProfiles, getWeightRecords, updateWeightProfile,
  updateWeightRecord, updateWeightProfileAvatar, uploadWeightAvatar, type WeightProfile, type WeightProfileForm,
  type WeightRecord, type WeightRecordForm
} from "../../api/weight";

interface BatchRow { date: string; weight?: number; existing: boolean }
type WeightUnit = "JIN" | "KG";

const loading = ref(false);
const profiles = ref<WeightProfile[]>([]);
const selectedProfileId = ref<number>();
const records = ref<WeightRecord[]>([]);
const profileFormRef = ref<FormInstance>();
const newProfileFormRef = ref<FormInstance>();
const recordFormRef = ref<FormInstance>();
const savingProfile = ref(false);
const creatingProfile = ref(false);
const savingRecord = ref(false);
const savingBatch = ref(false);
const uploadingAvatar = ref(false);
const profileDialogVisible = ref(false);
const recordDialogVisible = ref(false);
const batchDialogVisible = ref(false);
const editingRecordId = ref<number>();
const batchRows = ref<BatchRow[]>([]);
const inputUnit = ref<WeightUnit>("JIN");
const avatarVersion = ref(Date.now());

const emptyProfile = (): WeightProfileForm => ({ nickname: "", avatarUrl: "/brand/person.png", birthDate: "", gender: "M", heightCm: 170, startDate: localToday(), startWeightJin: 0, targetWeightJin: 0 });
const profileForm = reactive<WeightProfileForm>(emptyProfile());
const newProfileForm = reactive<WeightProfileForm>(emptyProfile());
const recordForm = reactive<WeightRecordForm>({ recordDate: "", weightJin: 0, bodyFatPercent: undefined, remark: "" });
const batchForm = reactive({ endDate: localToday(), intervalDays: 7, overwriteExisting: false });

const profileRules: FormRules = {
  nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  birthDate: [{ required: true, message: "请选择生日", trigger: "change" }],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  heightCm: [{ required: true, message: "请输入身高", trigger: "change" }],
  startDate: [{ required: true, message: "请选择开始日期", trigger: "change" }],
  startWeightJin: [{ required: true, message: "请输入起始体重", trigger: "change" }],
  targetWeightJin: [{ required: true, message: "请输入目标体重", trigger: "change" }]
};
const recordRules: FormRules = { recordDate: [{ required: true, message: "请选择登记日期", trigger: "change" }], weightJin: [{ required: true, message: "请输入体重", trigger: "change" }] };

const selectedProfile = computed(() => profiles.value.find(item => item.id === selectedProfileId.value));
const latestRecord = computed(() => records.value[0]);
const unitLabel = computed(() => inputUnit.value === "KG" ? "kg" : "斤");
const unitMax = computed(() => inputUnit.value === "KG" ? 499.99 : 999.99);
const currentBmi = computed(() => latestRecord.value ? calculateBmi(latestRecord.value.weightJin) : "--");
const totalLoss = computed(() => latestRecord.value ? (toJin(profileForm.startWeightJin) - latestRecord.value.weightJin).toFixed(1) : "--");
const distanceTarget = computed(() => latestRecord.value ? Math.max(0, latestRecord.value.weightJin - toJin(profileForm.targetWeightJin)).toFixed(1) : "--");
const currentFat = computed(() => latestRecord.value ? bodyFatText(latestRecord.value) : "--");
const shareUrl = computed(() => `${window.location.origin}/weight.html?id=${selectedProfileId.value}`);
const avatarPreviewUrl = computed(() => {
  const url = profileForm.avatarUrl || "/brand/person.png";
  return `${url}${url.includes("?") ? "&" : "?"}v=${avatarVersion.value}`;
});

watch(selectedProfileId, async () => { syncProfileForm(); await loadRecords(); });
watch(inputUnit, (next, previous) => {
  const factor = next === "KG" && previous === "JIN" ? 0.5 : 2;
  convertFormWeights(profileForm, factor);
  convertFormWeights(newProfileForm, factor);
  if (recordForm.weightJin) recordForm.weightJin = roundWeight(recordForm.weightJin * factor);
  batchRows.value.forEach(row => { if (row.weight) row.weight = roundWeight(row.weight * factor); });
});
onMounted(loadProfiles);

async function loadProfiles(preferredId?: number) {
  loading.value = true;
  try {
    profiles.value = await getWeightProfiles();
    const target = preferredId || selectedProfileId.value;
    selectedProfileId.value = profiles.value.some(item => item.id === target) ? target : profiles.value[0]?.id;
    syncProfileForm();
    await loadRecords();
  } finally { loading.value = false; }
}

function syncProfileForm() {
  if (!selectedProfile.value) return;
  Object.assign(profileForm, selectedProfile.value, {
    startWeightJin: fromJin(selectedProfile.value.startWeightJin),
    targetWeightJin: fromJin(selectedProfile.value.targetWeightJin)
  });
}
async function loadRecords() { records.value = selectedProfileId.value ? await getWeightRecords(selectedProfileId.value) : []; }

async function saveProfile() {
  if (!selectedProfileId.value) return;
  await profileFormRef.value?.validate();
  if (!validGoal(profileForm)) return;
  savingProfile.value = true;
  try { await updateWeightProfile(selectedProfileId.value, normalizeProfile(profileForm)); ElMessage.success("个人信息已保存"); await loadProfiles(selectedProfileId.value); }
  finally { savingProfile.value = false; }
}

function openCreateProfile() { Object.assign(newProfileForm, emptyProfile()); profileDialogVisible.value = true; }
async function createProfile() {
  await newProfileFormRef.value?.validate();
  if (!validGoal(newProfileForm)) return;
  creatingProfile.value = true;
  try { const id = await createWeightProfile(normalizeProfile(newProfileForm)); profileDialogVisible.value = false; ElMessage.success("人员创建成功"); await loadProfiles(id); }
  finally { creatingProfile.value = false; }
}

async function removeProfile() {
  if (!selectedProfileId.value) return;
  await ElMessageBox.confirm(`确认删除“${selectedProfile.value?.nickname}”？历史体重记录将保留但不再展示。`, "删除人员", { type: "warning", confirmButtonText: "确认删除", cancelButtonText: "取消" });
  await deleteWeightProfile(selectedProfileId.value); ElMessage.success("人员已删除"); await loadProfiles();
}

async function handleAvatarUpload(options: any) {
  if (!selectedProfileId.value) return;
  uploadingAvatar.value = true;
  try {
    const avatarUrl = await uploadWeightAvatar(options.file);
    await updateWeightProfileAvatar(selectedProfileId.value, avatarUrl);
    profileForm.avatarUrl = avatarUrl;
    if (selectedProfile.value) selectedProfile.value.avatarUrl = avatarUrl;
    avatarVersion.value = Date.now();
    ElMessage.success("头像已上传并自动保存");
  } finally { uploadingAvatar.value = false; }
}

function openRecordDialog(row?: WeightRecord) {
  editingRecordId.value = row?.id;
  Object.assign(recordForm, row ? { recordDate: row.recordDate, weightJin: fromJin(row.weightJin), bodyFatPercent: row.bodyFatPercent, remark: row.remark || "" } : { recordDate: localToday(), weightJin: fromJin(latestRecord.value?.weightJin) || profileForm.startWeightJin, bodyFatPercent: undefined, remark: "" });
  recordDialogVisible.value = true;
}
async function saveRecord() {
  if (!selectedProfileId.value) return;
  await recordFormRef.value?.validate(); savingRecord.value = true;
  const payload = { ...recordForm, weightJin: toJin(recordForm.weightJin) };
  try { if (editingRecordId.value) await updateWeightRecord(editingRecordId.value, payload); else await createWeightRecord(selectedProfileId.value, payload); recordDialogVisible.value = false; ElMessage.success("体重记录已保存"); await loadRecords(); }
  finally { savingRecord.value = false; }
}
async function removeRecord(id: number) { await deleteWeightRecord(id); ElMessage.success("体重记录已删除"); await loadRecords(); }

function openBatchDialog() {
  batchForm.intervalDays = 7; batchForm.overwriteExisting = false;
  batchForm.endDate = new Date(profileForm.startDate + "T00:00:00") > new Date() ? profileForm.startDate : localToday();
  generateBatchRows(); batchDialogVisible.value = true;
}
function setIntervalDays(days: number) { batchForm.intervalDays = days; generateBatchRows(); }
function generateBatchRows() {
  if (!profileForm.startDate || !batchForm.endDate || batchForm.intervalDays < 1) { batchRows.value = []; return; }
  const end = new Date(batchForm.endDate + "T00:00:00");
  const date = new Date(profileForm.startDate + "T00:00:00");
  const rows: BatchRow[] = [];
  while (date <= end && rows.length < 201) {
    const dateText = formatLocalDate(date);
    const old = records.value.find(item => item.recordDate === dateText);
    rows.push({ date: dateText, weight: fromJin(old?.weightJin), existing: Boolean(old) });
    date.setDate(date.getDate() + batchForm.intervalDays);
  }
  batchRows.value = rows;
}
async function saveBatch() {
  if (!selectedProfileId.value || !batchRows.value.length) { ElMessage.warning("没有可保存的日期"); return; }
  if (batchRows.value.some(item => !item.weight || item.weight <= 0)) { ElMessage.warning("请填写每个待新增日期的体重"); return; }
  savingBatch.value = true;
  try {
    const result = await batchCreateWeightRecords(selectedProfileId.value, { endDate: batchForm.endDate, intervalDays: batchForm.intervalDays, weights: batchRows.value.map(item => toJin(Number(item.weight))), overwriteExisting: batchForm.overwriteExisting });
    batchDialogVisible.value = false;
    ElMessage.success(`批量完成：新增${result.inserted}条，更新${result.updated}条，跳过${result.skipped}条`);
    await loadRecords();
  } finally { savingBatch.value = false; }
}

function validGoal(form: WeightProfileForm) { if (form.targetWeightJin >= form.startWeightJin) { ElMessage.warning("目标体重必须小于起始体重"); return false; } return true; }
function normalizeProfile(form: WeightProfileForm): WeightProfileForm { return { ...form, startWeightJin: toJin(form.startWeightJin), targetWeightJin: toJin(form.targetWeightJin) }; }
function convertFormWeights(form: WeightProfileForm, factor: number) { if (form.startWeightJin) form.startWeightJin = roundWeight(form.startWeightJin * factor); if (form.targetWeightJin) form.targetWeightJin = roundWeight(form.targetWeightJin * factor); }
function fromJin(value?: number) { return value == null ? undefined : roundWeight(inputUnit.value === "KG" ? value / 2 : value); }
function toJin(value: number) { return roundWeight(inputUnit.value === "KG" ? value * 2 : value); }
function roundWeight(value: number) { return Math.round(value * 10) / 10; }
function calculateBmi(weightJin: number) { return profileForm.heightCm ? ((weightJin / 2) / Math.pow(profileForm.heightCm / 100, 2)).toFixed(1) : "--"; }
function bodyFatText(record: WeightRecord) { if (record.bodyFatPercent != null) return `${Number(record.bodyFatPercent).toFixed(1)}%`; const bmi = Number(calculateBmi(record.weightJin)); const age = ageAt(profileForm.birthDate, record.recordDate); return `${Math.min(65, Math.max(2, 1.2 * bmi + 0.23 * age - 10.8 * (profileForm.gender === "M" ? 1 : 0) - 5.4)).toFixed(1)}%`; }
function ageAt(birthDate: string, date: string) { const birth = new Date(birthDate + "T00:00:00"); const current = new Date(date + "T00:00:00"); let age = current.getFullYear() - birth.getFullYear(); if (current.getMonth() < birth.getMonth() || (current.getMonth() === birth.getMonth() && current.getDate() < birth.getDate())) age--; return Math.max(0, age); }
function genderName(value: string) { return value === "F" ? "女" : "男"; }
function localToday() { return formatLocalDate(new Date()); }
function formatLocalDate(date: Date) { return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,"0")}-${String(date.getDate()).padStart(2,"0")}`; }
async function copyShareLink() { try { await navigator.clipboard.writeText(shareUrl.value); } catch { const textarea=document.createElement("textarea"); textarea.value=shareUrl.value; document.body.appendChild(textarea); textarea.select(); document.execCommand("copy"); textarea.remove(); } ElMessage.success("该人员的手机链接已复制"); }
function openSharePage() { window.open(shareUrl.value, "_blank"); }
</script>

<style scoped>
.weight-page{display:flex;flex-direction:column;gap:18px}.page-heading,.card-header{display:flex;align-items:center;justify-content:space-between;gap:16px}.page-heading h2{margin:0 0 6px;font-size:24px;color:#102a27}.page-heading p,.avatar-row p,.card-header small{margin:0;color:#84928f;font-size:13px}.person-switcher,.header-actions,.card-header>div,.avatar-row>div{display:flex;align-items:center;gap:10px}.person-switcher>span{color:#61716e;font-size:13px}.overview-grid{display:grid;grid-template-columns:minmax(560px,1.6fr) minmax(300px,.8fr);gap:18px}.profile-card,.status-card,.records-card{border:0;border-radius:18px;box-shadow:0 10px 28px rgba(25,54,50,.06)}.avatar-row{display:flex;align-items:center;gap:18px;margin-bottom:22px;padding:14px;background:#f4fbf9;border-radius:16px}.avatar-row>div{align-items:flex-start;flex-direction:column}.avatar-row p{font-size:12px}.form-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:0 18px}.unit-text{margin-left:8px;color:#7f8d8a}.dialog-unit-choice{display:flex;align-items:center;gap:12px;margin:0 0 18px;padding:12px 14px;border-radius:12px;background:#f3f8f7;color:#536460;font-size:13px}.dialog-unit-choice em{color:#8a9996;font-size:12px;font-style:normal}.batch-unit-choice{margin-top:14px}.status-card{background:linear-gradient(145deg,#087f74,#13b79f);color:#fff}.status-card :deep(.el-card__header){border-color:rgba(255,255,255,.18);font-weight:700}.current-weight span{font-size:54px;font-weight:900;letter-spacing:-2px}.current-weight small{margin-left:8px}.status-content>p{margin:2px 0 24px;opacity:.72}.metric-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:10px}.metric-grid div{padding:13px;border:1px solid rgba(255,255,255,.15);border-radius:14px;background:rgba(255,255,255,.1)}.metric-grid strong,.metric-grid span{display:block}.metric-grid strong{font-size:19px}.metric-grid span{margin-top:5px;font-size:11px;opacity:.72}.status-actions{display:flex;justify-content:space-between;margin-top:18px}.field-tip{width:100%;color:#98a5a2;font-size:12px}.batch-settings{display:grid;grid-template-columns:1fr 1fr 1.25fr;gap:12px;margin:18px 0 10px}.batch-settings>div{display:flex;align-items:center;gap:7px}.batch-settings span,.batch-settings em{color:#667572;font-size:12px;font-style:normal;white-space:nowrap}.quick-interval{display:flex;align-items:center;gap:6px;margin-bottom:12px;color:#778784;font-size:12px}.batch-table{margin-top:10px}.batch-footer-tip{display:flex;align-items:center;justify-content:space-between;margin-top:14px;color:#82908d;font-size:13px}@media(max-width:1100px){.overview-grid{grid-template-columns:1fr}}@media(max-width:760px){.page-heading,.card-header{align-items:flex-start;flex-direction:column}.person-switcher,.header-actions{flex-wrap:wrap}.form-grid,.batch-settings{grid-template-columns:1fr}.batch-settings>div{justify-content:space-between}}
</style>
