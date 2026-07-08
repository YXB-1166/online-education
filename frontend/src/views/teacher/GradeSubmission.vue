<template>
  <div>
    <div class="page-title">批改作业</div>
    <el-card>
      <el-table :data="submissions" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="学生" width="120">
          <template #default="{ row }">{{ studentMap[row.studentId] || '未知' }}</template>
        </el-table-column>
        <el-table-column prop="content" label="提交内容" show-overflow-tooltip min-width="200" />
        <el-table-column label="分数" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.score != null" type="success" effect="dark" round>{{ row.score }}</el-tag>
            <el-tag v-else type="info" round>待批改</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <div v-if="row.score == null" style="display:flex;gap:6px;align-items:center;flex-wrap:wrap">
              <el-input-number v-model="row._score" :min="0" :max="100" size="small" style="width:100px" />
              <el-input v-model="row._comment" placeholder="评语" size="small" style="width:120px" />
              <el-button size="small" :loading="generating === row.id" @click="handleAutoComment(row)" round>🤖 自动评语</el-button>
              <el-button size="small" type="primary" @click="handleGrade(row)" round>打分</el-button>
            </div>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submissionPage, gradeSubmission, autoComment } from '../../api/exam'
import { listUsers } from '../../api/user'

const route = useRoute()
const submissions = ref([])
const loading = ref(false)
const studentMap = ref({})
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const generating = ref(null)

onMounted(async () => {
  const users = await listUsers()
  users.forEach(u => { studentMap.value[u.id] = u.realName })
  fetchData()
})

async function fetchData() {
  loading.value = true
  const res = await submissionPage({ assignmentId: route.params.aid, pageNum: pageNum.value, pageSize })
  submissions.value = res.list.map(s => ({ ...s, _score: 60, _comment: '' }))
  total.value = res.total
  loading.value = false
}

async function handleGrade(row) {
  await gradeSubmission(row.id, row._score, row._comment)
  ElMessage.success('打分成功')
  fetchData()
}

async function handleAutoComment(row) {
  generating.value = row.id
  try {
    const comment = await autoComment(row.id, row._score)
    row._comment = comment
    ElMessage.success('已生成评语')
  } catch {
    ElMessage.error('生成评语失败')
  }
  generating.value = null
}
</script>
