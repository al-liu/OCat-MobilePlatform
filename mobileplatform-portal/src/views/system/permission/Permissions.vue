<template>
  <div>
    <el-row style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-button v-if="$menuPermission.HAS_PERMISSION_CREATE_BUTTON" type="primary" @click="createAction()">创建新权限<i class="el-icon-apple el-icon--right"></i></el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="permissionsData"
          border
          style="width: 100%">
          <el-table-column
            fixed
            prop="name"
            label="权限名称"
            width="200">
          </el-table-column>
          <el-table-column
            fixed
            prop="code"
            label="权限编码"
            width="200">
          </el-table-column>
          <el-table-column
            prop="description"
            label="描述">
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="200">
            <template slot-scope="scope">
              <el-button v-if="$menuPermission.HAS_PERMISSION_UPDATE_BUTTON" @click="editAction(scope.row)" type="text" size="small">编辑</el-button>
              <el-button v-if="$menuPermission.HAS_PERMISSION_DELETE_BUTTON" @click="deleteAction(scope.row)" type="text" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <!-- 创建新权限 -->
    <el-dialog title="创建新权限" :visible.sync="createDialogFormVisible">
      <el-form :model="permissionForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="权限名称"  prop="name">
          <el-input v-model="permissionForm.name"></el-input>
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="permissionForm.code"></el-input>
        </el-form-item>
        <el-form-item label="权限描述" prop="description">
          <el-input v-model="permissionForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="createPermissionAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 编辑新角色 -->
    <el-dialog title="编辑新权限" :visible.sync="editDialogFormVisible">
      <el-form :model="permissionForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="权限名称"  prop="name">
          <el-input v-model="permissionForm.name"></el-input>
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="permissionForm.code"></el-input>
        </el-form-item>
        <el-form-item label="权限描述" prop="description">
          <el-input v-model="permissionForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editPermissionAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      permissionsData: [],
      createDialogFormVisible: false,
      editDialogFormVisible: false,
      tapPermissionId: 0,
      permissionForm: {
        name: '',
        code: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入编码', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入描述', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    createAction() {
      this.permissionForm.code = ''
      this.permissionForm.name = ''
      this.permissionForm.description = ''
      this.createDialogFormVisible = true
    },
    editAction(row) {
      this.permissionForm.code = row.code
      this.permissionForm.name = row.name
      this.permissionForm.description = row.description
      this.editDialogFormVisible = true
      this.tapPermissionId = row.id
    },
    deleteAction(row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deletePermissionApi(row.id)
      }).catch(() => {
      })
    },
    createPermissionAction(formName) {
      this.createDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.createPermissionApi()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editPermissionAction(formName) {
      this.editDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.updatePermissionApi(this.tapPermissionId)
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    permissionsApi: async function () {
      const res = await this.$http.get(this.$http.API.PERMISSION_ALL)
      if (res.success) {
        this.permissionsData = res.data
      }
    },
    createPermissionApi: async function () {
      let params = this.permissionForm
      const res = await this.$http.post(this.$http.API.PERMISSION_CREATE, params)
      if (res.success) {
        this.$message({
          message: '创建成功啦',
          type: 'success'
        })
        this.permissionsApi()
      }
    },
    updatePermissionApi: async function (permissionId) {
      let params = this.permissionForm
      const res = await this.$http.put(this.$http.API.PERMISSION_UPDATE+permissionId, params)
      if (res.success) {
        this.$message({
          message: '更新成功啦',
          type: 'success'
        })
        this.permissionsApi()
      }
    },
    deletePermissionApi: async function (permissionId) {
      const res = await this.$http.delete(this.$http.API.PERMISSION_DELETE+permissionId)
      if (res.success) {
        this.$message({
          message: '删除成功啦',
          type: 'success'
        })
        this.permissionsApi()
      }
    },
  },
  created() {
    this.permissionsApi()
  }
}
</script>

<style>

</style>