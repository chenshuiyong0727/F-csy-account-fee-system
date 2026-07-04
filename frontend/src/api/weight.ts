import request from "../utils/request";

export interface WeightProfile {
  id: number;
  nickname: string;
  avatarUrl?: string;
  birthDate: string;
  gender: "M" | "F";
  heightCm: number;
  startDate: string;
  startWeightJin: number;
  targetWeightJin: number;
  shareToken: string;
}

export interface WeightProfileForm {
  nickname: string;
  avatarUrl?: string;
  birthDate: string;
  gender: "M" | "F";
  heightCm: number;
  startDate: string;
  startWeightJin: number;
  targetWeightJin: number;
}

export interface WeightRecord {
  id: number;
  profileId: number;
  recordDate: string;
  weightJin: number;
  bodyFatPercent?: number;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface WeightRecordForm {
  recordDate: string;
  weightJin: number;
  bodyFatPercent?: number;
  remark?: string;
}

export interface WeightBatchForm {
  endDate: string;
  intervalDays: number;
  weights: number[];
  overwriteExisting: boolean;
}

export interface WeightBatchResult {
  inserted: number;
  updated: number;
  skipped: number;
}

export function getWeightProfiles() {
  return request.get<unknown, WeightProfile[]>("/weight/profiles");
}

export function createWeightProfile(data: WeightProfileForm) {
  return request.post<unknown, number>("/weight/profiles", data);
}

export function updateWeightProfile(id: number, data: WeightProfileForm) {
  return request.put(`/weight/profiles/${id}`, data);
}

export function updateWeightProfileAvatar(id: number, avatarUrl: string) {
  return request.put(`/weight/profiles/${id}/avatar`, { avatarUrl });
}

export function deleteWeightProfile(id: number) {
  return request.delete(`/weight/profiles/${id}`);
}

export function getWeightRecords(profileId: number) {
  return request.get<unknown, WeightRecord[]>(`/weight/profiles/${profileId}/records`);
}

export function createWeightRecord(profileId: number, data: WeightRecordForm) {
  return request.post<unknown, number>(`/weight/profiles/${profileId}/records`, data);
}

export function updateWeightRecord(id: number, data: WeightRecordForm) {
  return request.put(`/weight/records/${id}`, data);
}

export function deleteWeightRecord(id: number) {
  return request.delete(`/weight/records/${id}`);
}

export function batchCreateWeightRecords(profileId: number, data: WeightBatchForm) {
  return request.post<unknown, WeightBatchResult>(`/weight/profiles/${profileId}/records/batch`, data);
}

export function uploadWeightAvatar(file: File) {
  const form = new FormData();
  form.append("file", file);
  return request.post<unknown, string>("/weight/avatar", form);
}
