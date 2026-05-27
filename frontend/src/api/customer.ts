import request from "../utils/request";
import type { CustomerExpire, PageResult } from "../types/common";

export interface Customer {
  id: number;
  companyName: string;
  taxNo?: string;
  address?: string;
  remark?: string;
  status: number;
}

export interface CustomerForm {
  companyName: string;
  taxNo?: string;
  address?: string;
  remark?: string;
  status?: number;
}

export function customerPage(params: Record<string, unknown>) {
  return request.get<unknown, PageResult<Customer>>("/customer/page", { params });
}

export function customerExpirePage(params: Record<string, unknown>) {
  return request.get<unknown, PageResult<CustomerExpire>>("/customer/expire-page", { params });
}

export function customerDetail(id: number) {
  return request.get<unknown, Customer>(`/customer/${id}`);
}

export function createCustomer(data: CustomerForm) {
  return request.post<unknown, number>("/customer", data);
}

export function updateCustomer(id: number, data: CustomerForm) {
  return request.put(`/customer/${id}`, data);
}

export function deleteCustomer(id: number) {
  return request.delete(`/customer/${id}`);
}

export function exportCustomerExpire(params: Record<string, unknown>) {
  return request.get("/customer/expire-export", { params, responseType: "blob" });
}
