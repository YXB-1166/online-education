<template>
  <div>
    <div class="page-title">考试管理</div>
    <el-card>
      <el-table :data="exams" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="课程" min-width="140">
          <template #default="{ row }">{{ courseNames[row.courseId] || '课程#' + row.courseId }}</template>
        </el-table-column>
        <el-table-column prop="title" label="考试名称" min-width="180" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="自动批改" width="140" align="center">
          <template #default="{ row }">
            <span v-if="row.autoGradeChoice">选择✓</span><span v-else>选择✗</span>
            <span style="margin:0 4px">/</span>
            <span v-if="row.autoGradeJudge">判断✓</span><span v-else>判断✗</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="80" align="center" />
        <el-table-column prop="totalScore" label="总分" width="70" align="center" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.courseId + '/exams')" round>管理</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.courseId + '/exams/' + row.id + '/edit')" round>编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && exams.length === 0" description="暂无考试" />
      <div style="margin-top:16px;text-align:center">
        <el-pagination v-if="total > pageSize" v-model:current-page="pageNum" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchExams" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { examPage } from '../../api/exam-online'
import { myTeaching } from '../../api/course'

const router = useRouter()
const store = useUserStore()

const exams = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const courseNames = ref({})

onMounted(async () => {
  try {
    const courses = await myTeaching(store.user.id)
    courses.forEach(c => { courseNames.value[c.id] = c.courseName })
  } catch (_) {}
  fetchExams()
})

async function fetchExams() {
  loading.value = true
  const res = await examPage({ teacherId: store.user.id, pageNum: pageNum.value, pageSize })
  exams.value = res.list
  total.value = res.total
  loading.value = false
}
</script>
