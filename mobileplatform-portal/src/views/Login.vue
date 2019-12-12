<template>
  <div>
    <el-row>
        <el-col :span="9"><div class="placeholder_hiden">1</div></el-col>
        <el-col :span="6">
            <div style="margin-top: 100px;">
                <h3 style="text-align:center;">OCat移动平台 Beta 0.0.1</h3>
                <el-form :rules="rules" ref="ruleForm" :model="ruleForm">
                    <el-form-item prop="username">
                        <el-input prefix-icon='el-icon-user' v-model="ruleForm.username" placeholder="请输入用户名" clearable></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input prefix-icon='el-icon-view' v-model="ruleForm.password" placeholder="请输入密码" type="password" @change="loginAction('ruleForm')" clearable></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loginAction('ruleForm')" size="medium" style="width: 100%">登录</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-col>
        <el-col :span="9"><div class="placeholder_hiden">1</div></el-col>
    </el-row>
  </div>
</template>

<script>

export default {
  data () {
    return {
      ruleForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    if (localStorage.username) {
      this.ruleForm.username = localStorage.username
    }
  },
  methods: {
    loginAction (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loginApi()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    loginApi: async function () {
      let params = this.ruleForm
      const res = await this.$http.post(this.$http.API.LOGIN, params)
      if (res.success === true) {
        if (res.data.enabled === 2) {
          this.$message({
            message: '用户被锁定',
            type: 'warning'
          })
        } else {
          this.$message({
            message: '登录成功啦',
            type: 'success'
          })
          localStorage.username = this.ruleForm.username
          this.$store.commit('setUserInfo', res.data)
          console.log('localstore username:' + localStorage.username)
          console.log('查看的登录信息：' + this.$store.getters.loginInfo)
          console.log('是否登录：' + this.$store.getters.isLogin)
          this.$menuPermission.checkPermissions()
          console.log('是否存在离线包管理这个菜单1：'+this.$store.getters.hasButtonById('12'))
          console.log('是否存在离线包管理这个菜单2：'+this.$menuPermission.HAS_USER_CREATE_BUTTON)
          this.$router.push('/home')
        }
      }
    }
  }
}
</script>

<style scoped>
.placeholder_hiden {
  visibility: hidden;
}
</style>
