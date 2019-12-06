import Vue from 'vue'
import Router from 'vue-router'
/* TabBar 首页 */
const Home = () => import(/* webpackChunkName: "home" */ './views/homes/Home.vue')
const Financing = () => import(/* webpackChunkName: "financing" */ './views/homes/Financing.vue')
const Life = () => import(/* webpackChunkName: "life" */ './views/homes/Life.vue')
const Mine = () => import(/* webpackChunkName: "mine" */ './views/homes/Mine.vue')
/* 转账 */
const Transfer = () => import(/* webpackChunkName: "transfer" */ './views/transfer/Transfer.vue')
const TransferResult = () => import(/* webpackChunkName: "transfer" */ './views/transfer/Result.vue')
/* 关于 */
/* 商城 */
const Goods = () => import(/* webpackChunkName: "goods" */ './views/mall/Goods.vue')

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'root',
      redirect: '/main/home'
    },
    {
      path: '/main/',
      name: 'main',
      component: () => import(/* webpackChunkName: "main" */ './views/Main.vue'),
      children: [
        {
          path: 'home',
          component: Home
        },
        {
          path: 'financing',
          component: Financing
        },
        {
          path: 'life',
          component: Life
        },
        {
          path: 'mine',
          component: Mine
        }
      ]
    },
    {
      path: '/transfer',
      name: 'transfer',
      component: Transfer
    },
    {
      path: '/transferResult',
      name: 'transferResult',
      component: TransferResult
    },
    {
      path: '/goods',
      name: 'goods',
      component: Goods
    }
  ]
})
