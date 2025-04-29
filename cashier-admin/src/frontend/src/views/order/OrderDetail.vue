<template>
  <div class="order-detail">
    <a-spin :loading="loading">
      <a-descriptions
        :data="orderInfo"
        :column="2"
        layout="inline-horizontal"
        bordered
      >
        <template #title>
          <div class="title">
            <span>订单详情</span>
            <a-tag :color="getStatusColor(orderInfo.status)">
              {{ getStatusText(orderInfo.status) }}
            </a-tag>
          </div>
        </template>
      </a-descriptions>

      <a-card class="items-card" title="订单商品">
        <a-table :data="orderInfo.items" :pagination="false">
          <template #columns>
            <a-table-column title="商品名称" data-index="productName" />
            <a-table-column title="单价" data-index="unitPrice">
              <template #cell="{ record }">
                ¥{{ record.unitPrice }}
              </template>
            </a-table-column>
            <a-table-column title="数量" data-index="quantity" />
            <a-table-column title="小计" data-index="subtotal">
              <template #cell="{ record }">
                ¥{{ record.subtotal }}
              </template>
            </a-table-column>
          </template>
        </a-table>

        <div class="amount-info">
          <div class="amount-item">
            <span>总金额：</span>
            <span class="amount">¥{{ orderInfo.totalAmount }}</span>
          </div>
          <div class="amount-item">
            <span>优惠金额：</span>
            <span class="amount">¥{{ orderInfo.discountAmount }}</span>
          </div>
          <div class="amount-item">
            <span>实收金额：</span>
            <span class="amount">¥{{ orderInfo.actualAmount }}</span>
          </div>
        </div>
      </a-card>

      <a-card class="remark-card" title="备注信息">
        <p>{{ orderInfo.remark || '无' }}</p>
      </a-card>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrderStore } from '@/stores/order'

const props = defineProps({
  orderId: {
    type: [String, Number],
    required: true
  }
})

const orderStore = useOrderStore()
const loading = ref(false)

const orderInfo = ref({
  id: '',
  orderNumber: '',
  cashierName: '',
  status: '',
  totalAmount: 0,
  discountAmount: 0,
  actualAmount: 0,
  remark: '',
  items: []
})

const orderStatusOptions = [
  { value: 'PENDING', label: '待处理' },
  { value: 'PAID', label: '已支付' },
  { value: 'CANCELLED', label: '已取消' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'REFUNDED', label: '已退款' }
]

const getStatusColor = (status) => {
  const colors = {
    PENDING: 'orange',
    PAID: 'green',
    CANCELLED: 'gray',
    COMPLETED: 'green',
    REFUNDED: 'red'
  }
  return colors[status] || 'gray'
}

const getStatusText = (status) => {
  const option = orderStatusOptions.find(item => item.value === status)
  return option ? option.label : status
}

const fetchOrderDetail = async () => {
  loading.value = true
  try {
    await orderStore.fetchOrder(props.orderId)
    orderInfo.value = orderStore.currentOrder
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}

.title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.items-card {
  margin-top: 20px;
}

.amount-info {
  margin-top: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

.amount-item {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.amount-item:last-child {
  margin-bottom: 0;
}

.amount {
  font-weight: bold;
  color: #165DFF;
  margin-left: 10px;
}

.remark-card {
  margin-top: 20px;
}
</style> 