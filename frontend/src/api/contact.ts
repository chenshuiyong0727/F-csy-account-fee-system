import request from "../utils/request";
import type { Contact } from "../types/common";

export interface ContactForm {
  contactName: string;
  contactPhone?: string;
  contactWechat?: string;
  positionName?: string;
  isPrimary?: number;
  remark?: string;
}

export function contactList(customerId: number) {
  return request.get<unknown, Contact[]>(`/customer/${customerId}/contact/list`);
}

export function createContact(customerId: number, data: ContactForm) {
  return request.post<unknown, number>(`/customer/${customerId}/contact`, data);
}

export function updateContact(id: number, data: ContactForm) {
  return request.put(`/customer/contact/${id}`, data);
}

export function deleteContact(id: number) {
  return request.delete(`/customer/contact/${id}`);
}

export function setPrimaryContact(id: number) {
  return request.put(`/customer/contact/${id}/primary`);
}
