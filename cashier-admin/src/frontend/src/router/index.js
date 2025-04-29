import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/order/list'
  },
  {
    path: '/order/list',
    name: 'order-list',
    component: () => import('../views/order/OrderList.vue')
  },
  {
    path: '/order/statistics',
    name: 'order-statistics',
    component: () => import('../views/order/OrderStatistics.vue')
  },
  {
    path: '/product/list',
    name: 'product-list',
    component: () => import('../views/product/ProductList.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 