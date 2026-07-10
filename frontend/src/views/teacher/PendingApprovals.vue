<template>
  <div>
    <div class="page-title">选课审核</div>
    <el-card>
      <div class="course-selector">
        <span class="label">选择课程：</span>
        <el-select v-model="selectedCourseId" placeholder="全部课程" size="large" filterable clearable style="width:360px">
          <el-option label="全部课程" :value="null" />
          <el-option v-for="c in myCourses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
      </div>
    </el-card>

    <el-card style="margin-top:16px">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column label="课程" min-width="140">
          <template #default="{ row }">{{ courseMap[row.courseId] || '课程' + row.courseId }}</template>
        </el-table-column>
        <el-table-column label="学生" width="140">
          <template #default="{ row }">{{ studentMap[row.studentId] || '学生' + row.studentId }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === '1'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === '4'" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">已退选</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="170">
          <template #default="{ row }">{{ row.selectTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <template v-if="row.status === '0'">
              <el-button size="small" type="success" @click="handleApprove(row)" round>通过</el-button>
              <el-button size="small" type="danger" @click="handleReject(row)" round>拒绝</el-button>
            </template>
            <el-tag v-else-if="row.status === '1'" type="success" effect="plain">已通过</el-tag>
            <el-tag v-else-if="row.status === '4'" type="danger" effect="plain">已拒绝</el-tag>
            <el-tag v-else type="info" effect="plain">已退选</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无选课记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { listCourses, listSelectionsByCourse, approveSelection, rejectSelection } from '../../api/course'
import { listUsers } from '../../api/user'

const store = useUserStore()
const myCourses = ref([])
const selectedCourseId = ref(null)
const allSelections = ref([])
const loading = ref(false)
const studentMap = ref({})
const courseMap = ref({})

const list = computed(() => {
  let items = allSelections.value
  if (selectedCourseId.value) {
    items = items.filter(s => s.courseId === selectedCourseId.value)
  }
  return [...items].sort((a, b) => {
    if (a.status === '0' && b.status !== '0') return -1
    if (a.status !== '0' && b.status === '0') return 1
    return new Date(b.selectTime) - new Date(a.selectTime)
  })
})

onMounted(async () => {
  loading.value = true
  const [courses, users] = await Promise.all([listCourses(), listUsers()])
  myCourses.value = courses.filter(c => c.teacherId === store.user.id)
  courses.forEach(c => { courseMap.value[c.id] = c.courseName })
  users.forEach(u => { studentMap.value[u.id] = u.realName || u.username })

  const results = await Promise.all(myCourses.value.map(c => listSelectionsByCourse(c.id, store.user.id)))
  allSelections.value = results.flat()
  loading.value = false
})

async function handleApprove(row) {
  await approveSelection(row.id)
  ElMessage.success('已通过')
  row.status = '1'
}

async function handleReject(row) {
  await rejectSelection(row.id)
  ElMessage.success('已拒绝')
  row.status = '4'
}
</script>

<style scoped>
.course-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}
.course-selector .label {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
}
</style>
