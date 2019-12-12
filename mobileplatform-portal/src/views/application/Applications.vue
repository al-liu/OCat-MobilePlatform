<template>
  <div>
    <el-row style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-button v-if="$menuPermission.HAS_APP_CREATE_BUTTON" type="primary" @click="createAction()">创建新应用<i class="el-icon-apple el-icon--right"></i></el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="appsData"
          border
          style="width: 100%">
          <el-table-column
            fixed
            prop="name"
            label="应用名称"
            width="200">
          </el-table-column>
          <el-table-column
            fixed
            prop="appId"
            label="AppId"
            width="200">
          </el-table-column>
          <el-table-column
            fixed
            prop="appSecret"
            label="AppSecret"
            width="200">
          </el-table-column>
          <el-table-column
            prop="description"
            label="应用描述">
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="200">
            <template slot-scope="scope">
              <el-button v-if="$menuPermission.HAS_APP_UPDATE_BUTTON" @click="editAction(scope.row)" type="text" size="small">编辑</el-button>
              <el-button v-if="$menuPermission.HAS_APP_DELETE_BUTTON" @click="deleteAction(scope.row)" type="text" size="small">删除</el-button>
              <el-button v-if="$menuPermission.HAS_APP_PACKAGE_BUTTON" @click="gotoResourceAction(scope.row)" type="text" size="small">离线包</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <!-- 创建新应用 -->
    <el-dialog title="创建新权限" :visible.sync="createDialogFormVisible">
      <el-form :model="appForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="应用名称"  prop="name">
          <el-input v-model="appForm.name"></el-input>
        </el-form-item>
        <el-form-item label="应用描述" prop="description">
          <el-input v-model="appForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="createAppAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 编辑新应用 -->
    <el-dialog title="编辑新应用" :visible.sync="editDialogFormVisible">
      <el-form :model="appForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="应用名称"  prop="name">
          <el-input v-model="appForm.name"></el-input>
        </el-form-item>
        <el-form-item label="应用描述" prop="description">
          <el-input v-model="appForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editAppAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      appsData: [],
      createDialogFormVisible: false,
      editDialogFormVisible: false,
      tapAppId: 0,
      appForm: {
        name: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入描述', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    createAction () {
      this.appForm.name = ''
      this.appForm.appId = ''
      this.appForm.appSecret = ''
      this.appForm.description = ''
      this.createDialogFormVisible = true
    },
    editAction (row) {
      this.appForm.name = row.name
      this.appForm.appId = row.appId
      this.appForm.appSecret = row.appSecret
      this.appForm.description = row.description
      this.editDialogFormVisible = true
      this.tapAppId = row.id
    },
    gotoResourceAction (row) {
      this.$router.push({ name: 'resources', query: { appInfo: row } })
    },
    deleteAction (row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteAppApi(row.id)
      }).catch(() => {
      })
    },
    createAppAction (formName) {
      this.createDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.createAppApi()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editAppAction (formName) {
      this.editDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.updateAppApi(this.tapAppId)
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    appsApi: async function () {
      const res = await this.$http.get(this.$http.API.APPS_ALL)
      if (res.success) {
        this.appsData = res.data
      }
    },
    createAppApi: async function () {
      let params = this.appForm
      const res = await this.$http.post(this.$http.API.APPS_CREATE, params)
      if (res.success) {
        this.$message({
          message: '创建成功啦',
          type: 'success'
        })
        this.appsApi()
      }
    },
    updateAppApi: async function (id) {
      let params = this.appForm
      const res = await this.$http.put(this.$http.API.APPS_UPDATE + id, params)
      if (res.success) {
        this.$message({
          message: '更新成功啦',
          type: 'success'
        })
        this.appsApi()
      }
    },
    deleteAppApi: async function (id) {
      const res = await this.$http.delete(this.$http.API.APPS_DELETE + id)
      if (res.success) {
        this.$message({
          message: '删除成功啦',
          type: 'success'
        })
        this.appsApi()
      }
    }
  },
  created () {
    this.appsApi()
  }
}
</script>
