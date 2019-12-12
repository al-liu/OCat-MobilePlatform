import http from './http'
export default {
  install (Vue, options = {}) {
    Vue.prototype.$http = http
  }
}
