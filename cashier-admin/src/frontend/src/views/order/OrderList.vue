<template>
  <div class="order-list">
    <a-card>
      <template #title>
        <div class="card-header">
          <span>订单管理</span>
          <a-button type="primary" @click="handleCreate">新建订单</a-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <a-form :model="searchForm" layout="inline" class="search-form">
        <a-form-item field="cashierId" label="收银员">
          <a-input v-model="searchForm.cashierId" placeholder="收银员ID" allow-clear />
        </a-form-item>
        <a-form-item field="status" label="订单状态">
          <a-select v-model="searchForm.status" placeholder="订单状态" allow-clear>
            <a-option
              v-for="item in orderStatusOptions"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            />
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">搜索</a-button>
        </a-form-item>
      </a-form>

      <!-- 订单列表 -->
      <a-table
        :data="orderList"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      >
        <template #columns>
          <a-table-column title="订单号" data-index="orderNumber" width="180" />
          <a-table-column title="收银员" data-index="cashierName" width="120" />
          <a-table-column title="总金额" data-index="totalAmount" width="120">
            <template #cell="{ record }">
              ¥{{ record.totalAmount }}
            </template>
          </a-table-column>
          <a-table-column title="实收金额" data-index="actualAmount" width="120">
            <template #cell="{ record }">
              ¥{{ record.actualAmount }}
            </template>
          </a-table-column>
          <a-table-column title="状态" data-index="status" width="120">
            <template #cell="{ record }">
              <a-tag :color="getStatusColor(record.status)">
                {{ getStatusText(record.status) }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="创建时间" data-index="createTime" width="180" />
          <a-table-column title="操作" fixed="right" width="200">
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" @click="handleView(record)">查看</a-button>
                <a-button
                  type="text"
                  status="success"
                  v-if="record.status === 'PENDING'"
                  @click="handleComplete(record)"
                >
                  完成
                </a-button>
                <a-button
                  type="text"
                  status="danger"
                  v-if="record.status === 'PENDING'"
                  @click="handleCancel(record)"
                >
                  取消
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 订单详情对话框 -->
    <a-modal
      v-model:visible="dialogVisible"
      title="订单详情"
      width="60%"
      :footer="null"
    >
      <order-detail :order-id="currentOrderId" v-if="dialogVisible" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Message, Modal } from '@arco-design/web-vue'
import OrderDetail from './OrderDetail.vue'
import { useOrderStore } from '@/stores/order'
import { completeOrder, cancelOrder } from '@/api/order'

const router = useRouter()
const orderStore = useOrderStore()

const loading = ref(false)
const dialogVisible = ref(false)
const currentOrderId = ref(null)

const searchForm = reactive({
  cashierId: '',
  status: ''
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

const fetchOrders = async () => {
  loading.value = true
  try {
    await orderStore.fetchOrders(searchForm)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  orderStore.pagination.current = 1
  fetchOrders()
}

const handlePageChange = (current) => {
  orderStore.pagination.current = current
  fetchOrders()
}

const handlePageSizeChange = (pageSize) => {
  orderStore.pagination.pageSize = pageSize
  orderStore.pagination.current = 1
  fetchOrders()
}

const handleCreate = () => {
  router.push('/order/create')
}

const handleView = (row) => {
  currentOrderId.value = row.id
  dialogVisible.value = true
}

const handleComplete = async (row) => {
  try {
    await Modal.confirm({
      title: '提示',
      content: '确认完成该订单吗？',
      okText: '确认',
      cancelText: '取消'
    })
    await completeOrder(row.id)
    Message.success('订单已完成')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      Message.error('操作失败')
    }
  }
}

const handleCancel = async (row) => {
  try {
    await Modal.confirm({
      title: '提示',
      content: '确认取消该订单吗？',
      okText: '确认',
      cancelText: '取消'
    })
    await cancelOrder(row.id)
    Message.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      Message.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style> 