<template>
  <div>
    <div class="page-title">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      {{ courseName }} — 学生名单
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column label="学生姓名" min-width="120">
          <template #default="{ row }">{{ studentMap[row.studentId] || '学生' + row.studentId }}</template>
        </el-table-column>
        <el-table-column label="选课时间" width="180">
          <template #default="{ row }">{{ row.selectTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="plain" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="退选时间" width="180">
          <template #default="{ row }">{{ row.dropTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="成绩" width="80">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无选课记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { listSelectionsByCourse, listCourses } from '../../api/course'
import { listUsers } from '../../api/user'

const route = useRoute()
const store = useUserStore()
const list = ref([])
const loading = ref(false)
const courseName = ref('')
const studentMap = ref({})

const statusMap = { '0': '待审核', '1': '已选课', '2': '已退选', '3': '已结课', '4': '审核不通过' }
const statusTypeMap = { '0': 'warning', '1': 'success', '2': 'info', '3': 'primary', '4': 'danger' }

function statusLabel(s) { return statusMap[s] || '未知' }
function statusType(s) { return statusTypeMap[s] || 'info' }

onMounted(async () => {
  loading.value = true
  const courseId = route.params.id
  const [selections, courses, users] = await Promise.all([
    listSelectionsByCourse(courseId, store.user.id),
    listCourses(),
    listUsers()
  ])
  list.value = selections
  const c = courses.find(x => x.id === Number(courseId))
  if (c) courseName.value = c.courseName
  users.forEach(u => { studentMap.value[u.id] = u.realName || u.username })
  loading.value = false
})
</script>