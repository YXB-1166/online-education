<template>
  <div>
    <div class="page-title">我的成绩</div>
    <el-card>
      <el-table :data="mergedList" stripe v-loading="loading">
        <el-table-column label="课程名称" min-width="140">
          <template #default="{ row }">{{ row.courseName }}</template>
        </el-table-column>
        <el-table-column label="作业标题" min-width="140">
          <template #default="{ row }">{{ row.assignmentTitle }}</template>
        </el-table-column>
        <el-table-column label="分数" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.score != null" type="success" effect="dark" round>{{ row.score }}</el-tag>
            <el-tag v-else type="info" round>未批改</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评语" min-width="140" />
        <el-table-column prop="content" label="我的回答" show-overflow-tooltip min-width="200" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '../../stores/user'
import { listSubmissions } from '../../api/exam'
import { listAssignments } from '../../api/exam'
import { listCourses } from '../../api/course'

const store = useUserStore()
const submissions = ref([])
const assignments = ref([])
const courses = ref([])
const loading = ref(false)

const mergedList = computed(() => {
  const aMap = {}
  assignments.value.forEach(a => { aMap[a.id] = a })
  const cMap = {}
  courses.value.forEach(c => { cMap[c.id] = c })
  return submissions.value.map(s => {
    const a = aMap[s.assignmentId] || {}
    const c = cMap[a.courseId] || {}
    return {
      ...s,
      assignmentTitle: a.title || '未知作业',
      courseName: c.courseName || '未知课程'
    }
  })
})

onMounted(async () => {
  loading.value = true
  const [subs, asgns, coursesList] = await Promise.all([
    listSubmissions({ studentId: store.user.id }),
    listAssignments(),
    listCourses()
  ])
  submissions.value = subs
  assignments.value = asgns
  courses.value = coursesList
  loading.value = false
})
</script>
