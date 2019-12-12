<template>
  <div>
    <el-row style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-button v-if="$menuPermission.HAS_USER_CREATE_BUTTON" type="primary" @click="createAction()">创建新用户<i class="el-icon-apple el-icon--right"></i></el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="usersData"
          border
          style="width: 100%">
          <el-table-column
            fixed
            prop="username"
            label="用户名"
            width="200">
          </el-table-column>
          <el-table-column
            prop="description"
            label="描述">
          </el-table-column>
          <el-table-column label="是否锁定" width="120" v-if="$menuPermission.HAS_USER_LOCK_BUTTON">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.lock" @change="lockAction($event, scope.row)"></el-switch>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="150">
            <template slot-scope="scope">
              <el-button v-if="$menuPermission.HAS_USER_UPDATE_BUTTON" @click="editAction(scope.row)" type="text" size="small">编辑</el-button>
              <el-button v-if="$menuPermission.HAS_USER_DELETE_BUTTON" @click="deleteAction(scope.row)" type="text" size="small">删除</el-button>
              <el-button v-if="$menuPermission.HAS_USER_ROLE_BUTTON" @click="roleAction(scope.row)" type="text" size="small">角色</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-dialog title="创建新用户" :visible.sync="createDialogFormVisible">
      <el-form :model="userForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="用户名"  prop="username">
          <el-input v-model="userForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="userForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="createUserAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="编辑新用户" :visible.sync="editDialogFormVisible">
      <el-form :model="userForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="userForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editUserAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="分配角色" :visible.sync="roleDialogFormVisible">
      <el-transfer 
        v-model="roles" 
        :titles="['待分配角色', '已分配角色']"
        :button-texts="['到左边', '到右边']"
        :data="rolesData"></el-transfer>
      <div slot="footer" class="dialog-footer">
        <el-button @click="roleDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="manageRoleAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      usersData: [],
      createDialogFormVisible: false,
      editDialogFormVisible: false,
      roleDialogFormVisible: false,
      userForm: {
        username: '',
        password: '',
        description: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入描述', trigger: 'blur' }
        ]
      },
      roles: [],
      rolesData: [],
      editUserId: 0
    }
  },
  methods: {
    createUserAction (formName) {
      this.createDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.createUserApi()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editUserAction (formName) {
      this.editDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.updateUserApi(this.editUserId)
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    manageRoleAction () {
      this.roleDialogFormVisible = false
      console.log('roles:' + JSON.stringify(this.roles))
      this.allotRolesApi(this.editUserId)
    },
    createAction () {
      this.userForm.username = ''
      this.userForm.password = ''
      this.userForm.description = ''
      this.createDialogFormVisible = true
    },
    editAction (row) {
      this.userForm.username = row.username
      this.userForm.description = row.description
      this.editDialogFormVisible = true
      console.log('edit user is:' + JSON.stringify(row))
      this.editUserId = row.id
    },
    deleteAction (row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteUserApi(row.id)
      }).catch(() => {
      })
    },
    roleAction (row) {
      this.roleDialogFormVisible = true
      this.editUserId = row.id
      this.rolesApi()
      this.rolesOfUserApi(row.id)
    },
    lockAction ($event, row) {
      console.log($event+':'+row.username)
      this.lockUserApi(row.id, $event)
    },
    usersApi: async function () {
      const res = await this.$http.get(this.$http.API.USERS_ALL)
      if (res.success) {
        this.usersData = res.data
      }
    },
    rolesApi: async function () {
      this.rolesData = []
      const res = await this.$http.get(this.$http.API.ROLES_ALL)
      if (res.success) {
        var roleList = res.data
        roleList.forEach(element => {
          var item = {
            key: element.id,
            label: element.name,
            disabled: false
          }
          this.rolesData.push(item)
        })
      }
    },
    rolesOfUserApi: async function (userId) {
      this.roles = []
      const res = await this.$http.get("/system/user/" + userId + "/roles")
      if (res.success) {
        var roles = res.data
        roles.forEach(element => {
          this.roles.push(element.id)
        })
      }
    },
    allotRolesApi: async function (userId) {
      const res = await this.$http.put("/system/user/" + userId + "/roles", this.roles)
      if (res.success) {
        this.$message({
          message: '恭喜您，分配角色成功',
          type: 'success'
        })
      }
    },
    createUserApi: async function () {
      let params = this.userForm
      const res = await this.$http.post(this.$http.API.USERS_CREATE, params)
      if (res.success) {
        this.$message({
          message: '创建成功啦',
          type: 'success'
        })
        this.usersApi()
      }
    },
    updateUserApi: async function (userId) {
      let params = this.userForm
      const res = await this.$http.put(this.$http.API.USERS_UPDATE+userId, params)
      if (res.success) {
        this.$message({
          message: '更新成功啦',
          type: 'success'
        })
        this.usersApi()
      }
    },
    deleteUserApi: async function (userId) {
      let params = this.userForm
      const res = await this.$http.delete(this.$http.API.USERS_DELETE+userId)
      if (res.success) {
        this.$message({
          message: '删除成功啦',
          type: 'success'
        })
        this.usersApi()
      }
    },
    lockUserApi: async function (userId, isLock) {
      var enable = 1
      var message = "解锁成功"
      if (isLock === true) {
        enable = 2
        var message = "锁定成功"
      }
      const res = await this.$http.put("/system/user/"+userId+"/enable", {"enabled":enable})
      if (res.success) {

        this.$message({
          message: message,
          type: 'success'
        })
        this.usersApi()
      }
    }
  },
  created() {
    this.usersApi()
  }
}
</script>

<style>

</style>