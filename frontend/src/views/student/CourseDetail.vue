<template>
  <div>
    <div class="page-title">{{ course?.courseName || '课程详情' }}</div>
    <div v-loading="loading">
      <el-card v-if="course" style="margin-bottom:24px">
        <div style="display:flex;align-items:center;gap:20px;margin-bottom:20px">
          <div class="course-icon-lg">{{ course.courseName.charAt(0) }}</div>
          <div>
            <div style="font-size:20px;font-weight:700">{{ course.courseName }}</div>
            <div style="color:#64748b;font-size:14px;margin-top:4px">
              授课教师：{{ teacherName }} ｜ 容量：{{ course.enrolledCount }}/{{ course.maxStudents }}
            </div>
          </div>
        </div>
        <el-divider style="margin:12px 0" />
        <p style="color:#475569;line-height:1.8;margin:0">{{ course.description || '暂无课程介绍' }}</p>
      </el-card>

      <el-card style="margin-bottom:24px">
        <template #header>
          <div style="display:flex;align-items:center;gap:8px;font-weight:600">
            <el-icon><Document /></el-icon> 作业列表
          </div>
        </template>
        <el-table :data="assignments" stripe v-if="assignments.length > 0">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="fullScore" label="满分" width="80" />
          <el-table-column prop="deadline" label="截止时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="$router.push(`/courses/${course.id}/assignments/${row.id}/submit`)" round>提交</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-else style="text-align:center;padding:40px 0;color:#94a3b8">
          暂无作业
        </div>
      </el-card>

      <el-card>
        <template #header>
          <div style="display:flex;align-items:center;gap:8px;font-weight:600">
            <el-icon><ChatDotSquare /></el-icon> 课程论坛
          </div>
        </template>
        <el-button type="primary" @click="$router.push('/courses/' + course.id + '/forum')" round>进入论坛</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourse } from '../../api/course'
import { listAssignments } from '../../api/exam'
import { listUsers } from '../../api/user'

const route = useRoute()
const course = ref(null)
const assignments = ref([])
const loading = ref(false)
const teacherName = ref('')

onMounted(async () => {
  loading.value = true
  course.value = await getCourse(route.params.id)
  assignments.value = await listAssignments({ courseId: route.params.id })
  const users = await listUsers()
  const teacher = users.find(u => u.id === course.value.teacherId)
  teacherName.value = teacher ? teacher.realName : '未知'
  loading.value = false
})
</script>

<style scoped>
.course-icon-lg {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  flex-shrink: 0;
}
</style>
