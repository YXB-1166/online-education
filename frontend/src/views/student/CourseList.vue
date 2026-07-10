<template>
  <div class="page-wrap">
    <div class="page-title">课程广场</div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索课程名称或关键词..." clearable
        @keyup.enter="handleSearch" style="width:360px" />
      <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
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

    <div class="course-grid">
      <el-card v-for="c in list" :key="c.id" class="course-card" shadow="hover">
        <div class="card-top">
          <div class="course-icon">{{ c.courseName.charAt(0) }}</div>
          <div class="course-meta">
            <span class="course-name">{{ c.courseName }}</span>
            <span class="course-teacher">{{ c.teacherName || '未知' }} 授课</span>
          </div>
        </div>
        <p class="course-desc">{{ c.description || '暂无介绍' }}</p>
        <div class="card-bottom">
          <span class="stat">{{ c.enrolledCount }}/{{ c.maxStudents }} 已选</span>
          <div class="card-actions">
            <el-button size="small" @click="$router.push('/courses/' + c.id)" round>详情</el-button>
            <el-button v-if="store.user?.role === 1" size="small" type="primary" @click="handleSelect(c.id)" round>选课</el-button>
          </div>
        </div>
      </el-card>
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
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
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
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
}
.course-card:hover {
  transform: translateY(-3px);
  border-color: #c7d2fe;
  box-shadow: 0 10px 24px -8px rgba(79,70,229,0.15) !important;
}
.course-card :deep(.el-card__body) { padding: 18px; }
.card-top {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}
.course-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  flex-shrink: 0;
}
.course-meta {
  flex: 1;
  min-width: 0;
}
.course-name {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course-teacher {
  font-size: 12px;
  color: #64748b;
}
.course-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}
.stat {
  font-size: 12px;
  color: #4f46e5;
  font-weight: 600;
}
.card-actions {
  display: flex;
  gap: 6px;
}
.pagination-bar {
  text-align: center;
  margin: 16px 0;
}
</style>
