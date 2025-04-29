import axios from 'axios'
import { Message } from '@arco-design/web-vue'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    // 这里可以添加token等认证信息
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    Message.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default request 