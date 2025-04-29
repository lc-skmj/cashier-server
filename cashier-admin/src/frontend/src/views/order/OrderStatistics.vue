<template>
  <div class="order-statistics">
    <a-card>
      <template #title>
        <div class="card-header">
          <span>订单统计</span>
          <a-range-picker
            v-model="dateRange"
            @change="handleDateChange"
            style="width: 300px"
          />
        </div>
      </template>

      <!-- 统计卡片 -->
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card hoverable>
            <template #title>订单总数</template>
            <div class="statistic-value">{{ statistics.totalOrders }}</div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card hoverable>
            <template #title>总金额</template>
            <div class="statistic-value">¥{{ statistics.totalAmount }}</div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card hoverable>
            <template #title>实收金额</template>
            <div class="statistic-value">¥{{ statistics.actualAmount }}</div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card hoverable>
            <template #title>优惠金额</template>
            <div class="statistic-value">¥{{ statistics.discountAmount }}</div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 每日统计图表 -->
      <a-card class="chart-card">
        <template #title>每日订单统计</template>
        <div class="chart-container">
          <a-spin :loading="loading">
            <div ref="chartRef" style="width: 100%; height: 400px"></div>
          </a-spin>
        </div>
      </a-card>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useECharts } from '@/hooks/useECharts'
import { getOrderStatistics, getDailyStatistics } from '@/api/order'
import dayjs from 'dayjs'

const loading = ref(false)
const dateRange = ref([dayjs().subtract(7, 'day'), dayjs()])
const statistics = ref({
  totalOrders: 0,
  totalAmount: 0,
  actualAmount: 0,
  discountAmount: 0
})

const { chartRef, setOption } = useECharts()

const fetchStatistics = async () => {
  loading.value = true
  try {
    const [startTime, endTime] = dateRange.value
    const [statisticsRes, dailyRes] = await Promise.all([
      getOrderStatistics(startTime.format('YYYY-MM-DD'), endTime.format('YYYY-MM-DD')),
      getDailyStatistics(startTime.format('YYYY-MM-DD'), endTime.format('YYYY-MM-DD'))
    ])
    
    statistics.value = statisticsRes.data
    updateChart(dailyRes.data)
  } catch (error) {
    Message.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

const updateChart = (data) => {
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['订单数', '总金额', '实收金额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数'
      },
      {
        type: 'value',
        name: '金额',
        position: 'right'
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'bar',
        data: data.map(item => item.orderCount)
      },
      {
        name: '总金额',
        type: 'line',
        yAxisIndex: 1,
        data: data.map(item => item.totalAmount)
      },
      {
        name: '实收金额',
        type: 'line',
        yAxisIndex: 1,
        data: data.map(item => item.actualAmount)
      }
    ]
  }
  setOption(option)
}

const handleDateChange = () => {
  fetchStatistics()
}

onMounted(() => {
  fetchStatistics()
})

onBeforeUnmount(() => {
  const chart = chartRef.value?.__echarts__
  if (chart) {
    chart.dispose()
  }
})
</script>

<style scoped>
.order-statistics {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  color: #165DFF;
  text-align: center;
  margin-top: 8px;
}

.chart-card {
  margin-top: 20px;
}

.chart-container {
  position: relative;
  height: 400px;
}
</style> 