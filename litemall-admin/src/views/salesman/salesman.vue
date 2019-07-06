<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入姓名"/>
      <el-input v-model="listQuery.phone" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="团长ID" prop="id" sortable/>

      <el-table-column align="center" width="100px" label="团长姓名" prop="name"/>

      <el-table-column align="center" label="手机号码" prop="phone"/>

      <el-table-column align="center" width="70px" label="性别" prop="gender">
        <template slot-scope="scope">
          <el-tag >{{ genderDic[scope.row.gender] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="所在地" prop="address"/>
      <el-table-column align="center" label="配送半径" prop="dispatchingDistance"/>
      <el-table-column align="center" width="100px" label="团长等级" prop="salesmanLevel">
        <template slot-scope="scope">
          <el-tag >{{ saleLevelDic[scope.row.saleLevel] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" width="100px" label="工作状态" prop="workStatus">
        <template slot-scope="scope">
          <el-tag>{{ workStatusDic[scope.row.workStatus] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="left" label="审核状态" prop="auditStatus">
        <template slot-scope="scope">
          <el-tag :type="scope.row.auditStatus==1 ? 'success' : 'error' ">{{ scope.row.auditStatus!=2 ? auditStatusDic[scope.row.auditStatus] : auditStatusDic[scope.row.auditStatus] +' 原因：' +scope.row.auditFailReason }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="left" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.auditStatus==0" type="primary" size="mini" @click="handleAudit(scope.row)">审核</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :visible.sync="dialogFormVisible" title="审核">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item prop="auditStatus" label="审核结论">
          <el-radio-group v-model="dataForm.auditStatus" @change="chackeAndAddRule">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="dataForm.auditStatus==2" label="不通过原因" prop="auditFailReason">
          <el-input v-model="dataForm.auditFailReason"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="auditData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, deleteSalesman, auditSalesman } from '@/api/salesman'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Salesman',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        phone: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      rules: {
        auditStatus: [
          { required: true, message: '请选择审核结果', trigger: 'blur' }
        ],
        auditFailReason: [
          { required: false, message: '', trigger: 'blur' }
        ]
      },
      downloadLoading: false,
      genderDic: ['未知', '男', '女'],
      saleLevelDic: ['初级', '中级', '高级'],
      workStatusDic: ['休息', '工作中'],
      auditStatusDic: ['未审核', '通过', '未通过'],
      dataForm: {
        id: undefined,
        auditStatus: undefined,
        auditFailReason: undefined
      },
      dialogFormVisible: false,
      auditingData: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    chackeAndAddRule() {
      if (this.dataForm.auditStatus === '2') {
        this.rules.auditFailReason[0].required = true
        this.rules.auditFailReason[0].message = '请填写未通过意见'
      } else {
        this.rules.auditFailReason[0].required = false
        this.rules.auditFailReason[0].message = ''
      }
    },
    handleAudit(row) {
      this.auditingData = row
      this.resetForm()
      this.dialogFormVisible = true
      this.dataForm.id = row.id
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    auditData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          auditSalesman(this.dataForm)
            .then(response => {
              this.auditingData.auditStatus = this.dataForm.auditStatus
              this.auditingData.auditFailReason = this.dataForm.auditFailReason
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '审核成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    resetForm() {
      this.dataForm = {
        id: undefined,
        auditStatus: undefined,
        auditFailReason: undefined
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleDelete(row) {
      deleteSalesman(row).then(response => {
        this.$notify.success({
          title: '成功',
          message: '删除成功'
        })
        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['姓名', '手机号码', '性别', '所在地', '配送半径', '团长等级', '状态']
        const filterVal = ['name', 'phone', 'gender', 'address', 'dispatchingDistance', 'salesmanLevel', 'workStatus']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
