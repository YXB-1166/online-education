<template>
  <div>
    <div class="page-title">我的课程</div>
    <div class="course-grid" v-loading="loading">
      <div v-for="course in pagedCourses" :key="course.id" class="course-card">
        <div class="card-top" :style="{ background: getColor(course.id) }"></div>
        <div class="card-body">
          <div class="card-header">
            <h3 class="card-title">{{ course.courseName }}</h3>
            <el-tag v-if="course.status === '0'" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="course.status === '1'" type="success" size="small">即将开课</el-tag>
            <el-tag v-else-if="course.status === '2'" type="primary" size="small">授课中</el-tag>
            <el-tag v-else-if="course.status === '3'" size="small">已结课</el-tag>
            <el-tag v-else-if="course.status === '4'" type="danger" size="small">审核不通过</el-tag>
          </div>
          <div class="card-meta">
            <span>{{ course.credit }} 学分</span>
            <span>已选 {{ course.enrolledCount }}/{{ course.maxStudents }}</span>
            <span v-if="course.homeworkRatio">作业 {{ course.homeworkRatio }}% / 考试 {{ course.examRatio }}%</span>
          </div>
          <el-divider style="margin:10px 0" />
          <div class="card-actions">
            <el-button v-if="course.status === '1'" size="small" type="success" @click="openStartDialog(course)" round plain>开课</el-button>
            <el-button v-if="course.status === '2'" size="small" type="warning" @click="handleEnd(course)" round plain>结课</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + course.id + '/edit')" round plain>编辑</el-button>
            <el-button size="small" type="primary" @click="$router.push('/teacher/courses/' + course.id + '/assignments')" round plain>作业与资料</el-button>
            <el-button size="small" type="success" @click="$router.push('/teacher/courses/' + course.id + '/exams')" round plain>考试管理</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + course.id + '/forum')" round plain>论坛</el-button>
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && courses.length === 0" description="暂无课程" />
    </div>
    <div style="margin-top:20px;text-align:center">
      <el-pagination v-if="total > pageSize" v-model:current-page="pageNum" :page-size="pageSize" :total="total" layout="prev, pager, next" />
    </div>

    <el-dialog v-model="startDialogVisible" title="开课设置" width="420">
      <el-form :model="startForm" label-width="120px">
        <el-form-item label="平时作业占比">
          <el-input-number v-model="startForm.homeworkRatio" :min="0" :max="100" /> %
        </el-form-item>
        <el-form-item label="期末考试占比">
          <el-input-number v-model="startForm.examRatio" :min="0" :max="100" /> %
        </el-form-item>
        <el-form-item label="考试时间">
          <el-date-picker v-model="startForm.examTime" type="datetime" placeholder="选择考试时间" format="YYYY-MM-DD HH:mm" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startDialogVisible = false" round>取消</el-button>
        <el-button type="primary" @click="handleStart" :loading="starting" round>确认开课</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { myTeaching, startCourse, endCourse } from '../../api/course'

const store = useUserStore()
const courses = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 12
const startDialogVisible = ref(false)
const starting = ref(false)
const startForm = ref({ homeworkRatio: 50, examRatio: 50, examTime: null })
const currentCourseId = ref(null)

const total = computed(() => courses.value.length)
const pagedCourses = computed(() =>
  courses.value.slice((pageNum.value - 1) * pageSize, pageNum.value * pageSize)
)

const colors = ['#1677ff','#6366f1','#8b5cf6','#ec4899','#f59e0b','#10b981','#06b6d4','#f97316']

onMounted(async () => {
  loading.value = true
  courses.value = (await myTeaching(store.user.id)).sort((a, b) => a.id - b.id)
  loading.value = false
})

function getColor(id) { return colors[id % colors.length] }

function openStartDialog(row) {
  currentCourseId.value = row.id
  startForm.value = { homeworkRatio: 50, examRatio: 50, examTime: null }
  startDialogVisible.value = true
}

function formatDate(d) {
  if (!d) return null
  const dt = new Date(d)
  if (isNaN(dt.getTime())) return null
  const y = dt.getFullYear()
  const m = String(dt.getMonth() + 1).padStart(2, '0')
  const day = String(dt.getDate()).padStart(2, '0')
  const h = String(dt.getHours()).padStart(2, '0')
  const mi = String(dt.getMinutes()).padStart(2, '0')
  const s = String(dt.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${mi}:${s}`
}

async function handleStart() {
  if (startForm.value.homeworkRatio + startForm.value.examRatio !== 100) {
    ElMessage.warning('作业占比 + 考试占比必须等于 100%')
    return
  }
  starting.value = true
  await startCourse(currentCourseId.value, startForm.value.homeworkRatio, startForm.value.examRatio, formatDate(startForm.value.examTime))
  ElMessage.success('开课成功，通知已推送给学生')
  startDialogVisible.value = false
  courses.value = (await myTeaching(store.user.id)).sort((a, b) => a.id - b.id)
  pageNum.value = 1
  starting.value = false
}

async function handleEnd(row) {
  await ElMessageBox.confirm('确认结课？结课后学生将无法提交作业。')
  await endCourse(row.id)
  ElMessage.success('已结课')
  courses.value = (await myTeaching(store.user.id)).sort((a, b) => a.id - b.id)
  pageNum.value = 1
}
</script>

<style scoped>
.course-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.course-card {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  transition: transform 0.2s, box-shadow 0.2s;
}
.course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}
.card-top {
  height: 6px;
}
.card-body {
  padding: 16px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-meta {
  display: flex;
  gap: 12px;
  color: #64748b;
  font-size: 13px;
  flex-wrap: wrap;
}
.card-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.card-actions .el-button {
  margin: 0;
}

@media (max-width: 1100px) {
  .course-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 700px) {
  .course-grid { grid-template-columns: 1fr; }
}
</style>
