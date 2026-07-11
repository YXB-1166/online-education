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

      <el-card style="margin-bottom:24px" v-if="course && String(course.status) !== '1'">
        <template #header>
          <div style="display:flex;align-items:center;gap:8px;font-weight:600">
            <el-icon><Reading /></el-icon> 课程资料
            <el-tag v-if="unfinishedMaterialCount > 0" type="warning" size="small" round>{{ unfinishedMaterialCount }} 份未读</el-tag>
          </div>
        </template>
        <el-button type="primary" @click="$router.push('/courses/' + course.id + '/materials')" round>查看资料</el-button>
      </el-card>

      <el-card v-if="course && String(course.status) === '3'" style="margin-bottom:24px">
        <template #header>
          <div style="display:flex;align-items:center;gap:8px;font-weight:600">
            <el-icon><DataAnalysis /></el-icon> 最终成绩
          </div>
        </template>
        <el-skeleton :loading="gradeLoading" animated>
          <div v-if="finalGrade !== null" class="grade-result">
            <div class="grade-big">{{ finalGrade }}</div>
            <div class="grade-label">总评</div>
            <el-divider style="margin:16px 0" />
            <div class="grade-detail">
              <div class="grade-item">
                <span class="grade-item-label">平时作业（占比 {{ course.homeworkRatio }}%）</span>
                <span class="grade-item-value">{{ homeworkAvg }} / 100</span>
              </div>
              <div class="grade-item">
                <span class="grade-item-label">期末考试（占比 {{ course.examRatio }}%）</span>
                <span class="grade-item-value">{{ examScore ?? '未考试' }}</span>
              </div>
            </div>
          </div>
          <div v-else style="text-align:center;padding:20px;color:#94a3b8">成绩尚未出，请等待教师批改</div>
        </el-skeleton>
      </el-card>

      <el-card v-if="course && String(course.status) !== '1'" style="margin-top:24px">
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
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { getCourse } from '../../api/course'
import { listUsers } from '../../api/user'
import { listMaterialsWithStatus } from '../../api/material'
import { listAssignments, listSubmissions } from '../../api/exam'
import { getMyExamRecords, examPage } from '../../api/exam-online'
import { DataAnalysis, Reading, ChatDotSquare } from '@element-plus/icons-vue'

const route = useRoute()
const store = useUserStore()
const course = ref(null)
const loading = ref(false)
const teacher = ref(null)
const materialStatus = ref([])
const gradeLoading = ref(false)
const homeworkAvg = ref(null)
const examScore = ref(null)
const finalGrade = ref(null)

const unfinishedMaterialCount = computed(() => {
  return materialStatus.value.filter(m => m.completed === 0).length
})

onMounted(async () => {
  loading.value = true
  course.value = await getCourse(route.params.id)
  const users = await listUsers()
  teacher.value = users.find(u => u.id === course.value.teacherId) || null
  try {
    materialStatus.value = await listMaterialsWithStatus(route.params.id, store.user.id)
  } catch (_) {}
  loading.value = false

  if (String(course.value.status) === '3') {
    loadFinalGrade()
  }
})

async function loadFinalGrade() {
  gradeLoading.value = true
  const c = course.value
  try {
    const [assignments, subs, examRecords, examPageRes] = await Promise.all([
      listAssignments({ courseId: c.id }).catch(() => []),
      listSubmissions({ studentId: store.user.id }).catch(() => []),
      getMyExamRecords(store.user.id).catch(() => []),
      examPage({ courseId: c.id, pageNum: 1, pageSize: 200 }).catch(() => ({ list: [] }))
    ])
    const subMap = {}
    subs.forEach(s => { subMap[s.assignmentId] = s })

    let totalScore = 0, totalFull = 0
    for (const a of assignments) {
      const s = subMap[a.id]
      if (s && s.score != null) {
        totalScore += s.score
        totalFull += a.fullScore || 100
      }
    }
    homeworkAvg.value = totalFull > 0 ? Math.round(totalScore / totalFull * 100) : 0

    const courseExams = examPageRes.list || []
    const courseExamIds = new Set(courseExams.map(e => e.id))
    const matchedRecord = examRecords.find(r => courseExamIds.has(r.examId))
    examScore.value = matchedRecord?.score ?? null

    const hr = c.homeworkRatio || 50
    const er = c.examRatio || 50
    if (examScore.value != null) {
      finalGrade.value = Math.round(homeworkAvg.value * hr / 100 + examScore.value * er / 100)
    } else if (hr === 100) {
      finalGrade.value = homeworkAvg.value
    } else {
      finalGrade.value = null
    }
  } catch (_) {}
  gradeLoading.value = false
}
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
.grade-result { text-align: center; padding: 8px 0; }
.grade-big { font-size: 48px; font-weight: 800; color: #4f46e5; }
.grade-label { color: #94a3b8; font-size: 14px; margin-top: 4px; }
.grade-detail { display: flex; flex-direction: column; gap: 8px; }
.grade-item { display: flex; justify-content: space-between; font-size: 14px; padding: 0 20px; }
.grade-item-label { color: #64748b; }
.grade-item-value { font-weight: 600; color: #1e293b; }
</style>
