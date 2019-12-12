import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import filter from './filter'
import Network from './utils/network/network'
import MenuPermission from './utils/authentication/authentication'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)
Vue.use(Network)
Vue.use(MenuPermission)
Vue.config.productionTip = false

new Vue({
  router,
  store,
  filter,
  render: h => h(App)
}).$mount('#app')
