<template>
  <a-layout class="layout">
    <a-layout-header class="header">
      <div class="logo">收银系统</div>
      <a-menu
        mode="horizontal"
        :selected-keys="[currentRoute]"
        @menu-item-click="handleMenuClick"
      >
        <a-menu-item key="order-list">订单管理</a-menu-item>
        <a-menu-item key="order-statistics">订单统计</a-menu-item>
        <a-menu-item key="product-list">商品管理</a-menu-item>
      </a-menu>
    </a-layout-header>
    <a-layout-content class="content">
      <router-view />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const currentRoute = computed(() => route.name)

const handleMenuClick = (key) => {
  router.push({ name: key })
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.logo {
  width: 120px;
  font-size: 18px;
  font-weight: bold;
  margin-right: 40px;
}

.content {
  padding: 20px;
  background: #f5f5f5;
}
</style> 