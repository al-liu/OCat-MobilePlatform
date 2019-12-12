<template>
  <div>
    <el-row style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-button v-if="$menuPermission.HAS_MENU_CREATE_BUTTON" type="primary" @click="createAction()">创建新菜单<i class="el-icon-apple el-icon--right"></i></el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="menusData"
          border
          row-key="id"
          default-expand-all
          :tree-props="{children: 'children'}"
          style="width: 100%">
          <el-table-column
            fixed
            prop="id"
            label="菜单id">
          </el-table-column>
          <el-table-column
            prop="parentId"
            label="菜单父id">
          </el-table-column>
          <el-table-column
            prop="orderNum"
            label="排序编号">
          </el-table-column>
          <el-table-column
            prop="type"
            label="类型">
            <template slot-scope="scope">
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.type | menuType}}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            label="名称">
          </el-table-column>
          <el-table-column
            prop="icon"
            label="图标">
          </el-table-column>
          <el-table-column
            prop="href"
            label="路由">
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
              <el-button v-if="$menuPermission.HAS_MENU_UPDATE_BUTTON" @click="editAction(scope.row)" type="text" size="small">编辑</el-button>
              <el-button v-if="$menuPermission.HAS_MENU_DELETE_BUTTON" @click="deleteAction(scope.row)" type="text" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <!-- 创建新菜单 -->
    <el-dialog title="创建新菜单" :visible.sync="createDialogFormVisible">
      <el-form :model="menuForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="菜单父id"  prop="parentId">
          <el-input v-model="menuForm.parentId"></el-input>
        </el-form-item>
        <el-form-item label="排序编号" prop="orderNum">
          <el-input v-model="menuForm.orderNum"></el-input>
        </el-form-item>
        <el-form-item label="菜单类型"  prop="type">
          <el-select v-model="menuForm.type" placeholder="请选择">
            <el-option
              v-for="item in menuTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="menuForm.name"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标"  prop="icon">
          <el-input v-model="menuForm.icon"></el-input>
        </el-form-item>
        <el-form-item label="菜单路由" prop="href">
          <el-input v-model="menuForm.href"></el-input>
        </el-form-item>
        <el-form-item label="菜单描述" prop="description">
          <el-input v-model="menuForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="createMenuAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 编辑新菜单 -->
    <el-dialog title="编辑新权限" :visible.sync="editDialogFormVisible">
      <el-form :model="menuForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="菜单父id"  prop="parentId">
          <el-input v-model="menuForm.parentId"></el-input>
        </el-form-item>
        <el-form-item label="排序编号" prop="orderNum">
          <el-input v-model="menuForm.orderNum"></el-input>
        </el-form-item>
        <el-form-item label="菜单类型"  prop="type">
          <el-select v-model="menuForm.type" placeholder="请选择">
            <el-option
              v-for="item in menuTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="menuForm.name"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标"  prop="icon">
          <el-input v-model="menuForm.icon"></el-input>
        </el-form-item>
        <el-form-item label="菜单路由" prop="href">
          <el-input v-model="menuForm.href"></el-input>
        </el-form-item>
        <el-form-item label="菜单描述" prop="description">
          <el-input v-model="menuForm.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editMenuAction('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      menusData: [],
      createDialogFormVisible: false,
      editDialogFormVisible: false,
      tapMenuId: 0,
      menuForm: {
        parentId: '',
        orderNum: '',
        type: '',
        name: '',
        icon: '',
        href: '',
        description: ''
      },
      rules: {
        parentId: [
          { required: true, message: '请输入菜单父id', trigger: 'blur' }
        ],
        orderNum: [
          { required: true, message: '请输入排序编号', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请输入菜单类型', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入菜单名称', trigger: 'blur' }
        ],
        icon: [
          { required: true, message: '请输入菜单图标', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入菜单描述', trigger: 'blur' }
        ]
      },
      menuTypeOptions: [{
        value: 1,
        label: '菜单'
      },{
        value: 2,
        label: '按钮'
      }]
    }
  },
  methods: {
    createAction() {
      this.menuForm.parentId = ''
      this.menuForm.orderNum = ''
      this.menuForm.type = ''
      this.menuForm.name = ''
      this.menuForm.icon = ''
      this.menuForm.href = ''
      this.menuForm.description = ''
      this.createDialogFormVisible = true
    },
    editAction(row) {
      this.menuForm.parentId = row.parentId
      this.menuForm.orderNum = row.orderNum
      this.menuForm.type = row.type
      this.menuForm.name = row.name
      this.menuForm.icon = row.icon
      this.menuForm.href = row.href
      this.menuForm.description = row.description
      this.editDialogFormVisible = true
      this.tapMenuId = row.id
    },
    deleteAction(row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteMenuApi(row.id)
      }).catch(() => {
      })
    },
    createMenuAction(formName) {
      this.createDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.createMenuApi()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editMenuAction(formName) {
      this.editDialogFormVisible = false
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.updateMenuApi(this.tapMenuId)
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    menusApi: async function () {
      this.menusData = []
      const res = await this.$http.get(this.$http.API.MENUS_ALL)
      if (res.success) {
        this.menusData = res.data
        console.log('所有菜单：'+JSON.stringify(this.menusData))
      }
    },
    createMenuApi: async function () {
      let params = this.menuForm
      const res = await this.$http.post(this.$http.API.MENUS_CREATE, params)
      if (res.success) {
        this.$message({
          message: '创建成功啦',
          type: 'success'
        })
        this.menusApi()
      }
    },
    updateMenuApi: async function (menuId) {
      let params = this.menuForm
      const res = await this.$http.put(this.$http.API.MENUS_UPDATE+menuId, params)
      if (res.success) {
        this.$message({
          message: '更新成功啦',
          type: 'success'
        })
        this.menusApi()
      }
    },
    deleteMenuApi: async function (menuId) {
      const res = await this.$http.delete(this.$http.API.MENUS_DELETE+menuId)
      if (res.success) {
        this.$message({
          message: '删除成功啦',
          type: 'success'
        })
        this.menusApi()
      }
    },
  },
  created() {
    this.menusApi()
  }
}
</script>

<style>

</style>