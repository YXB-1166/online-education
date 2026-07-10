<template>
  <div>
    <div class="page-title">课程管理</div>
    <el-card>
      <div style="margin-bottom:16px">
        <el-radio-group v-model="filterStatus" size="small">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="0">审核中</el-radio-button>
          <el-radio-button value="1">已开课</el-radio-button>
          <el-radio-button value="3">已结课</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column label="授课教师" width="120">
          <template #default="{ row }">{{ teacherMap[row.teacherId] || '未知' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="String(row.status) === '0'" type="warning">待审核</el-tag>
            <el-tag v-else-if="String(row.status) === '1'" type="success">即将开课</el-tag>
            <el-tag v-else-if="String(row.status) === '2'" type="primary">授课中</el-tag>
            <el-tag v-else-if="String(row.status) === '3'">已结课</el-tag>
            <el-tag v-else-if="String(row.status) === '4'" type="danger">审核不通过</el-tag>
            <el-tag v-else type="info">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="maxStudents" label="容量" width="70" />
        <el-table-column prop="enrolledCount" label="已选" width="70" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="String(row.status) === '0'" size="small" type="primary" round @click="handleApprove(row.id)">通过</el-button>
            <el-button v-if="String(row.status) === '0'" size="small" type="warning" round @click="handleReject(row.id)">驳回</el-button>
            <el-button size="small" type="danger" round @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCourses, deleteCourse, approveCourse, rejectCourse } from '../../api/course'
import { listUsers } from '../../api/user'

const allCourses = ref([])
const loading = ref(false)
const teacherMap = ref({})
const filterStatus = ref('')

const courses = computed(() => {
  let items = allCourses.value
  const s = filterStatus.value
  if (s === '0') items = items.filter(c => String(c.status) === '0')
  else if (s === '1') items = items.filter(c => String(c.status) === '1' || String(c.status) === '2')
  else if (s === '3') items = items.filter(c => String(c.status) === '3')
  return [...items].sort((a, b) => {
    if (String(a.status) === '0' && String(b.status) !== '0') return -1
    if (String(a.status) !== '0' && String(b.status) === '0') return 1
    return 0
  })
})

onMounted(async () => {
  loading.value = true
  const [users] = await Promise.all([listUsers()])
  users.forEach(u => { if (u.role === 2) teacherMap.value[u.id] = u.realName })
  allCourses.value = await listCourses()
  loading.value = false
})

async function handleApprove(id) {
  await ElMessageBox.confirm('确认通过该课程审核？')
  await approveCourse(id)
  ElMessage.success('已通过')
  const c = allCourses.value.find(x => x.id === id)
  if (c) c.status = 1
}

async function handleReject(id) {
  await ElMessageBox.confirm('确认驳回该课程？')
  await rejectCourse(id)
  ElMessage.success('已驳回')
  const c = allCourses.value.find(x => x.id === id)
  if (c) c.status = 4
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该课程？')
  await deleteCourse(id)
  ElMessage.success('删除成功')
  allCourses.value = allCourses.value.filter(c => c.id !== id)
}
</script>
