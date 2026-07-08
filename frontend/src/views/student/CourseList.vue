<template>
  <div>
    <div class="page-title">课程广场</div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索课程名称或关键词..." clearable
        @keyup.enter="handleSearch" style="width:360px" />
      <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
    </div>

    <div class="course-grid">
      <el-card v-for="c in list" :key="c.id" class="course-card" shadow="hover">
        <div class="course-card-top">
          <div class="course-icon">{{ c.courseName.charAt(0) }}</div>
          <div class="course-meta">
            <span class="course-name">{{ c.courseName }}</span>
            <span class="course-teacher">
              <el-icon><User /></el-icon>
              {{ teacherMap[c.teacherId] || '未知' }}
            </span>
          </div>
        </div>
        <p class="course-desc">{{ c.description || '暂无介绍' }}</p>
        <div class="course-stats">
          <div class="stat-item">
            <span class="stat-num">{{ c.enrolledCount }}</span>
            <span class="stat-label">已选 / {{ c.maxStudents }}</span>
          </div>
        </div>
        <div class="course-actions">
          <el-button size="small" @click="$router.push('/courses/' + c.id)" round>查看详情</el-button>
          <el-button v-if="store.user?.role === 1" size="small" type="primary" @click="handleSelect(c.id)" round>
            选课
          </el-button>
        </div>
      </el-card>
    </div>
    <div style="margin-top:24px;text-align:center">
      <el-pagination
        v-if="total > pageSize"
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { coursePage, selectCourse } from '../../api/course'
import { listUsers } from '../../api/user'

const store = useUserStore()
const list = ref([])
const teacherMap = ref({})
const total = ref(0)
const pageNum = ref(1)
const pageSize = 8
const keyword = ref('')

onMounted(async () => {
  const [users] = await Promise.all([listUsers()])
  users.forEach(u => { if (u.role === 2) teacherMap.value[u.id] = u.realName })
  fetchData()
})

function handleSearch() {
  pageNum.value = 1
  fetchData()
}

async function fetchData() {
  const params = { pageNum: pageNum.value, pageSize }
  if (keyword.value.trim()) params.courseName = keyword.value.trim()
  const res = await coursePage(params)
  list.value = res.list
  total.value = res.total
}

async function handleSelect(courseId) {
  await selectCourse(store.user.id, courseId)
  ElMessage.success('已提交选课申请，等待教师审核')
  fetchData()
}
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
.course-card {
  border-radius: 14px !important;
  overflow: hidden;
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
  background: linear-gradient(135deg, #667eea, #764ba2);
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
.course-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}
.stat-item {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #4f46e5;
}
.stat-label {
  font-size: 12px;
  color: #94a3b8;
}
.course-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}
</style>
