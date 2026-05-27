import request from "../utils/request";
import type { FeeRecord, PageResult } from "../types/common";

export interface FeeRecordForm {
  customerId?: number;
  receiveDate: string;
  payerName?: string;
  payerContactId?: number;
  payMethod: string;
  receiverName: string;
  amount: number;
  chargeEndDate: string;
  remark?: string;
}

export function feeRecordPage(params: Record<string, unknown>) {
  return request.get<unknown, PageResult<FeeRecord>>("/fee-record/page", { params });
}

export function feeRecordDetail(id: number) {
  return request.get<unknown, FeeRecord>(`/fee-record/${id}`);
}

export function createFeeRecord(data: FeeRecordForm) {
  return request.post<unknown, number>("/fee-record", data);
}

export function updateFeeRecord(id: number, data: FeeRecordForm) {
  return request.put(`/fee-record/${id}`, data);
}

export function deleteFeeRecord(id: number) {
  return request.delete(`/fee-record/${id}`);
}

export function exportFeeRecord(params: Record<string, unknown>) {
  return request.get("/fee-record/export", { params, responseType: "blob" });
}
