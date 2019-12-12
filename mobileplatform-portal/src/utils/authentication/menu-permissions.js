import store from '../../store'
let USER_CREATE_BUTTON = '12'
let USER_UPDATE_BUTTON = '8'
let USER_DELETE_BUTTON = '9'
let USER_LOCK_BUTTON = '11'
let USER_ROLE_BUTTON = '10'
// 角色
let ROLE_CREATE_BUTTON = '13'
let ROLE_UPDATE_BUTTON = '14'
let ROLE_DELETE_BUTTON = '15'
let ROLE_PERMISSION_BUTTON = '16'
let ROLE_MENU_BUTTON = '17'
// 权限
let PERMISSION_CREATE_BUTTON = '18'
let PERMISSION_UPDATE_BUTTON = '19'
let PERMISSION_DELETE_BUTTON = '20'
// 菜单
let MENU_CREATE_BUTTON = '21'
let MENU_UPDATE_BUTTON = '22'
let MENU_DELETE_BUTTON = '23'
// 应用
let APP_CREATE_BUTTON = '24'
let APP_UPDATE_BUTTON = '25'
let APP_DELETE_BUTTON = '26'
let APP_PACKAGE_BUTTON = '27'
// 包管理
let PACKAGE_UPLOAD_BUTTON = '28'
let PACKAGE_DELETE_BUTTON = '29'
let PACKAGE_RELEASE_BUTTON = '30'
let PACKAGE_PATCHS_BUTTON = '31'

export default {
  // 用户
  HAS_USER_CREATE_BUTTON: true,
  HAS_USER_UPDATE_BUTTON: true,
  HAS_USER_DELETE_BUTTON: true,
  HAS_USER_LOCK_BUTTON: true,
  HAS_USER_ROLE_BUTTON: true,
  // 角色
  HAS_ROLE_CREATE_BUTTON: true,
  HAS_ROLE_UPDATE_BUTTON: true,
  HAS_ROLE_DELETE_BUTTON: true,
  HAS_ROLE_PERMISSION_BUTTON: true,
  HAS_ROLE_MENU_BUTTON: true,
  // 权限
  HAS_PERMISSION_CREATE_BUTTON: true,
  HAS_PERMISSION_UPDATE_BUTTON: true,
  HAS_PERMISSION_DELETE_BUTTON: true,
  // 菜单
  HAS_MENU_CREATE_BUTTON: true,
  HAS_MENU_UPDATE_BUTTON: true,
  HAS_MENU_DELETE_BUTTON: true,
  // 应用
  HAS_APP_CREATE_BUTTON: true,
  HAS_APP_UPDATE_BUTTON: true,
  HAS_APP_DELETE_BUTTON: true,
  HAS_APP_PACKAGE_BUTTON: true,
  // 包管理
  HAS_PACKAGE_UPLOAD_BUTTON: true,
  HAS_PACKAGE_DELETE_BUTTON: true,
  HAS_PACKAGE_RELEASE_BUTTON: true,
  HAS_PACKAGE_PATCHS_BUTTON: true,

  // 需要登录后调用，否则返回值都是 false
  checkPermissions () {
    // 用户
    this.HAS_USER_CREATE_BUTTON = store.getters.hasButtonById(USER_CREATE_BUTTON)
    this.HAS_USER_UPDATE_BUTTON = store.getters.hasButtonById(USER_UPDATE_BUTTON)
    this.HAS_USER_DELETE_BUTTON = store.getters.hasButtonById(USER_DELETE_BUTTON)
    this.HAS_USER_LOCK_BUTTON = store.getters.hasButtonById(USER_LOCK_BUTTON)
    this.HAS_USER_ROLE_BUTTON = store.getters.hasButtonById(USER_ROLE_BUTTON)
    // 角色
    this.HAS_ROLE_CREATE_BUTTON = store.getters.hasButtonById(ROLE_CREATE_BUTTON)
    this.HAS_ROLE_UPDATE_BUTTON = store.getters.hasButtonById(ROLE_UPDATE_BUTTON)
    this.HAS_ROLE_DELETE_BUTTON = store.getters.hasButtonById(ROLE_DELETE_BUTTON)
    this.HAS_ROLE_PERMISSION_BUTTON = store.getters.hasButtonById(ROLE_PERMISSION_BUTTON)
    this.HAS_ROLE_MENU_BUTTON = store.getters.hasButtonById(ROLE_MENU_BUTTON)
    // 权限
    this.HAS_PERMISSION_CREATE_BUTTON = store.getters.hasButtonById(PERMISSION_CREATE_BUTTON)
    this.HAS_PERMISSION_UPDATE_BUTTON = store.getters.hasButtonById(PERMISSION_UPDATE_BUTTON)
    this.HAS_PERMISSION_DELETE_BUTTON = store.getters.hasButtonById(PERMISSION_DELETE_BUTTON)
    // 菜单
    this.HAS_MENU_CREATE_BUTTON = store.getters.hasButtonById(MENU_CREATE_BUTTON)
    this.HAS_MENU_UPDATE_BUTTON = store.getters.hasButtonById(MENU_UPDATE_BUTTON)
    this.HAS_MENU_DELETE_BUTTON = store.getters.hasButtonById(MENU_DELETE_BUTTON)
    // 应用
    this.HAS_APP_CREATE_BUTTON = store.getters.hasButtonById(APP_CREATE_BUTTON)
    this.HAS_APP_UPDATE_BUTTON = store.getters.hasButtonById(APP_UPDATE_BUTTON)
    this.HAS_APP_DELETE_BUTTON = store.getters.hasButtonById(APP_DELETE_BUTTON)
    this.HAS_APP_PACKAGE_BUTTON = store.getters.hasButtonById(APP_PACKAGE_BUTTON)
    // 包管理
    this.HAS_PACKAGE_UPLOAD_BUTTON = store.getters.hasButtonById(PACKAGE_UPLOAD_BUTTON)
    this.HAS_PACKAGE_DELETE_BUTTON = store.getters.hasButtonById(PACKAGE_DELETE_BUTTON)
    this.HAS_PACKAGE_RELEASE_BUTTON = store.getters.hasButtonById(PACKAGE_RELEASE_BUTTON)
    this.HAS_PACKAGE_PATCHS_BUTTON = store.getters.hasButtonById(PACKAGE_PATCHS_BUTTON)
  }
}
