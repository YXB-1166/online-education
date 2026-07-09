<template>
  <div>
    <div class="page-title">学习进度</div>

    <div v-if="loading" style="text-align:center;padding:60px">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p style="margin-top:12px;color:#94a3b8">正在加载学习数据...</p>
    </div>

    <template v-else-if="progress.length === 0">
      <el-empty description="还没有选课记录" />
    </template>

    <template v-else>
      <el-row :gutter="20">
        <el-col v-for="p in progress" :key="p.courseId" :xs="24" :sm="12" :lg="8" style="margin-bottom:20px">
          <el-card :class="{ 'risk-card': p.risk }" shadow="hover">
            <div class="card-header">
              <div class="course-identity">
                <div class="course-avatar">{{ p.course_name.charAt(0) }}</div>
                <div>
                  <div class="course-name">{{ p.course_name }}</div>
                  <div class="course-meta">
                    <el-tag size="small" type="info" effect="plain" round>#{{ p.courseId }}</el-tag>
                    <el-tag size="small" type="warning" effect="plain" round>{{ p.credit }} 学分</el-tag>
                  </div>
                </div>
              </div>
              <el-tag v-if="p.risk" type="danger" effect="dark" size="small">⚠️ 挂科风险</el-tag>
            </div>

            <div class="stat-row">
              <div class="stat-item">
                <div class="stat-value">{{ p.totalAssignments }}</div>
                <div class="stat-label">总作业数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ p.submittedCount }}</div>
                <div class="stat-label">已提交</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ p.gradedCount }}</div>
                <div class="stat-label">已批改</div>
              </div>
              <div class="stat-item">
                <div class="stat-value" :class="avgClass(p.avgScore)">{{ p.avgScore.toFixed(1) }}</div>
                <div class="stat-label">平均分</div>
              </div>
            </div>

            <div style="margin-top:12px">
              <div style="display:flex;justify-content:space-between;font-size:12px;color:#94a3b8">
                <span>作业提交进度</span>
                <span>{{ p.submittedCount }}/{{ p.totalAssignments }}</span>
              </div>
              <el-progress :percentage="submitPercent(p)" :stroke-width="8"
                :color="p.submittedCount === p.totalAssignments ? '#22c55e' : '#6366f1'"
                style="margin-top:4px" />
            </div>

            <div v-if="p.missedCount > 0" class="warning-item">
              <el-icon color="#eab308"><WarningFilled /></el-icon>
              <span>{{ p.missedCount }} 份作业未提交</span>
            </div>
            <div v-if="p.below60Count > 0" class="warning-item danger">
              <el-icon color="#ef4444"><WarningFilled /></el-icon>
              <span>{{ p.below60Count }} 次作业低于60分</span>
            </div>
            <div v-if="p.avgScore > 0 && p.avgScore < 60 && p.gradedCount >= p.totalAssignments / 2" class="warning-item danger">
              <el-icon color="#ef4444"><CircleCloseFilled /></el-icon>
              <span>挂科风险高，请及时复习！</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { Loading, WarningFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import request from '../../api/request'

const store = useUserStore()
const progress = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const data = await request({ url: '/assistant/progress', method: 'get', params: { studentId: store.user.id } })
    progress.value = data.map(p => {
      const total = p.totalAssignments || 0
      const submitted = p.submittedCount || 0
      const graded = p.gradedCount || 0
      const below60 = p.below60Count || 0
      const avg = p.avgScore || 0
      const missed = total - submitted
      const risk = (missed >= 3 && missed >= total / 3) || (below60 >= 3 && below60 >= graded / 2) || (avg > 0 && avg < 60 && graded >= total * 2 / 3)
      return { ...p, totalAssignments: total, submittedCount: submitted, gradedCount: graded, below60Count: below60, avgScore: avg, missedCount: missed, risk }
    })
  } finally {
    loading.value = false
  }
})

function submitPercent(p) {
  if (p.totalAssignments === 0) return 0
  return Math.round(p.submittedCount / p.totalAssignments * 100)
}

function avgClass(avg) {
  if (avg >= 85) return 'avg-excellent'
  if (avg >= 70) return 'avg-good'
  if (avg >= 60) return 'avg-pass'
  if (avg === 0) return ''
  return 'avg-fail'
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}
.course-identity {
  display: flex;
  align-items: center;
  gap: 10px;
}
.course-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.course-name {
  font-weight: 700;
  font-size: 16px;
  color: #1e293b;
  line-height: 1.3;
}
.course-meta {
  display: flex;
  gap: 4px;
  margin-top: 3px;
}
.stat-row {
  display: flex;
  gap: 8px;
}
.stat-item {
  flex: 1;
  text-align: center;
  padding: 10px 4px;
  background: #f8fafc;
  border-radius: 10px;
}
.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #6366f1;
}
.stat-label {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 2px;
}
.avg-excellent { color: #22c55e; }
.avg-good { color: #6366f1; }
.avg-pass { color: #eab308; }
.avg-fail { color: #ef4444; }
.warning-item {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 6px 10px;
  background: #fefce8;
  border-radius: 8px;
  font-size: 13px;
  color: #92400e;
}
.warning-item.danger {
  background: #fef2f2;
  color: #991b1b;
}
.risk-card {
  border: 2px solid #fca5a5 !important;
}
</style>
