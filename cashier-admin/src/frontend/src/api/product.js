import request from './request'

export function getProducts(params) {
  return request({
    url: '/products',
    method: 'get',
    params
  })
}

export function getProduct(id) {
  return request({
    url: `/products/${id}`,
    method: 'get'
  })
}

export function createProduct(data) {
  return request({
    url: '/products',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: `/products/${data.id}`,
    method: 'put',
    data
  })
}

export function toggleProductStatus(id) {
  return request({
    url: `/products/${id}/status`,
    method: 'put'
  })
} 