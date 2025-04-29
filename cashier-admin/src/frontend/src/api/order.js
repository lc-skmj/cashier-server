import request from './request'

// 获取订单列表
export function getOrders(params) {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrder(id) {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/orders/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'put'
  })
}

// 完成订单
export function completeOrder(id) {
  return request({
    url: `/orders/${id}/complete`,
    method: 'put'
  })
}

// 获取订单统计
export function getOrderStatistics(startDate, endDate) {
  return request({
    url: '/orders/statistics',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 获取每日订单统计
export function getDailyStatistics(startDate, endDate) {
  return request({
    url: '/orders/daily-statistics',
    method: 'get',
    params: { startDate, endDate }
  })
} 