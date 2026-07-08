<template>
  <div>
    <div class="page-title">作业与资料管理</div>
    <el-card>
      <template #header>
        <el-radio-group v-model="activeTab">
          <el-radio-button value="assignments">作业管理</el-radio-button>
          <el-radio-button value="materials">课程资料</el-radio-button>
        </el-radio-group>
      </template>

      <div v-if="activeTab === 'assignments'">
        <div style="margin-bottom:16px">
          <el-button type="primary" @click="$router.push('/teacher/courses/' + courseId + '/assignments/create')" round>发布作业</el-button>
        </div>
        <el-table :data="assignments" stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="fullScore" label="满分" width="80" />
          <el-table-column prop="deadline" label="截止时间" width="180" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="$router.push('/teacher/courses/' + courseId + '/assignments/' + row.id + '/grade')" round>批改</el-button>
              <el-button size="small" type="danger" @click="handleDeleteAssignment(row)" round>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:16px;text-align:center">
          <el-pagination v-if="total > pageSize" v-model:current-page="pageNum" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchAssignments" />
        </div>
        <el-empty v-if="!loading && assignments.length === 0" description="暂无作业" />
      </div>

      <div v-if="activeTab === 'materials'">
        <el-form :model="materialForm" label-width="110px" style="margin-bottom:24px">
          <el-form-item label="资料标题" required>
            <el-input v-model="materialForm.title" placeholder="资料标题" />
          </el-form-item>
          <el-form-item label="资料内容" required>
            <el-input v-model="materialForm.content" type="textarea" :rows="4" placeholder="资料正文内容" />
          </el-form-item>
          <el-form-item label="最低阅读时长">
            <el-input-number v-model="materialForm.requiredSeconds" :min="10" :max="3600" /> 秒
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAddMaterial" :loading="addingMaterial" round>发布资料</el-button>
          </el-form-item>
        </el-form>
        <el-divider />
        <el-table :data="materials" stripe v-loading="loadingMaterials">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="requiredSeconds" label="阅读时长" width="100">
            <template #default="{ row }">{{ row.requiredSeconds }}秒</template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleDeleteMaterial(row.id)" round>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loadingMaterials && materials.length === 0" description="暂无资料" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { assignmentPage, deleteAssignment } from '../../api/exam'
import { listMaterials, addMaterial, deleteMaterial } from '../../api/material'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const courseId = route.params.id
const activeTab = ref('assignments')

const assignments = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10

const materials = ref([])
const loadingMaterials = ref(false)
const addingMaterial = ref(false)
const materialForm = ref({ title: '', content: '', requiredSeconds: 60 })

onMounted(() => { fetchAssignments(); fetchMaterials() })

async function fetchAssignments() {
  loading.value = true
  const res = await assignmentPage({ courseId, pageNum: pageNum.value, pageSize })
  assignments.value = res.list
  total.value = res.total
  loading.value = false
}

async function handleDeleteAssignment(row) {
  await deleteAssignment(row.id)
  ElMessage.success('已删除')
  fetchAssignments()
}

async function fetchMaterials() {
  loadingMaterials.value = true
  materials.value = await listMaterials(courseId)
  loadingMaterials.value = false
}

async function handleAddMaterial() {
  if (!materialForm.value.title.trim() || !materialForm.value.content.trim()) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  addingMaterial.value = true
  await addMaterial({ ...materialForm.value, courseId, teacherId: store.user.id })
  ElMessage.success('发布成功')
  materialForm.value = { title: '', content: '', requiredSeconds: 60 }
  await fetchMaterials()
  addingMaterial.value = false
}

async function handleDeleteMaterial(id) {
  await ElMessageBox.confirm('确认删除此资料？')
  await deleteMaterial(id)
  ElMessage.success('已删除')
  await fetchMaterials()
}
</script>
