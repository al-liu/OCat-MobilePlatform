import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 登录信息
    userInfo: {}
  },
  mutations: {
    // 设置登录信息
    setUserInfo (state, info) {
      state.userInfo = info
    },
    // 清除登录信息
    clearUserInfo (state) {
      state.userInfo = {}
    }
  },
  getters: {
    // 登录状态
    isLogin: state => {
      return state.userInfo !== null && JSON.stringify(state.userInfo) !== '{}'
    },
    loginInfo: state => {
      return state.userInfo
    },
    // 通过 menu id 查找该用户是否有这个菜单
    hasButtonById: (state) => (id) => {
      return state.userInfo.buttonList.find(menu => menu.id === id) !== undefined
    }
  },
  actions: {

  }
})
