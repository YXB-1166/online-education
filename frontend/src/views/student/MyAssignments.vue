<template>
  <div>
    <div class="page-title">我的作业</div>

    <div class="filter-bar">
      <el-select v-model="courseFilter" placeholder="全部课程" clearable style="width:200px" @change="pageNum=1">
        <el-option label="全部课程" value="" />
        <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
      <el-radio-group v-model="statusFilter" size="small" @change="pageNum=1">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="completed">已完成</el-radio-button>
        <el-radio-button value="unfinished">未完成</el-radio-button>
      </el-radio-group>
      <span class="filter-stat">共 {{ filteredList.length }} 项</span>
    </div>

    <div v-if="filteredList.length === 0" class="empty-state">
      <el-empty description="暂无作业" />
    </div>
    <div v-else class="assignment-list">
      <div v-for="item in pagedList" :key="item.id" class="assignment-card">
        <div class="card-left">
          <div class="assign-icon">{{ item.title.charAt(0) }}</div>
          <div class="assign-info">
            <span class="assign-title">{{ item.title }}</span>
            <span class="assign-meta">
              {{ courseMap[item.courseId] || '未知课程' }}
              <el-divider direction="vertical" />
              截止 {{ item.deadline?.replace('T', ' ') || '无期限' }}
              <el-divider direction="vertical" />
              满分 {{ item.fullScore || '-' }}
            </span>
          </div>
        </div>
        <div class="card-right">
          <div class="assign-status">
            <el-tag v-if="btnInfo(item).status === 'course_ended'" type="info" size="small" round>已结课</el-tag>
            <el-tag v-else-if="btnInfo(item).status === 'ended_expired'" type="danger" size="small" round>已过期</el-tag>
            <el-tag v-else-if="btnInfo(item).status === 'expired'" type="danger" size="small" round>未完成</el-tag>
            <el-tag v-else-if="btnInfo(item).status === 'completed'" type="info" size="small" round>已完成</el-tag>
            <el-tag v-else-if="btnInfo(item).status === 'submit'" type="warning" size="small" round>待提交</el-tag>
            <el-tag v-else-if="btnInfo(item).status === 'graded'" type="success" size="small" round>已批改</el-tag>
            <div v-if="submissionMap[item.id]?.score != null" class="assign-score">{{ submissionMap[item.id].score }}分</div>
          </div>
          <el-button v-if="btnInfo(item).submitBtn" size="small" :type="btnInfo(item).submitBtnType" :disabled="btnInfo(item).submitDisabled" @click="$router.push(`/courses/${item.courseId}/assignments/${item.id}/submit`)" round>
            {{ btnInfo(item).submitBtn }}
          </el-button>
          <el-button v-if="submissionMap[item.id]" size="small" plain @click="$router.push(`/courses/${item.courseId}/assignments/${item.id}/submit?readonly=1`)" round>查看</el-button>
        </div>
      </div>
    </div>

    <div class="pagination-bar">
      <el-pagination
        v-if="filteredList.length > pageSize"
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="filteredList.length"
        layout="prev, pager, next"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { listAssignments, listSubmissions } from '../../api/exam'
import { myCourses } from '../../api/course'

const store = useUserStore()
const allAssignments = ref([])
const submissionMap = ref({})
const courses = ref([])
const courseMap = ref({})
const courseEndedMap = ref({})
const courseFilter = ref('')
const statusFilter = ref('')
const pageNum = ref(1)
const pageSize = 15

function btnInfo(a) {
  const sub = submissionMap.value[a.id]
  const submitted = !!sub
  const submitCount = sub?.submitCount || 0
  const maxCount = a.allowSubmitCount || 1
  const deadlinePassed = a.deadline && new Date(a.deadline) < new Date()
  const courseEnded = courseEndedMap.value[a.courseId]
  if (courseEnded) {
    if (submitted) return { status: 'course_ended', submitBtn: null, submitBtnType: 'info', submitDisabled: true }
    return { status: 'ended_expired', submitBtn: '已过期', submitBtnType: 'info', submitDisabled: true }
  }
  if (!submitted && deadlinePassed) return { status: 'expired', submitBtn: '未完成', submitBtnType: 'info', submitDisabled: true }
  if (!submitted) return { status: 'submit', submitBtn: '提交', submitBtnType: 'primary', submitDisabled: false }
  if (submitted && sub.score != null) {
    if (deadlinePassed || submitCount >= maxCount) return { status: 'graded', submitBtn: null, submitBtnType: 'info', submitDisabled: true }
    return { status: 'graded', submitBtn: '再次提交', submitBtnType: 'primary', submitDisabled: false }
  }
  if (submitted && deadlinePassed) return { status: 'completed', submitBtn: null, submitBtnType: 'info', submitDisabled: true }
  if (submitted && !deadlinePassed) {
    if (submitCount >= maxCount) return { status: 'completed', submitBtn: null, submitBtnType: 'info', submitDisabled: true }
    return { status: 'resubmit', submitBtn: '重新提交', submitBtnType: 'primary', submitDisabled: false }
  }
  return { status: 'expired', submitBtn: null, submitBtnType: 'info', submitDisabled: true }
}

const filteredList = computed(() => {
  let items = allAssignments.value.filter(a => {
    if (courseFilter.value && a.courseId !== courseFilter.value) return false
    const info = btnInfo(a)
    if (statusFilter.value === 'completed') {
      return info.status === 'completed' || info.status === 'graded' || info.status === 'course_ended'
    }
    if (statusFilter.value === 'unfinished') {
      return info.status === 'submit' || info.status === 'resubmit'
    }
    return true
  })
  return items.sort((a, b) => {
    const sa = btnInfo(a).status
    const sb = btnInfo(b).status
    const order = { submit: 0, resubmit: 1, completed: 2, graded: 3, course_ended: 4, ended_expired: 5, expired: 6 }
    return (order[sa] || 99) - (order[sb] || 99)
  })
})

const pagedList = computed(() => {
  const start = (pageNum.value - 1) * pageSize
  return filteredList.value.slice(start, start + pageSize)
})

onMounted(async () => {
  const [assignments, subs, enrolled] = await Promise.all([
    listAssignments({}),
    listSubmissions({ studentId: store.user.id }),
    myCourses(store.user.id)
  ])
  allAssignments.value = assignments || []
  courses.value = enrolled || []
  const map = {}
  const endedMap = {}
  ;(enrolled || []).forEach(c => { map[c.id] = c.courseName; if (String(c.status) === '3') endedMap[c.id] = true })
  courseMap.value = map
  courseEndedMap.value = endedMap
  const sm = {}
  ;(subs || []).forEach(s => { sm[s.assignmentId] = s })
  submissionMap.value = sm
})
</script>

<style scoped>
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
.assignment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.assignment-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border: 1px solid #eef2f6;
  border-radius: 10px;
  padding: 14px 18px;
  transition: box-shadow 0.2s;
}
.assignment-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  border-color: #c7d2fe;
}
.card-left {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-width: 0;
}
.assign-icon {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
  flex-shrink: 0;
}
.assign-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.assign-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}
.assign-meta {
  font-size: 12px;
  color: #94a3b8;
}
.card-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.assign-status {
  display: flex;
  align-items: center;
  gap: 6px;
}
.assign-score {
  font-size: 13px;
  font-weight: 700;
  color: #4f46e5;
}
.pagination-bar {
  text-align: center;
  margin: 24px 0 0;
}
.empty-state {
  padding: 60px 0;
}
</style>
