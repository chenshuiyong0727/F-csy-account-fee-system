export interface PageResult<T> {
  total: number;
  records: T[];
}

export interface CustomerExpire {
  customerId: number;
  companyName: string;
  taxNo?: string;
  address?: string;
  remark?: string;
  latestChargeEndDate?: string;
  remainDays?: number;
  expireStatus: string;
  primaryContactName?: string;
  primaryContactPhone?: string;
  primaryContactWechat?: string;
  latestReceiveDate?: string;
  latestReceiveAmount?: number;
}

export interface Contact {
  id: number;
  customerId: number;
  contactName: string;
  contactPhone?: string;
  contactWechat?: string;
  positionName?: string;
  isPrimary: number;
  remark?: string;
}

export interface FeeRecord {
  id: number;
  customerId: number;
  companyName: string;
  receiveDate: string;
  payerName?: string;
  payerContactId?: number;
  payerContactName?: string;
  payMethod: string;
  receiverName: string;
  amount: number;
  chargeEndDate: string;
  remark?: string;
  createBy?: string;
  createTime?: string;
}
