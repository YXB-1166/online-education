<template>
  <div>
    <div class="page-title">
      成绩趋势
      <el-select v-model="courseId" placeholder="选择课程" style="width:220px;margin-left:12px" @change="loadTrend">
        <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
    </div>

    <div v-if="!courseId" style="padding:60px 0"><el-empty description="请先选择课程" /></div>

    <template v-else-if="loading"><div style="text-align:center;padding:60px"><el-icon class="is-loading" :size="32"><Loading /></el-icon></div></template>

    <template v-else-if="trendData.length === 0"><el-empty description="暂无成绩数据" /></template>

    <template v-else>
      <el-card shadow="hover">
        <template #header>
          <span>各次成绩走势</span>
          <span style="float:right;font-size:12px;color:#94a3b8">蓝色=作业 绿色=考试</span>
        </template>
        <v-chart :option="chartOption" style="height:350px" autoresize />
      </el-card>

      <el-card shadow="hover" style="margin-top:16px">
        <template #header>成绩明细</template>
        <div v-for="item in trendData" :key="item.itemId + item.itemType" class="trend-item">
          <div class="trend-left">
            <el-tag :type="item.itemType === 'exam' ? 'success' : 'primary'" size="small" round>
              {{ item.itemType === 'exam' ? '考试' : '作业' }}
            </el-tag>
            <span>{{ item.itemName }}</span>
          </div>
          <div class="trend-right">
            <span class="trend-score" :class="scoreClass(item)">{{ item.score }}/{{ item.fullScore }}</span>
            <span class="trend-time">{{ timeStr(item.scoreTime) }}</span>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useUserStore } from '../../stores/user'
import { Loading } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getGradeTrend } from '../../api/grade'
import { myCourses } from '../../api/course'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const store = useUserStore()
const courses = ref([])
const courseId = ref(null)
const trendData = ref([])
const loading = ref(false)

const chartOption = computed(() => {
  const labels = trendData.value.map(d => d.itemName)
  const scores = trendData.value.map(d => d.score)
  const full = trendData.value.map(d => d.fullScore)
  const types = trendData.value.map(d => d.itemType)
  return {
    tooltip: {
      trigger: 'axis',
      formatter: params => {
        const p = params[0]
        const idx = p.dataIndex
        const d = trendData.value[idx]
        return `${d.itemName}<br/>得分: ${d.score}/${d.fullScore} (${(d.score / d.fullScore * 100).toFixed(0)}%)`
      }
    },
    legend: { data: ['得分'], top: 0 },
    grid: { left: 50, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: labels, axisLabel: { rotate: 15, fontSize: 11 } },
    yAxis: { type: 'value', max: 100 },
    series: [{
      name: '得分',
      type: 'line',
      data: scores.map((v, i) => ({
        value: v,
        itemStyle: { color: types[i] === 'exam' ? '#22c55e' : '#6366f1' }
      })),
      markLine: { data: [{ yAxis: 60, label: { formatter: '及格线' }, lineStyle: { color: '#ef4444', type: 'dashed' } }] },
      smooth: true,
    }]
  }
})

function scoreClass(item) {
  const ratio = item.score / item.fullScore
  if (ratio >= 0.9) return 'score-excellent'
  if (ratio >= 0.8) return 'score-good'
  if (ratio >= 0.6) return 'score-pass'
  return 'score-fail'
}

function timeStr(t) {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}

async function loadTrend() {
  if (!courseId.value) return
  trendData.value = []
  loading.value = true
  try {
    const d = await getGradeTrend(courseId.value, store.user.id)
    if (d) trendData.value = d
  } finally { loading.value = false }
}

watch(courseId, loadTrend)

onMounted(async () => {
  courses.value = (await myCourses(store.user.id)) || []
  if (courses.value.length > 0) { courseId.value = courses.value[0].id }
})
</script>

<style scoped>
.trend-item { display:flex;justify-content:space-between;align-items:center;padding:10px 4px;border-bottom:1px solid #f8fafc }
.trend-left { display:flex;align-items:center;gap:8px;font-size:13px }
.trend-right { display:flex;align-items:center;gap:10px }
.trend-score { font-weight:700;font-size:14px }
.trend-time { font-size:12px;color:#94a3b8 }
.score-excellent { color:#22c55e }
.score-good { color:#6366f1 }
.score-pass { color:#eab308 }
.score-fail { color:#ef4444 }
</style>
