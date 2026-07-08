<template>
  <div>
    <div class="page-title">我的课程</div>
    <div v-if="courses.length === 0" style="text-align:center;padding:60px 0;color:#94a3b8">
      <el-icon :size="48"><Notebook /></el-icon>
      <p style="margin-top:16px">还没有选课，去课程广场看看吧</p>
      <el-button type="primary" @click="$router.push('/courses')" round>浏览课程</el-button>
    </div>
    <div v-else class="course-grid">
      <el-card v-for="c in courses" :key="c.id" class="course-card" shadow="hover">
        <div class="course-card-top">
          <div class="course-icon" :style="{ background: colors[c.id % colors.length] }">
            {{ c.courseName.charAt(0) }}
          </div>
          <div class="course-meta">
            <span class="course-name">{{ c.courseName }}</span>
            <span class="course-teacher">
              <el-icon><User /></el-icon>{{ teacherMap[c.teacherId] || '未知' }}
            </span>
          </div>
          <el-tag size="small" type="success" effect="plain" style="margin-left:auto">已选课</el-tag>
        </div>
        <p class="course-desc">{{ c.description || '暂无介绍' }}</p>
        <div class="course-actions">
          <el-button size="small" @click="$router.push('/courses/' + c.id)" round>详情</el-button>
          <el-button size="small" @click="$router.push('/courses/' + c.id + '/forum')" round>论坛</el-button>
          <el-button size="small" type="primary" @click="showSummary(c)" round>知识点摘要</el-button>
          <el-button size="small" type="danger" @click="handleDrop(c.id)" round plain>退课</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" :title="currentCourse?.courseName + ' - 章节知识点摘要'" width="700px" top="5vh">
      <div v-if="chapters.length === 0" style="text-align:center;padding:30px;color:#94a3b8">
        <p>暂无章节知识点数据</p>
        <el-button v-if="store.user?.role === 2 || store.user?.role === 3" type="primary" @click="handleAutoGenerate" :loading="generating" style="margin-top:12px">
          自动生成章节知识点
        </el-button>
      </div>
      <div v-else class="summary-list">
        <div v-for="ch in chapters" :key="ch.id" class="chapter-item">
          <div class="chapter-header">
            <div class="chapter-title">
              <el-icon color="#6366f1"><Reading /></el-icon>
              <span>{{ ch.title }}</span>
            </div>
            <el-tag v-if="ch.summary" size="small" type="info" effect="plain">{{ ch.summary }}</el-tag>
          </div>
          <div class="kp-list">
            <div v-for="(kp, kpi) in ch.knowledgePoints" :key="kp.id || (ch.id + '-' + kpi)" class="kp-item">
              <el-tag :type="importanceType(kp.importance)" size="small" effect="dark" round>
                {{ kp.importance === 'high' ? '重点' : kp.importance === 'medium' ? '掌握' : '了解' }}
              </el-tag>
              <span>{{ kp.content }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="chapters.length > 0 && (store.user?.role === 2 || store.user?.role === 3)" type="primary" @click="handleAutoGenerate" :loading="generating">
          重新生成
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { myCourses, dropCourse, getChapters, generateChapters } from '../../api/course'
import { listUsers } from '../../api/user'

const store = useUserStore()
const courses = ref([])
const teacherMap = ref({})
const colors = ['#667eea','#22c55e','#eab308','#ef4444','#ec4899','#06b6d4']
const dialogVisible = ref(false)
const currentCourse = ref(null)
const chapters = ref([])
const generating = ref(false)

onMounted(async () => {
  const [list, users] = await Promise.all([myCourses(store.user.id), listUsers()])
  users.forEach(u => { if (u.role === 2) teacherMap.value[u.id] = u.realName })
  courses.value = list
})

async function showSummary(c) {
  currentCourse.value = c
  chapters.value = []
  dialogVisible.value = true
  const res = await getChapters(c.id)
  chapters.value = res
}

async function handleAutoGenerate() {
  generating.value = true
  try {
    const res = await generateChapters(currentCourse.value.id)
    chapters.value = res
    ElMessage.success('章节知识点已自动生成')
  } finally {
    generating.value = false
  }
}

async function handleDrop(courseId) {
  await dropCourse(store.user.id, courseId)
  ElMessage.success('退课成功')
  courses.value = await myCourses(store.user.id)
}

function importanceType(imp) {
  if (imp === 'high') return 'danger'
  if (imp === 'medium') return 'warning'
  return 'info'
}
</script>

<style scoped>
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
.course-card {
  border-radius: 14px !important;
  transition: transform 0.2s, box-shadow 0.2s;
}
.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -8px rgba(0,0,0,0.15) !important;
}
.course-card-top {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 12px;
}
.course-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
}
.course-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}
.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}
.course-teacher {
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}
.course-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.course-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}
.summary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.chapter-item {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 14px;
  background: #fafbff;
}
.chapter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}
.chapter-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
}
.kp-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-left: 26px;
}
.kp-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #475569;
}
</style>
