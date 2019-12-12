<template>
  <div>
    <el-row style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-button v-if="$menuPermission.HAS_ROLE_CREATE_BUTTON" type="primary" @click="createAction()">创建新角色<i class="el-icon-apple el-icon--right"></i></el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="rolesData"
          border
          style="width: 100%">
          <el-table-column
            fixed
            prop="name"
            label="角色名称"
            width="200">
          </el-table-column>
          <el-table-column
            fixed
            prop="code"
            label="角色编码"
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
              <el-button v-if="$menuPermission.HAS_ROLE_UPDATE_BUTTON" @click="editAction(scope.row)" type="text" size="small">编辑</el-button>
              <el-button v-if="$menuPermission.HAS_ROLE_DELETE_BUTTON" @click="deleteAction(scope.row)" type="text" size="small">删除</el-button>
              <el-button v-if="$menuPermission.HAS_ROLE_PERMISSION_BUTTON" @click="permissionAction(scope.row)" type="text" size="small">权限</el-button>
              <el-button v-if="$menuPermission.HAS_ROLE_MENU_BUTTON" @click="menuAction(scope.row)" type="text" size="small">菜单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <!-- 创建新角色 -->
    <el-dialog title="创建新角色" :visible.sync="createDialogFormVisible">
      <el-form :model="roleForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="角色名称"  prop="name">
          <el-input v-model="roleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="roleForm.code"></el-input>
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="roleForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="createRoleAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 编辑新角色 -->
    <el-dialog title="编辑新角色" :visible.sync="editDialogFormVisible">
      <el-form :model="roleForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="角色名称"  prop="name">
          <el-input v-model="roleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="roleForm.code"></el-input>
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="roleForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editRoleAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 权限管理 -->
    <el-dialog title="分配权限" :visible.sync="permissionDialogFormVisible">
      <el-transfer 
        v-model="permissions" 
        :titles="['待分配权限', '已分配权限']"
        :button-texts="['到左边', '到右边']"
        :data="permissionsData"></el-transfer>
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="managePermissionAction()">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="分配菜单" :visible.sync="menuDialogFormVisible">
      <el-tree
        :data="menusData"
        show-checkbox
        node-key="id"
        ref="tree"
        :default-expanded-keys="[]"
        :default-checked-keys="defaultCheckedMenus"
        :props="defaultProps">
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="menuDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="manageMenuAction()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      rolesData: [],
      createDialogFormVisible: false,
      editDialogFormVisible: false,
      permissionDialogFormVisible: false,
      menuDialogFormVisible: false,
      tapRoleId: 0,
      permissionsData:[],
      permissions:[],
      roleForm: {
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
      },
      menusData: [],
      defaultCheckedMenus: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  methods: {
    createAction() {
      this.roleForm.code = ''
      this.roleForm.name = ''
      this.roleForm.description = ''
      this.createDialogFormVisible = true
    },
    editAction(row) {
      this.roleForm.code = row.code
      this.roleForm.name = row.name
      this.roleForm.description = row.description
      this.editDialogFormVisible = true
      this.tapRoleId = row.id
    },
    deleteAction(row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteRoleApi(row.id)
      }).catch(() => {
      })
    },
    permissionAction(row) {
      this.tapRoleId = row.id
      this.permissionDialogFormVisible = true
      this.permissionsApi()
    },
    menuAction(row) {
      this.tapRoleId = row.id
      this.menuDialogFormVisible = true
      this.menusApi()
    },
    createRoleAction(formName) {
      this.createDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.createRoleApi()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editRoleAction(formName) {
      this.editDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.updateRoleApi(this.tapRoleId)
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    managePermissionAction() {
      this.permissionDialogFormVisible = false
      this.allotPermissionsApi(this.tapRoleId)
    },
    manageMenuAction() {
      this.menuDialogFormVisible = false
      var menuIdList = this.$refs.tree.getCheckedNodes(false, true).map(x => x.id)
      console.log(menuIdList);
      this.allotMenusApi(this.tapRoleId, menuIdList)
    },
    rolesApi: async function () {
      const res = await this.$http.get(this.$http.API.ROLES_ALL)
      if (res.success) {
        this.rolesData = res.data
      }
    },
    permissionsApi: async function () {
      this.permissionsData = []
      const res = await this.$http.get(this.$http.API.PERMISSIONS_ALL)
      if (res.success) {
        var list = res.data
        list.forEach(element => {
          var item = {
            key: element.id,
            label: element.name,
            disabled: false
          }
          this.permissionsData.push(item)
        })
        this.permissionsOfRoleApi(this.tapRoleId)
      }
    },
    menusApi: async function () {
      this.permissionsData = []
      const res = await this.$http.get(this.$http.API.MENUS_ALL)
      if (res.success) {
        this.menusData = res.data
        this.menusOfRoleApi(this.tapRoleId)
      }
    },
    permissionsOfRoleApi: async function (roleId) {
      this.permissions = []
      const res = await this.$http.get("/system/role/" + roleId + "/permissions")
      if (res.success) {
        var list = res.data
        list.forEach(element => {
          this.permissions.push(element.id)
        })
      }
    },
    menusOfRoleApi: async function (roleId) {
      this.defaultCheckedMenus = []
      const res = await this.$http.get("/system/role/" + roleId + "/menus")
      if (res.success) {
        var list = res.data
        // 后台返回所有菜单数据，包括父菜单，但是树形组件默认勾选数组不能有父菜单的 id，会直接默认勾选父菜单的所有子菜单。
        var checked = []
        list.forEach(element => {
          checked.push(element.id)
        })
        this.defaultCheckedMenus = checked
        console.log('defaultCheckedMenus:' + this.defaultCheckedMenus)
      }
    },
    allotPermissionsApi: async function (roleId) {
      const res = await this.$http.put("/system/role/" + roleId + "/permissions", this.permissions)
      if (res.success) {
        this.$message({
          message: '恭喜您，分配权限成功',
          type: 'success'
        })
      }
    },
    allotMenusApi: async function (roleId, menuIdList) {
      const res = await this.$http.put("/system/role/" + roleId + "/menus", menuIdList)
      if (res.success) {
        this.$message({
          message: '恭喜您，分配菜单成功',
          type: 'success'
        })
      }
    },
    createRoleApi: async function () {
      let params = this.roleForm
      const res = await this.$http.post(this.$http.API.ROLES_CREATE, params)
      if (res.success) {
        this.$message({
          message: '创建成功啦',
          type: 'success'
        })
        this.rolesApi()
      }
    },
    updateRoleApi: async function (roleId) {
      let params = this.roleForm
      const res = await this.$http.put(this.$http.API.ROLES_UPDATE+roleId, params)
      if (res.success) {
        this.$message({
          message: '更新成功啦',
          type: 'success'
        })
        this.rolesApi()
      }
    },
    deleteRoleApi: async function (roleId) {
      const res = await this.$http.delete(this.$http.API.ROLES_DELETE+roleId)
      if (res.success) {
        this.$message({
          message: '删除成功啦',
          type: 'success'
        })
        this.rolesApi()
      }
    },
  },
  created() {
    this.rolesApi()
  }
}
</script>

<style>

</style>