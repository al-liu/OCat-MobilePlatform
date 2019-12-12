export default {
  // 基础url前缀
  baseURL: 'http://localhost:8082/api',
  // 请求头信息
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'X-Requested-With': 'XMLHttpRequest'
  },
  multipartHeaders: {
    'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': 'multipart/form-data'
  },
  // 参数
  data: {},
  // 设置超时时间
  timeout: 10000,
  // 携带凭证
  withCredentials: false,
  // 返回数据类型
  responseType: 'json'
}
