import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import Dashboard from "../views/Dashboard.vue";
import CustomerList from "../views/customer/CustomerList.vue";
import CustomerDetail from "../views/customer/CustomerDetail.vue";
import FeeRecordList from "../views/feeRecord/FeeRecordList.vue";
import CashDetail from "../views/feeRecord/CashDetail.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/dashboard" },
    { path: "/login", component: Login },
    { path: "/dashboard", component: Dashboard },
    { path: "/customer", component: CustomerList },
    { path: "/customer/detail/:id", component: CustomerDetail },
    { path: "/fee-record", component: FeeRecordList },
    { path: "/cash-detail", component: CashDetail }
  ]
});

router.beforeEach((to) => {
  const token = localStorage.getItem("token");
  if (to.path !== "/login" && !token) {
    return "/login";
  }
  if (to.path === "/login" && token) {
    return "/dashboard";
  }
  return true;
});

export default router;
