<template>
  <div>
    <div class="page-title">
      成绩统计
      <el-select v-model="courseId" placeholder="选择课程" style="width:220px;margin-left:12px" @change="loadStats">
        <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
    </div>

    <div v-if="!courseId" style="padding:60px 0"><el-empty description="请先选择课程" /></div>

    <template v-else-if="loading"><div style="text-align:center;padding:60px"><el-icon class="is-loading" :size="32"><Loading /></el-icon></div></template>

    <template v-else>
      <el-row :gutter="16" style="margin-bottom:16px">
        <el-col :span="4" v-for="s in statCards" :key="s.label">
          <div class="stat-card">
            <div class="stat-num">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="14">
          <el-card shadow="hover">
            <template #header>成绩分布</template>
            <v-chart :option="chartOption" style="height:300px" autoresize />
          </el-card>
        </el-col>
        <el-col :span="10">
          <el-card shadow="hover">
            <template #header>
              <span>学生成绩列表</span>
              <el-button size="small" style="float:right" @click="doExport">导出 Excel</el-button>
            </template>
            <div style="max-height:400px;overflow-y:auto">
              <div v-for="s in stats.students" :key="s.studentId" class="student-grade-row">
                <span>{{ s.studentName }}</span>
                <el-tag :type="gradeTag(s.finalScore)" size="small">{{ s.finalScore ?? '未出' }}</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { Loading } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getGradeStats, exportGrades } from '../../api/grade'
import { myTeaching } from '../../api/course'

use([BarChart, GridComponent, TooltipComponent, CanvasRenderer])

const store = useUserStore()
const courses = ref([])
const courseId = ref(null)
const stats = ref({ distribution: {}, students: [] })
const loading = ref(false)

const statCards = computed(() => [
  { label: '总人数', value: stats.value.total ?? 0 },
  { label: '已评分', value: stats.value.scoredCount ?? 0 },
  { label: '及格率', value: `${stats.value.passRate ?? 0}%` },
  { label: '平均分', value: stats.value.avgScore ?? 0 },
  { label: '最高分', value: stats.value.maxScore ?? 0 },
  { label: '最低分', value: stats.value.minScore ?? 0 },
])

const chartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 60, right: 20, top: 30, bottom: 30 },
  xAxis: { type: 'category', data: Object.keys(stats.value.distribution) },
  yAxis: { type: 'value' },
  series: [{
    type: 'bar',
    data: Object.values(stats.value.distribution),
    itemStyle: {
      color: params => ['#22c55e','#6366f1','#eab308','#f97316','#ef4444'][params.dataIndex]
    },
    barWidth: 40,
  }],
}))

function gradeTag(score) {
  if (score == null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 70) return 'warning'
  if (score >= 60) return 'warning'
  return 'danger'
}

async function loadStats() {
  if (!courseId.value) return
  loading.value = true
  try {
    stats.value = await getGradeStats(courseId.value)
  } finally { loading.value = false }
}

async function doExport() {
  const course = courses.value.find(c => c.id === courseId.value)
  const name = course?.courseName || `course_${courseId.value}`
  try {
    const blob = await exportGrades(courseId.value, name)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url; a.download = `${name}_成绩.xlsx`; a.click()
    URL.revokeObjectURL(url)
  } catch (_) {}
}

onMounted(async () => {
  courses.value = (await myTeaching(store.user.id)) || []
  if (courses.value.length > 0) { courseId.value = courses.value[0].id; await loadStats() }
})
</script>

<style scoped>
.stat-card { background:#fff; border:1px solid #eef2f6; border-radius:10px; padding:16px; text-align:center }
.stat-num { font-size:28px;font-weight:800;color:#6366f1 }
.stat-label { font-size:12px;color:#94a3b8;margin-top:4px }
.student-grade-row { display:flex;justify-content:space-between;align-items:center;padding:8px 4px;border-bottom:1px solid #f8fafc;font-size:13px }
</style>
