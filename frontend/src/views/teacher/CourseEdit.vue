<template>
  <div>
    <div class="page-title">编辑课程</div>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:24px;align-items:start">
      <el-card v-loading="loading">
        <template #header><span style="font-weight:600">基本信息</span></template>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="form.courseName" />
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="学分" prop="credit">
            <el-input-number v-model="form.credit" :min="1" :max="10" />
          </el-form-item>
          <el-form-item label="容量" prop="maxStudents">
            <el-input-number v-model="form.maxStudents" :min="1" :max="200" />
          </el-form-item>
          <el-form-item label="作业占比">
            <el-input-number v-model="form.homeworkRatio" :min="0" :max="100" /> %
          </el-form-item>
          <el-form-item label="考试占比">
            <el-input-number v-model="form.examRatio" :min="0" :max="100" /> %
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" round>保存</el-button>
            <el-button @click="$router.back()" round>返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div style="display:flex;flex-direction:column;gap:24px">
        <el-card>
          <template #header><span style="font-weight:600">考试时间</span></template>
          <el-date-picker v-model="examTime" type="datetime" placeholder="选择考试时间" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm" style="width:100%" />
          <div style="margin-top:12px;text-align:right">
            <el-button type="primary" @click="handleSetExamTime" :loading="settingExam" round>更新考试时间</el-button>
          </div>
        </el-card>

        <el-card>
          <template #header><span style="font-weight:600">学生名单（{{ students.length }}人）</span></template>
          <el-table :data="students" stripe size="small" v-loading="loadingStudents" max-height="300">
            <el-table-column prop="studentId" label="ID" width="50" />
            <el-table-column prop="realName" label="姓名" />
            <el-table-column prop="studentStatus" label="选课状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status === '1'" type="success" size="small">已选课</el-tag>
                <el-tag v-else-if="row.status === '0'" type="warning" size="small">待审核</el-tag>
                <el-tag v-else-if="row.status === '4'" type="danger" size="small">未通过</el-tag>
                <el-tag v-else size="small">已退选</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { getCourse, updateCourse, setExamTime, listSelectionsByCourse } from '../../api/course'
import { listUsers } from '../../api/user'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ courseName: '', description: '', credit: 4, maxStudents: 50, id: null, homeworkRatio: 50, examRatio: 50 })
const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
}
const examTime = ref(null)
const settingExam = ref(false)
const students = ref([])
const loadingStudents = ref(false)

onMounted(async () => {
  loading.value = true
  const c = await getCourse(route.params.id)
  Object.assign(form, c)
  examTime.value = c.examTime || null
  loading.value = false
  await loadStudents()
})

async function loadStudents() {
  loadingStudents.value = true
  try {
    const list = await listSelectionsByCourse(route.params.id, store.user.id)
    const users = await listUsers()
    const userMap = {}
    users.forEach(u => { userMap[u.id] = u.realName })
    students.value = list.map(s => ({ ...s, realName: userMap[s.studentId] || '未知', studentStatus: s.status }))
  } catch (_) { students.value = [] }
  loadingStudents.value = false
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  if (form.homeworkRatio + form.examRatio !== 100) {
    ElMessage.warning('作业占比 + 考试占比必须等于 100%')
    return
  }
  await updateCourse(form)
  ElMessage.success('保存成功')
  router.push('/teacher/courses')
}

function formatDate(d) {
  if (!d) return null
  const dt = new Date(d)
  if (isNaN(dt.getTime())) return d
  const y = dt.getFullYear()
  const m = String(dt.getMonth() + 1).padStart(2, '0')
  const day = String(dt.getDate()).padStart(2, '0')
  const h = String(dt.getHours()).padStart(2, '0')
  const mi = String(dt.getMinutes()).padStart(2, '0')
  const s = String(dt.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${mi}:${s}`
}

async function handleSetExamTime() {
  settingExam.value = true
  await setExamTime(route.params.id, formatDate(examTime.value))
  ElMessage.success('考试时间已更新，通知已推送给学生')
  settingExam.value = false
}
</script>
