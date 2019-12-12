'use strict'

import axios from 'axios'
// import qs from 'qs'
import config from './config'
import api from './api'
import { Loading, Message } from 'element-ui'

axios.interceptors.request.use(config => {
  showLoading()
  console.log('axios request:' + JSON.stringify(config))
  return config
}, error => {
  closeLoading()
  return Promise.reject(error)
})

axios.interceptors.response.use(response => {
  console.log('axios response:' + JSON.stringify(response))
  closeLoading()
  return response
}, error => {
  closeLoading()
  return Promise.resolve(error.response)
})

function checkStatus (response) {
  var msg = '未知错误'
  if (response) {
    switch (response.status) {
      case 200:
      {
        if (response.data.code !== '000000') {
          Message.error(response.data.message)
        }
        return response.data
      }
      case 400: msg = '请求错误'
        break
      case 401: msg = '未授权，请登录'
        break
      case 403: msg = '拒绝访问'
        break
      case 404: msg = `请求地址出错: ${response.config.url}`
        break
      case 408: msg = '请求超时'
        break
      case 500: msg = '服务器内部错误'
        break
      case 501: msg = '服务未实现'
        break
      case 502: msg = '网关错误'
        break
      case 503: msg = '服务不可用'
        break
      case 504: msg = '网关超时'
        break
      case 505: msg = 'HTTP版本不受支持'
        break
      default:
    }
    Message.error(response.status + ' ' + msg)
  } else {
    msg = 'response 不存在'
  }
  return {
    code: 999999,
    message: msg,
    isSuccess: false,
    isFail: true,
    data: {}
  }
}

function showLoading () {
  Loading.service({ fullscreen: true, text: '正在加载' })
}

function closeLoading () {
  let loadingInstance = Loading.service({ fullscreen: true, text: '正在加载' })
  loadingInstance.close()
}

export default {
  httpConfig: config,
  API: api,
  async post (url, data) {
    const response = await axios({
      method: 'post',
      baseURL: config.baseURL,
      url,
      data: data,
      timeout: config.timeout,
      headers: config.headers
    })
    return checkStatus(response)
  },
  async upload (url, formdata) {
    const response = await axios({
      method: 'post',
      baseURL: config.baseURL,
      url,
      data: formdata,
      timeout: config.timeout,
      headers: config.multipartHeaders
    })
    return checkStatus(response)
  },
  async get (url, params) {
    const response = await axios({
      method: 'get',
      baseURL: config.baseURL,
      url,
      params,
      timeout: config.timeout,
      headers: config.headers
    })
    return checkStatus(response)
  },
  async delete (url, params) {
    const response = await axios({
      method: 'delete',
      baseURL: config.baseURL,
      url,
      params,
      timeout: config.timeout,
      headers: config.headers
    })
    return checkStatus(response)
  },
  async put (url, data) {
    const response = await axios({
      method: 'put',
      baseURL: config.baseURL,
      url,
      data,
      timeout: config.timeout,
      headers: config.headers
    })
    return checkStatus(response)
  }
}
