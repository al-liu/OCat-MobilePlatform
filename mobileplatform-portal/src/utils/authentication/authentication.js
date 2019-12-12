import permissions from './menu-permissions'
export default {
  install (Vue, options = {}) {
    Vue.prototype.$menuPermission = permissions
  }
}
