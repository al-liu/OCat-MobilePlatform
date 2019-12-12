import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import Login from './views/Login.vue'
import Welcome from './views/Welcome.vue'
import Users from './views/system/user/Users.vue'
import Roles from './views/system/role/Roles.vue'
import Permissions from './views/system/permission/Permissions.vue'
import Menus from './views/system/menu/Menus.vue'
import Applications from './views/application/Applications.vue'
import Resources from './views/package/Resources.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'login',
      component: Login
    },
    {
      path: '/home',
      component: Home,
      redirect: '/welcome',
      children: [
        {
          path: '/welcome',
          component: Welcome,
          alias: 'welcome'
        },
        {
          path: '/users',
          component: Users,
          alias: 'users'
        },
        {
          path: '/roles',
          component: Roles,
          alias: 'roles'
        },
        {
          path: '/permissions',
          component: Permissions,
          alias: 'permissions'
        },
        {
          path: '/menus',
          component: Menus,
          alias: 'menus'
        },
        {
          path: '/applications',
          component: Applications,
          alias: 'applications'
        },
        {
          path: '/resources',
          component: Resources,
          alias: 'resources',
          name: 'resources'
        }
      ]
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
    }
  ]
})
