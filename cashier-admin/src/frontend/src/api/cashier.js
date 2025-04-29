import request from '@/utils/request'

export function getCashiers(params) {
  return request({
    url: '/api/cashiers',
    method: 'get',
    params
  })
}

export function createCashier(data) {
  return request({
    url: '/api/cashiers',
    method: 'post',
    data
  })
}

export function updateCashier(data) {
  return request({
    url: `/api/cashiers/${data.id}`,
    method: 'put',
    data
  })
}

export function toggleCashierStatus(id) {
  return request({
    url: `/api/cashiers/${id}/status`,
    method: 'put'
  })
} 