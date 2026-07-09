<template>
  <div>
    <div class="page-title">用户管理</div>
    <div style="margin-bottom:16px">
      <el-button type="primary" @click="openAddDialog" round>
        <el-icon><Plus /></el-icon>新增用户
      </el-button>
    </div>
    <el-card>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.role === 3" type="danger" round>管理员</el-tag>
            <el-tag v-else-if="row.role === 2" type="warning" round>教师</el-tag>
            <el-tag v-else type="success" round>学生</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" round>{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)" round>详情</el-button>
            <el-button size="small" @click="editUser(row)" round>编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)" round>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:center">
        <el-pagination
          v-if="total > pageSize"
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDetailDialog" title="用户详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailData.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.realName }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-if="detailData.role === 3" type="danger" round>管理员</el-tag>
          <el-tag v-else-if="detailData.role === 2" type="warning" round>教师</el-tag>
          <el-tag v-else type="success" round>学生</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="职称" v-if="detailData.role === 2">{{ detailData.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ detailData.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small" round>{{ detailData.status === 1 ? '正常' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false" round>关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑用户' : '新增用户'" width="450px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="dialogForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialogForm.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="dialogForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="dialogForm.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="dialogForm.role" style="width:100%">
            <el-option :value="1" label="学生" />
            <el-option :value="2" label="教师" />
            <el-option :value="3" label="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title" v-if="dialogForm.role === 2">
          <el-input v-model="dialogForm.title" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="dialogForm.email" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="dialogForm.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false" round>取消</el-button>
        <el-button type="primary" @click="handleSave" round>保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userPage, addUser, updateUser, deleteUser } from '../../api/user'

const users = ref([])
const loading = ref(false)

const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const dialogForm = ref({ username: '', password: '', realName: '', role: 1, title: '', email: '', phone: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const showDetailDialog = ref(false)
const detailData = ref({})

const total = ref(0)
const pageNum = ref(1)
const pageSize = 10

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  const res = await userPage({ pageNum: pageNum.value, pageSize })
  users.value = res.list
  total.value = res.total
  loading.value = false
}

function showDetail(row) {
  detailData.value = { ...row }
  showDetailDialog.value = true
}

function openAddDialog() {
  isEdit.value = false
  dialogForm.value = { username: '', password: '', realName: '', role: 1, title: '', email: '', phone: '' }
  showDialog.value = true
}

function editUser(row) {
  isEdit.value = true
  dialogForm.value = { ...row }
  showDialog.value = true
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  if (isEdit.value) {
    await updateUser(dialogForm.value)
    ElMessage.success('更新成功')
  } else {
    await addUser(dialogForm.value)
    ElMessage.success('新增成功')
  }
  showDialog.value = false
  fetchData()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该用户？')
  await deleteUser(id)
  ElMessage.success('删除成功')
  fetchData()
}
</script>