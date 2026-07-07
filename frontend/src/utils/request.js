import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 120000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    // blob 响应直接返回（文件下载）
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const result = response.data
    if (result.code !== 200) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message))
    }
    return result.data
  },
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      import('../stores/user').then(({ useUserStore }) => {
        useUserStore().logout()
      }).catch(() => { /* ignore */ })
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(error)
    }
    // blob 响应的错误需要读取 JSON 内容
    if (error.response?.config?.responseType === 'blob' && error.response?.data) {
      return Promise.reject(error)
    }
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      ElMessage.error('请求超时，请检查网络后重试')
    } else if (!error.response) {
      ElMessage.error('网络连接失败，请检查后端服务是否启动')
    } else {
      ElMessage.error(error.response?.data?.message || `请求失败 (${error.response.status})`)
    }
    return Promise.reject(error)
  }
)

export default request
