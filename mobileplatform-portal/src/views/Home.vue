<template>
  <div class="home">
    <el-container>
      <el-header>
        <el-row type="flex" justify="space-between" style="height:100%; border-bottom:solid 1px #e6e6e6;">
          <el-col :span="12">
            <div style="text-align:left; line-height: 60px;">
              离线包移动平台
            </div>
          </el-col>
          <el-col :span="12">
            <div style="text-align:right; line-height: 60px;">
              <el-dropdown>
                <span class="el-dropdown-link" style="margin-right: 15px;">
                  {{username}}<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item >
                    <span @click="logoutAction()">登出</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </el-col>
        </el-row>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu :default-openeds="['1', '2']">
            <el-submenu :index="index+1+''" :key="index" v-for="(item, index) in menuList">
              <template slot="title">
                <i :class="item.icon"></i>
                <span>{{item.name}}</span>
              </template>
              <router-link :to="subItem.href" v-for="(subItem, subIndex) in item.children" :key="subIndex">
                <el-menu-item :index="(index+1)+'-'+(subIndex+1)">
                  <template slot="title">
                    <i :class="subItem.icon"></i>
                    <span>{{subItem.name}}</span>
                  </template>
                </el-menu-item>
              </router-link>
            </el-submenu>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>

export default {
  name: 'home',
  data() {
    return {
      menuList:[]
    }
  },
  created() {
    this.menuList = this.$store.getters.loginInfo.menuList
  },
  methods: {
    logoutAction () {
      this.logoutApi()
    },
    logoutApi: async function () {
      const res = await this.$http.get(this.$http.API.LOGOUT)
      if (res.success) {
        this.$message(
          '退出成功啦'
        )
        this.$store.commit('clearUserInfo')
        console.log('是否登录：' + this.$store.getters.isLogin)
        this.$router.push('/')
      }
    }
  },
  computed: {
    isLogin () {
      return this.$store.getters.isLogin
    },
    username () {
      return this.$store.getters.loginInfo.username
    }
  }
}
</script>

<style scoped>
a {
    text-decoration:none
}
</style>
