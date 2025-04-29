<template>
  <div class="cashier-list">
    <a-card>
      <template #title>
        <div class="header">
          <span>收银员管理</span>
          <a-button type="primary" @click="showCreateModal">
            <template #icon><icon-plus /></template>
            新增收银员
          </a-button>
        </div>
      </template>

      <a-table
        :data="cashierStore.cashierList"
        :loading="cashierStore.loading"
        :pagination="cashierStore.pagination"
        @page-change="onPageChange"
      >
        <template #columns>
          <a-table-column title="工号" data-index="employeeId" />
          <a-table-column title="姓名" data-index="name" />
          <a-table-column title="手机号" data-index="phone" />
          <a-table-column title="状态">
            <template #cell="{ record }">
              <a-tag :color="record.status === 1 ? 'green' : 'red'">
                {{ record.status === 1 ? '在职' : '离职' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" width="200">
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" @click="showEditModal(record)">
                  <template #icon><icon-edit /></template>
                  编辑
                </a-button>
                <a-button 
                  type="text" 
                  :status="record.status === 1 ? 'danger' : 'success'"
                  @click="handleToggleStatus(record)"
                >
                  <template #icon>
                    <icon-check v-if="record.status === 0" />
                    <icon-close v-else />
                  </template>
                  {{ record.status === 1 ? '离职' : '复职' }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑收银员' : '新增收银员'"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <a-form :model="formData" layout="vertical">
        <a-form-item field="employeeId" label="工号" required>
          <a-input v-model="formData.employeeId" placeholder="请输入工号" />
        </a-form-item>
        <a-form-item field="name" label="姓名" required>
          <a-input v-model="formData.name" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item field="phone" label="手机号" required>
          <a-input v-model="formData.phone" placeholder="请输入手机号" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useCashierStore } from '@/stores/cashier'
import { Message } from '@arco-design/web-vue'
import {
  IconPlus,
  IconEdit,
  IconCheck,
  IconClose
} from '@arco-design/web-vue/es/icon'

const cashierStore = useCashierStore()

// 表格分页
const onPageChange = (page) => {
  cashierStore.fetchCashiers({ page })
}

// 模态框相关
const modalVisible = ref(false)
const isEdit = ref(false)
const formData = reactive({
  id: undefined,
  employeeId: '',
  name: '',
  phone: ''
})

const showCreateModal = () => {
  isEdit.value = false
  formData.id = undefined
  formData.employeeId = ''
  formData.name = ''
  formData.phone = ''
  modalVisible.value = true
}

const showEditModal = (record) => {
  isEdit.value = true
  formData.id = record.id
  formData.employeeId = record.employeeId
  formData.name = record.name
  formData.phone = record.phone
  modalVisible.value = true
}

const handleModalOk = async () => {
  try {
    if (isEdit.value) {
      await cashierStore.updateCashier(formData)
      Message.success('更新成功')
    } else {
      await cashierStore.createCashier(formData)
      Message.success('创建成功')
    }
    modalVisible.value = false
    cashierStore.fetchCashiers()
  } catch (error) {
    Message.error(error.message)
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
}

// 切换状态
const handleToggleStatus = async (record) => {
  try {
    await cashierStore.toggleCashierStatus(record.id)
    Message.success('操作成功')
    cashierStore.fetchCashiers()
  } catch (error) {
    Message.error(error.message)
  }
}

// 初始化
cashierStore.fetchCashiers()
</script>

<style scoped>
.cashier-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 