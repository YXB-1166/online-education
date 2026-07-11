<template>
  <div class="page-wrap">
    <div class="page-title">课程广场</div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索课程名称或关键词..." clearable
        @keyup.enter="handleSearch" style="width:360px" />
      <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
    </div>

    <div v-if="list.length === 0" class="empty-state">
      <el-empty description="暂无课程数据" />
    </div>
    <div v-else class="course-grid">
      <el-card v-for="c in list" :key="c.id" class="course-card" shadow="hover">
        <div class="card-top">
          <div class="course-icon">{{ c.courseName.charAt(0) }}</div>
          <div class="course-meta">
            <span class="course-name">{{ c.courseName }}</span>
            <span class="course-teacher">{{ c.teacherName || '未知' }}</span>
          </div>
          <div class="enroll-badge">{{ c.enrolledCount }}/{{ c.maxStudents }}</div>
        </div>
        <p class="course-desc">{{ c.description || '暂无介绍' }}</p>
        <div class="card-bottom">
          <el-tag v-if="c.status === '0'" size="small" type="warning" effect="plain">待审核</el-tag>
          <el-tag v-else-if="c.status === '1'" size="small" type="success" effect="plain">即将开课</el-tag>
          <el-tag v-else-if="c.status === '2'" size="small" type="primary" effect="plain">授课中</el-tag>
          <el-tag v-else-if="c.status === '3'" size="small" effect="plain">已结课</el-tag>
          <el-tag v-else-if="c.status === '4'" size="small" type="danger" effect="plain">未通过</el-tag>
          <div class="card-actions">
            <el-button size="small" plain @click="$router.push('/courses/' + c.id)" round>详情</el-button>
            <el-button v-if="store.user?.role === 1 && (String(c.status) === '1' || String(c.status) === '2')" size="small" type="primary" @click="handleSelect(c.id)" round>选课</el-button>
          </div>
        </div>
      </el-card>
    </div>
    <div class="pagination-bar">
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
const store = useUserStore()
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 9
const keyword = ref('')

onMounted(() => {
  fetchData()
})

function handleSearch() {
  pageNum.value = 1
  fetchData()
}

async function fetchData() {
  const params = { pageNum: pageNum.value, pageSize, excludeStudentId: store.user?.role === 1 ? store.user.id : undefined }
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
.page-wrap {
  max-width: 1060px;
  margin: 0 auto;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 3px solid #4f46e5;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  padding-left: 12px;
}
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
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
.course-card :deep(.el-card__body) { padding: 18px; }
.card-top {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}
.enroll-badge {
  font-size: 11px;
  font-weight: 700;
  color: #4f46e5;
  background: #eef2ff;
  border-radius: 8px;
  padding: 2px 10px;
  white-space: nowrap;
  flex-shrink: 0;
  align-self: flex-start;
  margin-top: 2px;
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
.course-desc {
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
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}
.card-actions {
  display: flex;
  gap: 6px;
}
.pagination-bar {
  text-align: center;
  margin: 24px 0 0;
}
.empty-state {
  padding: 60px 0;
}
</style>
