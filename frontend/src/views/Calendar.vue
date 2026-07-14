<template>
  <div>
    <div class="page-title">课程日程</div>

    <div class="legend-bar">
      <span class="legend-item"><span class="legend-dot dot-assignment" /> 作业截止</span>
      <span class="legend-item"><span class="legend-dot dot-exam" /> 考试</span>
      <span class="legend-item"><span class="legend-dot dot-course-exam" /> 结课考试</span>
      <span style="margin-left:auto;font-size:12px;color:#94a3b8">点击事件跳转详情</span>
    </div>

    <div class="cal-toolbar">
      <el-button-group>
        <el-button :type="viewMode === 'month' ? 'primary' : ''" size="small" @click="viewMode='month'">月</el-button>
        <el-button :type="viewMode === 'week' ? 'primary' : ''" size="small" @click="viewMode='week'">周</el-button>
      </el-button-group>
      <div class="cal-nav">
        <el-button text @click="prev"><el-icon><ArrowLeft /></el-icon></el-button>
        <span class="cal-title">{{ titleText }}</span>
        <el-button text @click="next"><el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <el-button size="small" @click="today">今天</el-button>
    </div>

    <el-card shadow="hover" class="cal-card">
      <table class="cal-table" v-if="viewMode === 'month'">
        <thead><tr><th v-for="d in weekDays" :key="d">{{ d }}</th></tr></thead>
        <tbody>
          <tr v-for="(row, ri) in monthGrid" :key="ri">
            <td v-for="(cell, ci) in row" :key="ci"
              :class="{ 'cal-other': !cell.isCurrent, 'cal-today': cell.isToday }">
              <div class="cal-cell-header">{{ cell.day }}</div>
              <div class="cal-events">
                <div v-for="ev in cell.events" :key="ev.id"
                  class="cal-event" :class="'ev-' + ev.eventType"
                  @click.stop="goTo(ev)">
                  {{ ev.title }}
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="week-view" v-else>
        <div v-for="(day, di) in weekDaysData" :key="di" class="week-col"
          :class="{ 'cal-today': isSameDay(day.date, new Date()) }">
          <div class="week-col-header">
            <div class="week-day-name">{{ day.label }}</div>
            <div class="week-date-num">{{ day.date.getDate() }}</div>
          </div>
          <div class="week-events">
            <div v-for="ev in day.events" :key="ev.id"
              class="cal-event" :class="'ev-' + ev.eventType"
              @click="goTo(ev)">
              <div class="ev-title">{{ ev.title }}</div>
              <div class="ev-time">{{ formatTime(ev) }}</div>
            </div>
            <div v-if="day.events.length === 0" class="week-empty">无日程</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getCalendarEvents } from '../api/calendar'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const store = useUserStore()
const router = useRouter()
const viewMode = ref('month')
const baseDate = ref(new Date())
const allEvents = ref([])

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

function pad(n) { return n < 10 ? '0' + n : '' + n }

function fmtDate(d) {
  return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
}

const titleText = computed(() => {
  const d = baseDate.value
  if (viewMode.value === 'month') {
    return d.getFullYear() + '年' + (d.getMonth() + 1) + '月'
  }
  const start = new Date(d)
  start.setDate(start.getDate() - start.getDay())
  const end = new Date(start)
  end.setDate(end.getDate() + 6)
  return fmtDate(start) + ' ~ ' + fmtDate(end)
})

function isSameDay(a, b) {
  return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate()
}

const monthGrid = computed(() => {
  const d = new Date(baseDate.value)
  const year = d.getFullYear()
  const month = d.getMonth()
  const first = new Date(year, month, 1)
  const last = new Date(year, month + 1, 0)
  const startDay = first.getDay()
  const rows = []
  let row = []
  for (let i = 0; i < startDay; i++) {
    const prev = new Date(year, month, -startDay + i + 1)
    row.push({ day: prev.getDate(), isCurrent: false, isToday: false, events: [], date: prev })
  }
  for (let day = 1; day <= last.getDate(); day++) {
    const date = new Date(year, month, day)
    const today = isSameDay(date, new Date())
    const evs = allEvents.value.filter(e => isSameDay(parseDate(e.start), date))
    row.push({ day, isCurrent: true, isToday: today, events: evs, date })
    if (row.length === 7) { rows.push(row); row = [] }
  }
  if (row.length > 0) {
    const remaining = 7 - row.length
    for (let i = 1; i <= remaining; i++) {
      const next = new Date(year, month + 1, i)
      row.push({ day: next.getDate(), isCurrent: false, isToday: false, events: [], date: next })
    }
    rows.push(row)
  }
  return rows
})

const weekDaysData = computed(() => {
  const d = new Date(baseDate.value)
  const start = new Date(d)
  start.setDate(start.getDate() - start.getDay())
  return weekDays.map((label, i) => {
    const date = new Date(start)
    date.setDate(date.getDate() + i)
    const evs = allEvents.value.filter(e => isSameDay(parseDate(e.start), date))
    return { label, date, events: evs }
  })
})

function parseDate(v) {
  if (!v) return new Date()
  if (typeof v === 'string') return new Date(v.replace(' ', 'T'))
  return new Date(v)
}

function formatTime(ev) {
  if (!ev.endTime) return ''
  const s = parseDate(ev.startTime)
  const e = parseDate(ev.endTime)
  return pad(s.getHours()) + ':' + pad(s.getMinutes()) + ' - ' + pad(e.getHours()) + ':' + pad(e.getMinutes())
}

function prev() {
  const d = new Date(baseDate.value)
  if (viewMode.value === 'month') d.setMonth(d.getMonth() - 1)
  else d.setDate(d.getDate() - 7)
  baseDate.value = d
}

function next() {
  const d = new Date(baseDate.value)
  if (viewMode.value === 'month') d.setMonth(d.getMonth() + 1)
  else d.setDate(d.getDate() + 7)
  baseDate.value = d
}

function today() { baseDate.value = new Date() }

function goTo(ev) {
  if (ev.eventType === 'assignment') router.push('/my-assignments')
  else if (ev.eventType === 'exam' || ev.eventType === 'courseExam') router.push('/my-exams')
}

onMounted(async () => {
  try {
    const data = await getCalendarEvents(store.user.id, store.user.role)
    if (data && data.length > 0) {
      allEvents.value = data.map(e => ({
        ...e,
        start: e.startTime,
        end: e.endTime,
        eventType: e.eventType
      }))
    }
  } catch (_) {}
})
</script>

<style scoped>
.legend-bar {
  display: flex; gap: 18px; align-items: center;
  margin-bottom: 14px; padding: 10px 16px;
  background: #fff; border-radius: 10px; border: 1px solid #eef2f6;
  font-size: 13px; color: #475569;
}
.legend-item { display: flex; align-items: center; gap: 6px; }
.legend-dot { width: 10px; height: 10px; border-radius: 3px; flex-shrink: 0; }
.dot-assignment { background: #6366f1; }
.dot-exam { background: #22c55e; }
.dot-course-exam { background: #f59e0b; }

.cal-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 12px;
}
.cal-nav { display: flex; align-items: center; gap: 12px; }
.cal-title { font-size: 17px; font-weight: 700; color: #1e293b; min-width: 160px; text-align: center; }

.cal-card { border-radius: 12px !important; border: 1px solid #eef2f6; overflow: hidden; }
.cal-card :deep(.el-card__body) { padding: 0; }

.cal-table { width: 100%; border-collapse: collapse; table-layout: fixed; }
.cal-table th {
  padding: 10px 0; font-size: 13px; font-weight: 600; color: #64748b;
  background: #f8fafc; border-bottom: 1px solid #e2e8f0;
}
.cal-table td {
  vertical-align: top; height: 100px; padding: 4px;
  border: 1px solid #f1f5f9; cursor: pointer;
  transition: background 0.15s;
}
.cal-table td:hover { background: #f8fafc; }
.cal-table td.cal-other { opacity: 0.35; }
.cal-table td.cal-today { background: #eff6ff; }
.cal-cell-header {
  font-size: 13px; font-weight: 500; color: #334155;
  padding: 2px 4px; margin-bottom: 2px;
}
.cal-today .cal-cell-header {
  background: #1677ff; color: #fff; border-radius: 50%;
  width: 26px; height: 26px; display: grid; place-items: center;
}
.cal-events { display: flex; flex-direction: column; gap: 2px; }
.cal-event {
  font-size: 11px; color: #fff; padding: 2px 5px;
  border-radius: 4px; cursor: pointer; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  transition: transform 0.12s;
}
.cal-event:hover { transform: translateY(-1px); }
.ev-assignment { background: #6366f1; }
.ev-exam { background: #22c55e; }
.ev-courseExam { background: #f59e0b; }

.week-view { display: flex; min-height: 350px; }
.week-col { flex: 1; border-right: 1px solid #f1f5f9; }
.week-col:last-child { border-right: none; }
.week-col.cal-today { background: #eff6ff; }
.week-col-header { text-align: center; padding: 12px 0; border-bottom: 1px solid #e2e8f0; }
.week-day-name { font-size: 12px; color: #94a3b8; }
.week-date-num { font-size: 20px; font-weight: 700; color: #334155; margin-top: 2px; }
.week-col.cal-today .week-date-num { color: #1677ff; }
.week-events { padding: 6px; display: flex; flex-direction: column; gap: 4px; }
.week-events .cal-event { white-space: normal; padding: 6px 8px; font-size: 12px; }
.ev-title { font-weight: 500; }
.ev-time { font-size: 11px; opacity: 0.85; margin-top: 2px; }
.week-empty { text-align: center; color: #cbd5e1; font-size: 12px; padding: 20px 0; }
</style>
