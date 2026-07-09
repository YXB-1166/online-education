<template>
  <div>
    <div class="page-title">{{ course?.courseName || '课程详情' }}</div>
    <div v-loading="loading">
      <div style="display:grid;grid-template-columns:2fr 1fr;gap:24px;margin-bottom:24px;align-items:start">
        <el-card v-if="course">
          <div style="display:flex;align-items:center;gap:20px;margin-bottom:20px">
            <div class="course-icon-lg">{{ course.courseName.charAt(0) }}</div>
            <div>
              <div style="font-size:20px;font-weight:700">{{ course.courseName }}</div>
              <div style="color:#64748b;font-size:14px;margin-top:4px">
                授课教师：{{ teacher?.realName || '未知' }} ｜ 容量：{{ course.enrolledCount }}/{{ course.maxStudents }}
              </div>
            </div>
          </div>
          <el-divider style="margin:12px 0" />
          <div style="display:flex;gap:32px;margin-bottom:12px;flex-wrap:wrap">
            <div><span style="color:#64748b">成绩占比：</span>作业 {{ course.homeworkRatio || '-' }}% / 考试 {{ course.examRatio || '-' }}%</div>
            <div><span style="color:#64748b">考试时间：</span>{{ course.examTime ? course.examTime.replace('T', ' ') : '待定' }}</div>
          </div>
          <p style="color:#475569;line-height:1.8;margin:0">{{ course.description || '暂无课程介绍' }}</p>
        </el-card>

        <el-card v-if="teacher">
          <template #header><span style="font-weight:600">授课教师</span></template>
          <div style="text-align:center;padding:8px 0">
            <div class="teacher-avatar">{{ teacher.realName?.charAt(0) || '?' }}</div>
            <div style="font-size:18px;font-weight:700;margin-top:8px">{{ teacher.realName }}</div>
            <div style="color:#667eea;font-size:14px;margin-top:4px">{{ teacher.title || '教师' }}</div>
            <el-divider style="margin:16px 0" />
            <div style="text-align:left;font-size:14px;color:#475569;line-height:2">
              <div><span style="color:#94a3b8">邮箱：</span>{{ teacher.email || '未设置' }}</div>
              <div><span style="color:#94a3b8">电话：</span>{{ teacher.phone || '未设置' }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <el-card style="margin-bottom:24px">
        <template #header>
          <div style="display:flex;align-items:center;justify-content:space-between">
            <div style="display:flex;align-items:center;gap:8px;font-weight:600">
              <el-icon><Document /></el-icon> 作业列表
            </div>
            <el-switch v-model="showUnfinishedOnly" active-text="仅显示未完成" size="small" />
          </div>
        </template>
        <el-table :data="filteredAssignments" stripe v-if="filteredAssignments.length > 0">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="fullScore" label="满分" width="80" />
          <el-table-column prop="deadline" label="截止时间" width="180" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="submissionMap[row.id]" type="success" size="small" round>已提交</el-tag>
              <el-tag v-else type="info" size="small" round>未提交</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button v-if="btnInfo(row).show" :type="btnInfo(row).type" size="small" :disabled="btnInfo(row).disabled" @click="$router.push(`/courses/${course.id}/assignments/${row.id}/submit`)" round>
                {{ btnInfo(row).text }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-else style="text-align:center;padding:40px 0;color:#94a3b8">
          {{ showUnfinishedOnly ? '所有作业已完成' : '暂无作业' }}
        </div>
      </el-card>

      <el-card>
        <template #header>
          <div style="display:flex;align-items:center;gap:8px;font-weight:600">
            <el-icon><Reading /></el-icon> 课程资料
            <el-tag v-if="unfinishedMaterialCount > 0" type="warning" size="small" round>{{ unfinishedMaterialCount }} 份未读</el-tag>
          </div>
        </template>
        <el-button type="primary" @click="$router.push('/courses/' + course.id + '/materials')" round>查看资料</el-button>
      </el-card>

      <el-card style="margin-top:24px">
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { getCourse } from '../../api/course'
import { listAssignments, listSubmissions, listResubmitOpportunities } from '../../api/exam'
import { listUsers } from '../../api/user'
import { listMaterialsWithStatus } from '../../api/material'

const route = useRoute()
const store = useUserStore()
const course = ref(null)
const assignments = ref([])
const loading = ref(false)
const teacher = ref(null)
const submissionMap = ref({})
const makeupMap = ref({})
const showUnfinishedOnly = ref(false)
const materialStatus = ref([])

const filteredAssignments = computed(() => {
  if (!showUnfinishedOnly.value) return assignments.value
  return assignments.value.filter(a => !submissionMap.value[a.id])
})

const unfinishedMaterialCount = computed(() => {
  return materialStatus.value.filter(m => m.completed === 0).length
})

function btnInfo(row) {
  const sub = submissionMap.value[row.id]
  const submitted = !!sub
  const submitCount = sub?.submitCount || 0
  const maxCount = row.allowSubmitCount || 1
  const deadlinePassed = row.deadline && new Date(row.deadline) < new Date()
  const hasMakeup = makeupMap.value[row.id] && new Date(makeupMap.value[row.id].deadline) > new Date()
  if (!submitted && deadlinePassed && !hasMakeup) return { show: false }
  if (!submitted && deadlinePassed && hasMakeup) return { show: true, text: '补交', type: 'warning', disabled: false }
  if (!submitted) return { show: true, text: '提交', type: 'primary', disabled: false }
  if (submitted && deadlinePassed && hasMakeup) return { show: true, text: '补交', type: 'warning', disabled: false }
  if (submitted && deadlinePassed && !hasMakeup) return { show: false }
  if (submitted && !deadlinePassed) {
    if (maxCount === 1) return { show: false, text: '已提交', type: 'info', disabled: true }
    if (submitCount >= maxCount) return { show: false }
    return { show: true, text: '再次提交', type: 'primary', disabled: false }
  }
  return { show: false }
}

onMounted(async () => {
  loading.value = true
  course.value = await getCourse(route.params.id)
  assignments.value = await listAssignments({ courseId: route.params.id })
  const users = await listUsers()
  teacher.value = users.find(u => u.id === course.value.teacherId) || null
  const subs = await listSubmissions({ studentId: store.user.id })
  const map = {}
  subs.forEach(s => { map[s.assignmentId] = s })
  submissionMap.value = map
  try {
    materialStatus.value = await listMaterialsWithStatus(route.params.id, store.user.id)
  } catch (_) {}
  try {
    const opps = await listResubmitOpportunities(store.user.id)
    const mm = {}
    opps.forEach(o => { mm[o.assignmentId] = o })
    makeupMap.value = mm
  } catch (_) {}
  loading.value = false
})
</script>

<style scoped>
.teacher-avatar {
  width: 64px; height: 64px; border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; display: inline-flex; align-items: center; justify-content: center;
  font-size: 28px; font-weight: 700;
}
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
