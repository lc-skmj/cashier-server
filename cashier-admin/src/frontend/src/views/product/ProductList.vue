<template>
  <div class="product-list">
    <a-card>
      <template #title>
        <div class="card-header">
          <span>商品管理</span>
          <a-button type="primary" @click="handleCreate">新增商品</a-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <a-form :model="searchForm" layout="inline" class="search-form">
        <a-form-item field="name" label="商品名称">
          <a-input v-model="searchForm.name" placeholder="请输入商品名称" allow-clear />
        </a-form-item>
        <a-form-item field="category" label="商品分类">
          <a-select v-model="searchForm.category" placeholder="请选择分类" allow-clear>
            <a-option
              v-for="item in categoryOptions"
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

      <!-- 商品列表 -->
      <a-table
        :data="productList"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      >
        <template #columns>
          <a-table-column title="商品名称" data-index="name" />
          <a-table-column title="分类" data-index="category">
            <template #cell="{ record }">
              {{ getCategoryText(record.category) }}
            </template>
          </a-table-column>
          <a-table-column title="单价" data-index="price">
            <template #cell="{ record }">
              ¥{{ record.price }}
            </template>
          </a-table-column>
          <a-table-column title="库存" data-index="stock" />
          <a-table-column title="状态" data-index="status">
            <template #cell="{ record }">
              <a-tag :color="record.status === 1 ? 'green' : 'red'">
                {{ record.status === 1 ? '上架' : '下架' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="创建时间" data-index="createTime" width="180" />
          <a-table-column title="操作" fixed="right" width="200">
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" @click="handleEdit(record)">编辑</a-button>
                <a-button
                  type="text"
                  :status="record.status === 1 ? 'danger' : 'success'"
                  @click="handleToggleStatus(record)"
                >
                  {{ record.status === 1 ? '下架' : '上架' }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 商品表单对话框 -->
    <a-modal
      v-model:visible="dialogVisible"
      :title="dialogType === 'create' ? '新增商品' : '编辑商品'"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="vertical"
      >
        <a-form-item field="name" label="商品名称">
          <a-input v-model="form.name" placeholder="请输入商品名称" />
        </a-form-item>
        <a-form-item field="category" label="商品分类">
          <a-select v-model="form.category" placeholder="请选择分类">
            <a-option
              v-for="item in categoryOptions"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            />
          </a-select>
        </a-form-item>
        <a-form-item field="price" label="单价">
          <a-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            placeholder="请输入单价"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item field="stock" label="库存">
          <a-input-number
            v-model="form.stock"
            :min="0"
            placeholder="请输入库存"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item field="description" label="商品描述">
          <a-textarea
            v-model="form.description"
            placeholder="请输入商品描述"
            :max-length="200"
            show-word-limit
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { useProductStore } from '@/stores/product'
import { toggleProductStatus } from '@/api/product'

const productStore = useProductStore()
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('create')
const formRef = ref(null)

const searchForm = reactive({
  name: '',
  category: ''
})

const form = reactive({
  id: '',
  name: '',
  category: '',
  price: 0,
  stock: 0,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称' }],
  category: [{ required: true, message: '请选择商品分类' }],
  price: [{ required: true, message: '请输入商品单价' }],
  stock: [{ required: true, message: '请输入商品库存' }]
}

const categoryOptions = [
  { value: 'FOOD', label: '食品' },
  { value: 'DRINK', label: '饮料' },
  { value: 'DAILY', label: '日用品' },
  { value: 'OTHER', label: '其他' }
]

const getCategoryText = (category) => {
  const option = categoryOptions.find(item => item.value === category)
  return option ? option.label : category
}

const fetchProducts = async () => {
  loading.value = true
  try {
    await productStore.fetchProducts(searchForm)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  productStore.pagination.current = 1
  fetchProducts()
}

const handlePageChange = (current) => {
  productStore.pagination.current = current
  fetchProducts()
}

const handlePageSizeChange = (pageSize) => {
  productStore.pagination.pageSize = pageSize
  productStore.pagination.current = 1
  fetchProducts()
}

const handleCreate = () => {
  dialogType.value = 'create'
  dialogVisible.value = true
  Object.assign(form, {
    id: '',
    name: '',
    category: '',
    price: 0,
    stock: 0,
    description: ''
  })
}

const handleEdit = (record) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, record)
}

const handleSubmit = async () => {
  const result = await formRef.value.validate()
  if (result) {
    return
  }

  try {
    if (dialogType.value === 'create') {
      await productStore.createProduct(form)
      Message.success('创建成功')
    } else {
      await productStore.updateProduct(form)
      Message.success('更新成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch (error) {
    Message.error('操作失败')
  }
}

const handleCancel = () => {
  formRef.value.resetFields()
  dialogVisible.value = false
}

const handleToggleStatus = async (record) => {
  try {
    await toggleProductStatus(record.id)
    Message.success('操作成功')
    fetchProducts()
  } catch (error) {
    Message.error('操作失败')
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-list {
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