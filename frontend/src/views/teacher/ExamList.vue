<template>
  <div>
    <div class="page-title">在线考试管理</div>
    <el-card>
      <template #header>
        <div style="display:flex;align-items:center;gap:16px;flex-wrap:wrap">
          <el-radio-group v-model="filterStatus" @change="fetchExams">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="0">未发布</el-radio-button>
            <el-radio-button value="1">已发布</el-radio-button>
            <el-radio-button value="2">已结束</el-radio-button>
          </el-radio-group>
          <el-button type="primary" @click="$router.push('/teacher/courses/' + courseId + '/exams/create')" style="margin-left:auto" round>创建考试</el-button>
        </div>
      </template>
      <el-table :data="filteredExams" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="课程" min-width="120">
          <template #default="{ row }">{{ courseNames[row.courseId] || '--' }}</template>
        </el-table-column>
        <el-table-column prop="title" label="考试标题" min-width="160" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="isEnded(row)" type="info" size="small">已结束</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已发布</el-tag>
            <el-tag v-else type="warning" size="small">未发布</el-tag>
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
            <el-button v-if="row.status === 0" size="small" @click="$router.push('/teacher/courses/' + courseId + '/exams/' + row.id + '/edit')" round>编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="handlePublish(row)" round>发布</el-button>
            <el-button v-if="row.status === 1" size="small" @click="handleUnpublish(row)" round>下架</el-button>
            <el-button size="small" @click="handleDelete(row)" type="danger" round>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:center">
        <el-pagination v-if="total > pageSize" v-model:current-page="pageNum" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchExams" />
      </div>
      <el-empty v-if="!loading && filteredExams.length === 0" description="暂无考试" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { examPage, publishExam, deleteExam, updateExam } from '../../api/exam-online'
import { getCourse } from '../../api/course'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const courseId = route.params.id

const allExams = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const filterStatus = ref('')
const courseNames = ref({})

onMounted(() => { loadCourseName(); fetchExams() })

async function loadCourseName() {
  try {
    const c = await getCourse(courseId)
    courseNames.value[c.id] = c.courseName
  } catch (_) {}
}

const filteredExams = computed(() => {
  if (!filterStatus.value) return allExams.value
  if (filterStatus.value === '2') return allExams.value.filter(e => isEnded(e))
  return allExams.value.filter(e => e.status === Number(filterStatus.value))
})

function isEnded(e) {
  if (e.status !== 1) return false
  if (!e.endTime) return false
  return new Date(e.endTime) < new Date()
}

async function fetchExams() {
  loading.value = true
  const res = await examPage({ courseId, teacherId: store.user.id, pageNum: pageNum.value, pageSize })
  allExams.value = res.list
  total.value = res.total
  loading.value = false
}

async function handlePublish(row) {
  await publishExam(row.id)
  ElMessage.success('已发布')
  fetchExams()
}

async function handleUnpublish(row) {
  await updateExam({ ...row, status: 0 })
  ElMessage.success('已下架')
  fetchExams()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除考试「' + row.title + '」？')
  await deleteExam(row.id)
  ElMessage.success('已删除')
  fetchExams()
}
</script>
