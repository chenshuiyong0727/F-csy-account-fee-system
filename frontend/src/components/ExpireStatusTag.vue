<template>
  <el-tag v-if="status === 'NORMAL'" type="success">正常</el-tag>
  <el-tag v-else-if="status === 'WARNING_60'" type="warning">60天内到期</el-tag>
  <el-tag v-else-if="status === 'WARNING_30'" type="danger">30天内到期</el-tag>
  <el-tag v-else-if="status === 'WARNING_5'" color="#722ed1" effect="dark">5天内到期</el-tag>
  <el-tag v-else-if="status === 'EXPIRED'" type="info">已过期/欠费</el-tag>
  <el-tag v-else-if="status === 'NO_RECORD'" type="info">无收费记录</el-tag>
  <el-tag v-else type="info">未知</el-tag>
</template>

<script setup lang="ts">
defineProps<{ status?: string }>();
</script>
