import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

export function useECharts() {
  const chartRef = ref(null)
  let chart = null

  const initChart = () => {
    if (chartRef.value) {
      chart = echarts.init(chartRef.value)
      window.addEventListener('resize', handleResize)
    }
  }

  const handleResize = () => {
    chart?.resize()
  }

  const setOption = (option) => {
    if (chart) {
      chart.setOption(option)
    }
  }

  onMounted(() => {
    initChart()
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    chart?.dispose()
  })

  return {
    chartRef,
    setOption
  }
} 