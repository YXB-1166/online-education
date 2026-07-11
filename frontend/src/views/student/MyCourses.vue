<template>
  <div class="page-wrap">
    <div class="page-title">我的课程</div>

    <div v-if="courses.length === 0" class="empty-state">
      <el-empty description="还没有选课，去课程广场看看吧">
        <el-button type="primary" @click="$router.push('/courses')" round>浏览课程</el-button>
      </el-empty>
    </div>

    <template v-else>
      <div class="filter-bar">
        <el-radio-group v-model="statusFilter" size="small" @change="pageNum=1">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="2">授课中</el-radio-button>
          <el-radio-button value="1">即将开课</el-radio-button>
          <el-radio-button value="3">已结课</el-radio-button>
        </el-radio-group>
        <span class="filter-stat">共 {{ filteredCourses.length }} 门</span>
      </div>
      <div class="course-grid">
        <el-card v-for="c in pageCourses" :key="c.id" class="course-card" shadow="hover">
          <div class="card-top">
            <div class="course-icon">{{ c.courseName.charAt(0) }}</div>
            <div class="course-meta">
              <span class="course-name">{{ c.courseName }}</span>
              <span class="course-teacher">{{ teacherMap[c.teacherId] || '未知' }}</span>
            </div>
          </div>
          <p class="course-desc">{{ c.description || '暂无介绍' }}</p>
          <div class="card-bottom">
            <div class="card-actions">
              <el-button size="small" plain @click="$router.push('/courses/' + c.id)" round>详情</el-button>
              <el-button size="small" plain @click="$router.push('/courses/' + c.id + '/forum')" round>论坛</el-button>
              <el-button size="small" plain @click="showSummary(c)" round>摘要</el-button>
              <el-button v-if="String(c.status) !== '3'" size="small" type="danger" plain @click="handleDrop(c.id)" round>退课</el-button>
            </div>
          </div>
        </el-card>
      </div>
      <div class="pagination-bar">
        <el-pagination
          v-if="courses.length > pageSize"
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="courses.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </template>

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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { myCourses, dropCourse, getChapters, generateChapters } from '../../api/course'
import { listUsers } from '../../api/user'

const store = useUserStore()
const courses = ref([])
const teacherMap = ref({})
const dialogVisible = ref(false)
const currentCourse = ref(null)
const chapters = ref([])
const generating = ref(false)
const pageNum = ref(1)
const pageSize = 9
const statusFilter = ref('')

const filteredCourses = computed(() => {
  if (!statusFilter.value) return courses.value
  return courses.value.filter(c => String(c.status) === statusFilter.value)
})

const pageCourses = computed(() => {
  const start = (pageNum.value - 1) * pageSize
  return filteredCourses.value.slice(start, start + pageSize)
})

onMounted(async () => {
  const [list, users] = await Promise.all([myCourses(store.user.id), listUsers()])
  users.forEach(u => { if (u.role === 2) teacherMap.value[u.id] = u.realName })
  courses.value = list.sort((a, b) => a.id - b.id)
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
  courses.value = (await myCourses(store.user.id)).sort((a, b) => a.id - b.id)
}

function importanceType(imp) {
  if (imp === 'high') return 'danger'
  if (imp === 'medium') return 'warning'
  return 'info'
}
</script>

<style scoped>
.page-wrap {
  max-width: 1060px;
  margin: 0 auto;
}
.course-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.filter-stat {
  font-size: 13px;
  color: #94a3b8;
  margin-left: auto;
}
.course-card {
  border-radius: 12px !important;
  border: 1px solid #eef2f6;
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
}
.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px -8px rgba(0,0,0,0.12) !important;
}
.course-card :deep(.el-card__body) { padding: 18px; display: flex; flex-direction: column; height: 100%; }
.card-top {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}
.course-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  font-weight: 700;
  flex-shrink: 0;
}
.course-card:nth-child(6n+1) .course-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.course-card:nth-child(6n+2) .course-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.course-card:nth-child(6n+3) .course-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.course-card:nth-child(6n+4) .course-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.course-card:nth-child(6n+5) .course-icon { background: linear-gradient(135deg, #fa709a, #fee140); }
.course-card:nth-child(6n+6) .course-icon { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }

.course-card::before {
  content: '';
  display: block;
  height: 3px;
}
.course-card:nth-child(6n+1)::before { background: linear-gradient(90deg, #667eea, #764ba2); }
.course-card:nth-child(6n+2)::before { background: linear-gradient(90deg, #f093fb, #f5576c); }
.course-card:nth-child(6n+3)::before { background: linear-gradient(90deg, #4facfe, #00f2fe); }
.course-card:nth-child(6n+4)::before { background: linear-gradient(90deg, #43e97b, #38f9d7); }
.course-card:nth-child(6n+5)::before { background: linear-gradient(90deg, #fa709a, #fee140); }
.course-card:nth-child(6n+6)::before { background: linear-gradient(90deg, #a18cd1, #fbc2eb); }
.course-meta {
  flex: 1;
  min-width: 0;
}
.course-name {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course-teacher {
  font-size: 12px;
  color: #94a3b8;
}
.enrolled-tag {
  flex-shrink: 0;
}
.course-desc {
  flex: 1;
  font-size: 12px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 19.2px;
}
.card-bottom {
  display: flex;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}
.card-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  width: 100%;
}
.card-actions .el-button {
  flex: 1;
  min-width: 60px;
  margin: 0;
}
.empty-state {
  padding: 60px 0;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-bottom: 16px;
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
