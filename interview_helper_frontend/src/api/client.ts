import axios from 'axios'

const client = axios.create({
  baseURL: '/api',
  timeout: 120000,
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

client.interceptors.response.use(
  (res) => {
    // 后端业务错误以 HTTP 200 + code != 200 返回，这里统一转为异常，
    // 避免页面拿到 null 数据后抛出"不知名"的 JS 报错
    const code = res.data?.code
    if (code !== undefined && code !== 200) {
      return Promise.reject(new Error(res.data?.msg || '请求失败，请稍后重试'))
    }
    return res
  },
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    const msg = err.response?.data?.msg || '网络异常，请稍后重试'
    return Promise.reject(new Error(msg))
  },
)

export default client
