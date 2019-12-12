<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-page-header @back="goBack" content="离线包详情">
        </el-page-header>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <h2>{{appInfo.name}}</h2>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <span>AppId:{{appInfo.appId}}</span>
        <el-divider direction="vertical"></el-divider>
        <span>AppSecret:{{appInfo.appSecret}}</span>
      </el-col>
    </el-row>
    <el-row style="margin-top:20px;margin-bottom:20px;">
      <el-col :span="24">
        <el-button v-if="$menuPermission.HAS_PACKAGE_UPLOAD_BUTTON" type="primary" @click="uploadPackageAction()">上传离线包<i class="el-icon-apple el-icon--right"></i></el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="resourcesData"
          border
          style="width: 100%">
          <el-table-column
            fixed
            prop="versionName"
            label="版本名称"
            width="200">
          </el-table-column>
          <el-table-column
            fixed
            prop="versionCode"
            label="版本编号">
          </el-table-column>
          <el-table-column
            fixed
            prop="status"
            label="资源状态">
            <template slot-scope="scope">
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.status | packageStatus}}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="200">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status===1 && $menuPermission.HAS_PACKAGE_DELETE_BUTTON" @click="deleteAction(scope.row)" type="text" size="small">删除</el-button>
              <el-button v-if="$menuPermission.HAS_PACKAGE_PATCHS_BUTTON" @click="patchAction(scope.row)" type="text" size="small">补丁包</el-button>
              <el-button v-if="scope.row.status===1 && $menuPermission.HAS_PACKAGE_RELEASE_BUTTON" @click="releaseAction(scope.row)" type="primary" size="small">发布<i class="el-icon-upload el-icon--right"></i></el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="totalNum">
        </el-pagination>
      </el-col>
    </el-row>
    <el-dialog title="补丁包" :visible.sync="dialogTableVisible">
      <el-table :data="patchsData">
        <el-table-column property="newVersion" label="新版本号" width="150"></el-table-column>
        <el-table-column property="oldVersion" label="旧版本号" width="150"></el-table-column>
        <el-table-column property="status" label="状态" width="150">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.status | packageStatus}}</span>
            </div>
          </template>          
        </el-table-column>
        <el-table-column property="url" label="下载地址"></el-table-column>
      </el-table>
    </el-dialog>
    <!-- 上传离线包 -->
    <el-dialog title="上传离线包" :visible.sync="uploadDialogFormVisible">
      <el-form :model="packageForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="新包版本名称"  prop="versionName">
          <el-input v-model="packageForm.versionName"></el-input>
          <p style="color:red;font-size:12px;line-height:13px;">当前最新版本名称:{{latestVersionName}}</p>
        </el-form-item>
        <el-form-item label="新包版本编码" prop="versionCode">
          <el-input v-model="packageForm.versionCode"></el-input>
          <p style="color:red;font-size:12px;line-height:13px;">当前最新版本编号:{{latestVersionCode}}</p>
        </el-form-item>
        <el-form-item label="选取离线包">
          <el-upload
            class="upload-demo"
            ref="upload"
            :action="uploadUrl"
            :http-request="customUploadAction"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :file-list="fileList"
            :limit="1"
            :auto-upload="false">
            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            <div slot="tip" class="el-upload__tip">上传 zip 压缩离线包</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogFormVisible = false">取 消</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload('ruleForm')">上传到服务器</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      resourcesData: [],
      appInfo: {id:"",name:"",appId:"",appSecret:""},
      currentPage: 1,
      pageSize: 10,
      totalNum: 0,
      patchsData: [],
      dialogTableVisible: false,
      uploadDialogFormVisible: false,
      packageForm: {
        versionName: '',
        versionCode: ''
      },
      rules: {
        versionName: [
          { required: true, message: '请输入版本名称', trigger: 'blur' }
        ],
        versionCode: [
          { required: true, message: '请输入版本编号', trigger: 'blur' }
        ]
      },
      latestVersionName: '',
      latestVersionCode: '',
      uploadData:{versionName:'', versionCode:''},
      fileList: []
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    deleteAction(row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.removePackageApi(row.id)
      }).catch(() => {
      })
    },
    uploadPackageAction() {
      this.packageForm.versionName = ''
      this.packageForm.versionCode = ''
      this.uploadDialogFormVisible = true
      this.latestResourceApi()
    },
    patchAction(row) {
      this.dialogTableVisible = true
      this.patchAllApi(row.versionName)
    },
    releaseAction(row) {
      this.releasePackageApi(row.id)
    },
    handleCurrentChange (val) {
      console.log(`当前页: ${val}`)
      console.log('当前页currentPage:'+this.currentPage)
      this.resourceAllApi(val)
    },
    submitUpload (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.uploadData = this.packageForm
          this.$refs.upload.submit();
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    customUploadAction (content) {
      var formData = new FormData();
      formData.append("package", content.file);
      formData.append("versionName", this.uploadData.versionName);
      formData.append("versionCode", this.uploadData.versionCode);
      formData.append("appId", this.appInfo.appId);
      formData.append("appSecret", this.appInfo.appSecret);
      this.uploadApi(formData)
    },
    handleRemove(file, fileList) {
      this.fileList = fileList
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },
    resourceAllApi: async function (currPage) {
      var pageParams = {current:currPage, size:this.pageSize}
      const res = await this.$http.get("/application/"+this.appInfo.id+"/resources", pageParams)
      if (res.success) {
        this.resourcesData = res.data.records
        this.pageSize = res.data.size
        this.totalNum = res.data.total
        this.currentPage = res.data.current
      }
    },
    patchAllApi: async function (versionName) {
      const res = await this.$http.get("/application/"+this.appInfo.id+"/resource/" + versionName + "/patchs")
      if (res.success) {
        this.patchsData = res.data
      }
    },
    releasePackageApi: async function (resourceId) {
      var params = {applicationId:this.appInfo.id, resourceId: resourceId}
      const res = await this.$http.post(this.$http.API.PACKAGE_RELEASE, params)
      if (res.success) {
        this.$message({
          message: '发布成功啦',
          type: 'success'
        })
        this.resourceAllApi(this.currentPage)
      }
    },
    removePackageApi: async function (resourceId) {
      var params = {applicationId:this.appInfo.id, resourceId: resourceId}
      const res = await this.$http.post(this.$http.API.PACKAGE_REMOVE, params)
      if (res.success) {
        this.$message({
          message: '删除成功啦',
          type: 'success'
        })
        this.resourceAllApi(this.currentPage)
      }
    },
    latestResourceApi: async function () {
      const res = await this.$http.get(this.$http.API.PACKAGE_LATEST, {applicationId: this.appInfo.id})
      if (res.success) {
        this.latestVersionName = res.data.versionName
        this.latestVersionCode = res.data.versionCode
      } else {
        this.latestVersionName = '暂无'
        this.latestVersionCode = '暂无'
      }
    },
    uploadApi: async function (formData) {
      const res = await this.$http.upload(this.uploadUrl, formData)
      if (res.success) {
        this.$message({
          message: '上传成功',
          type: 'success'
        })
        this.resourceAllApi(this.currentPage)
        this.uploadDialogFormVisible = false
      }
      this.fileList = []
    }
  },
  created() {
    this.appInfo = this.$route.query.appInfo
    this.resourceAllApi(1)
    var q = '?' + 'appId=1212&appSecret=1&versionName=1&versionCode=1'
    this.uploadUrl = this.$http.httpConfig.baseURL + this.$http.API.PACKAGE_UPLOAD
    
  }
}
</script>
