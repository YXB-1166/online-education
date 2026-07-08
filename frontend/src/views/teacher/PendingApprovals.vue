<template>
  <div>
    <div class="page-title">选课审核</div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column label="课程" min-width="140">
          <template #default="{ row }">{{ courseMap[row.courseId] || '课程' + row.courseId }}</template>
        </el-table-column>
        <el-table-column label="学生" width="120">
          <template #default="{ row }">{{ studentMap[row.studentId] || '学生' + row.studentId }}</template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">{{ row.selectTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleApprove(row)" round>通过</el-button>
            <el-button size="small" type="danger" @click="handleReject(row)" round>拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无待审核选课" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { listPendingSelections, approveSelection, rejectSelection, listCourses } from '../../api/course'
import { listUsers } from '../../api/user'

const store = useUserStore()
const list = ref([])
const loading = ref(false)
const courseMap = ref({})
const studentMap = ref({})

onMounted(async () => {
  loading.value = true
  const [pending, courses, users] = await Promise.all([
    listPendingSelections(store.user.id),
    listCourses(),
    listUsers()
  ])
  list.value = pending
  courses.forEach(c => { courseMap.value[c.id] = c.courseName })
  users.forEach(u => { studentMap.value[u.id] = u.realName || u.username })
  loading.value = false
})

async function handleApprove(row) {
  await approveSelection(row.id)
  ElMessage.success('已通过')
  list.value = await listPendingSelections(store.user.id)
}

async function handleReject(row) {
  await rejectSelection(row.id)
  ElMessage.success('已拒绝')
  list.value = await listPendingSelections(store.user.id)
}
</script>
