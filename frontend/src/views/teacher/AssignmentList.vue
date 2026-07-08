<template>
  <div>
    <div class="page-title">作业管理</div>
    <div style="margin-bottom:16px">
      <el-button type="primary" @click="$router.push('/teacher/courses/' + courseId + '/assignments/create')" round>发布作业</el-button>
    </div>
    <el-card>
      <el-table :data="assignments" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="fullScore" label="满分" width="80" />
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="$router.push('/teacher/courses/' + courseId + '/assignments/' + row.id + '/grade')" round>批改</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" round>删除</el-button>
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
      <el-empty v-if="!loading && assignments.length === 0" description="暂无作业" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { assignmentPage, deleteAssignment } from '../../api/exam'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id
const assignments = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  const res = await assignmentPage({ courseId, pageNum: pageNum.value, pageSize })
  assignments.value = res.list
  total.value = res.total
  loading.value = false
}

async function handleDelete(row) {
  await deleteAssignment(row.id)
  ElMessage.success('已删除')
  fetchData()
}
</script>
