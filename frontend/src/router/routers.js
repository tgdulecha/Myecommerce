import { createRouter, createWebHistory } from "vue-router";
import Home from "@/views/Home.vue";

const routes = [
  { path: "/", component: Home },
    { path: "/new-order", component: () => import("@/views/NewOrder.vue") },
    { path: "/sign-in", component: () => import("@/views/SignIn.vue") },
    { path: "/sign-up", component: () => import("@/views/SignUp.vue") },

];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
