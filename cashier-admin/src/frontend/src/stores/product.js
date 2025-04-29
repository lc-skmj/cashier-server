import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProducts, createProduct, updateProduct } from '@/api/product'

export const useProductStore = defineStore('product', () => {
  const productList = ref([])
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 10,
    total: 0
  })

  const fetchProducts = async (params = {}) => {
    loading.value = true
    try {
      const { data } = await getProducts({
        ...params,
        page: pagination.value.current,
        size: pagination.value.pageSize
      })
      productList.value = data.records
      pagination.value.total = data.total
    } finally {
      loading.value = false
    }
  }

  const createProduct = async (productData) => {
    loading.value = true
    try {
      await createProduct(productData)
    } finally {
      loading.value = false
    }
  }

  const updateProduct = async (productData) => {
    loading.value = true
    try {
      await updateProduct(productData)
    } finally {
      loading.value = false
    }
  }

  return {
    productList,
    loading,
    pagination,
    fetchProducts,
    createProduct,
    updateProduct
  }
}) 