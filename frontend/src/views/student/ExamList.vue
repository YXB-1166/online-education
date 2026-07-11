<template>
  <div>
    <div class="page-title">在线考试</div>
    <el-card>
      <template #header>
        <el-radio-group v-model="tab">
          <el-radio-button value="pending">进行中</el-radio-button>
          <el-radio-button value="history">已完成</el-radio-button>
        </el-radio-group>
      </template>

      <div v-if="tab === 'pending'">
        <el-table :data="availableExams" stripe v-loading="loading">
          <el-table-column prop="title" label="考试名称" min-width="180" />
          <el-table-column prop="courseId" label="课程" width="200">
            <template #default="{ row }">{{ courseMap[row.courseId] || '课程#' + row.courseId }}</template>
          </el-table-column>
          <el-table-column label="时长" width="80" align="center">
            <template #default="{ row }">{{ row.duration }}分</template>
          </el-table-column>
          <el-table-column label="总分" width="70" align="center" prop="totalScore" />
          <el-table-column label="操作" width="140">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="$router.push('/exam/' + row.id)" round>开始考试</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && availableExams.length === 0" description="暂无进行中的考试" />
      </div>

      <div v-else>
        <el-table :data="historyRecords" stripe v-loading="loadingHistory">
          <el-table-column label="考试名称" min-width="200">
            <template #default="{ row }">{{ examTitleMap[row.examId] || '考试#' + row.examId }}</template>
          </el-table-column>
          <el-table-column label="课程" min-width="140">
            <template #default="{ row }">{{ courseMap[examCourseMap[row.examId]] || courseMap[row.examId] || '--' }}</template>
          </el-table-column>
          <el-table-column label="得分" width="100" align="center">
            <template #default="{ row }">
              <span :style="{ color: (examTitleMap[row.examId] ? getExamTotal(row.examId) : 0) > 0 && row.score >= (getExamTotal(row.examId) || 100) * 0.6 ? '#67c23a' : '#f56c6c', fontWeight: 700 }">{{ row.score }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" min-width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="$router.push('/exam/' + row.examId)" round>查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loadingHistory && historyRecords.length === 0" description="暂无考试记录" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { examPage, getMyExamRecords, getExamSilent } from '../../api/exam-online'
import { listCourses } from '../../api/course'

const router = useRouter()
const store = useUserStore()
const tab = ref('pending')

const loading = ref(false)
const loadingHistory = ref(false)
const allExams = ref([])
const historyRecords = ref([])
const submittedExamIds = ref(new Set())
const myCourses = ref([])
const courseMap = ref({})
const examTitleMap = ref({})
const examTotalMap = ref({})
const examCourseMap = ref({})

const availableExams = computed(() => {
  const myIds = new Set(myCourses.value.map(c => c.id || c.courseId))
  const now = Date.now()
  return allExams.value.filter(e =>
    e.status === 1 &&
    myIds.has(e.courseId) &&
    !submittedExamIds.value.has(e.id) &&
    (!e.endTime || new Date(e.endTime).getTime() > now)
  )
})

onMounted(async () => {
  loading.value = true
  try {
    const [courses, records] = await Promise.all([
      listCourses({ role: 1, userId: store.user.id }).catch(() => []),
      getMyExamRecords(store.user.id).catch(() => [])
    ])
    myCourses.value = courses
    historyRecords.value = records
    submittedExamIds.value = new Set(records.map(r => r.examId))
    courses.forEach(c => { courseMap.value[c.id || c.courseId] = c.courseName || '课程#' + c.id })
  } catch (_) {}
  try {
    const res = await examPage({ status: 1, pageNum: 1, pageSize: 200 })
    allExams.value = res.list || []
  } catch (_) {}
  loading.value = false
  // fetch exam titles for history records
  for (const r of historyRecords.value) {
    if (!examTitleMap.value[r.examId]) {
      const e = await getExamSilent(r.examId).catch(() => null)
      if (e) { examTitleMap.value[r.examId] = e.title; examTotalMap.value[r.examId] = e.totalScore; examCourseMap.value[r.examId] = e.courseId }
      else { examTitleMap.value[r.examId] = '考试#' + r.examId }
    }
  }
})

function getExamTotal(examId) { return examTotalMap.value[examId] || 100 }
</script>
