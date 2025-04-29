import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCashiers, createCashier, updateCashier, toggleCashierStatus } from '@/api/cashier'

export const useCashierStore = defineStore('cashier', () => {
  const cashierList = ref([])
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 10,
    total: 0
  })

  const fetchCashiers = async (params = {}) => {
    loading.value = true
    try {
      const { data } = await getCashiers({
        page: pagination.value.current,
        size: pagination.value.pageSize,
        ...params
      })
      cashierList.value = data.records
      pagination.value.total = data.total
    } finally {
      loading.value = false
    }
  }

  const createCashierAction = async (data) => {
    await createCashier(data)
  }

  const updateCashierAction = async (data) => {
    await updateCashier(data)
  }

  const toggleCashierStatusAction = async (id) => {
    await toggleCashierStatus(id)
  }

  return {
    cashierList,
    loading,
    pagination,
    fetchCashiers,
    createCashier: createCashierAction,
    updateCashier: updateCashierAction,
    toggleCashierStatus: toggleCashierStatusAction
  }
}) 