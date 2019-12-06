import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import { NavBar, Tabbar, TabbarItem, Button } from 'vant'
Vue.use(NavBar)
Vue.use(Tabbar)
Vue.use(TabbarItem)
Vue.use(Button)

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
