import request from "../utils/request";

export interface DashboardSummary {
  customerTotal: number;
  normalCount: number;
  warning60Count: number;
  warning30Count: number;
  warning5Count: number;
  expiredCount: number;
  noRecordCount: number;
  monthAmount: number;
  monthCount: number;
  yearAmount: number;
  yearCount: number;
}

export function getDashboardSummary() {
  return request.get<unknown, DashboardSummary>("/dashboard/summary");
}
