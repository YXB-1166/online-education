<template>
  <div>
    <div class="page-title">我的课程</div>
    <el-card>
      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="enrolledCount" label="已选/容量" width="100">
          <template #default="{ row }">{{ row.enrolledCount }}/{{ row.maxStudents }}</template>
        </el-table-column>
        <el-table-column label="成绩占比" width="130">
          <template #default="{ row }">作业{{ row.homeworkRatio || '-' }}% / 考试{{ row.examRatio || '-' }}%</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === '1'" type="success" size="small">即将开课</el-tag>
            <el-tag v-else-if="row.status === '2'" type="primary" size="small">授课中</el-tag>
            <el-tag v-else-if="row.status === '3'" size="small">已结课</el-tag>
            <el-tag v-else-if="row.status === '4'" type="danger" size="small">审核不通过</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="400">
          <template #default="{ row }">
            <el-button v-if="row.status === '1'" size="small" type="success" @click="openStartDialog(row)" round>开课</el-button>
            <el-button v-if="row.status === '2'" size="small" type="warning" @click="handleEnd(row)" round>结课</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.id + '/edit')" round>编辑</el-button>
            <el-button size="small" type="primary" @click="$router.push('/teacher/courses/' + row.id + '/assignments')" round>作业与资料</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.id + '/forum')" round>论坛</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { myTeaching, startCourse, endCourse } from '../../api/course'

const store = useUserStore()
const courses = ref([])
const loading = ref(false)
const startDialogVisible = ref(false)
const starting = ref(false)
const startForm = ref({ homeworkRatio: 50, examRatio: 50, examTime: null })
const currentCourseId = ref(null)

onMounted(async () => {
  loading.value = true
  courses.value = await myTeaching(store.user.id)
  loading.value = false
})

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
  courses.value = await myTeaching(store.user.id)
  starting.value = false
}

async function handleEnd(row) {
  await ElMessageBox.confirm('确认结课？结课后学生将无法提交作业。')
  await endCourse(row.id)
  ElMessage.success('已结课')
  courses.value = await myTeaching(store.user.id)
}
</script>
