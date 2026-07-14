<template>
  <div>
    <div class="page-title">
      课程日程
      <span class="title-tip">作业截止 · 考试安排 · 一目了然</span>
    </div>

    <div class="legend-bar">
      <span class="legend-item"><span class="legend-dot" style="background:#6366f1" /> 作业截止</span>
      <span class="legend-item"><span class="legend-dot" style="background:#22c55e" /> 考试</span>
      <span class="legend-item"><span class="legend-dot" style="background:#f59e0b" /> 结课考试</span>
    </div>

    <el-card shadow="hover" class="cal-card">
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getCalendarEvents } from '../api/calendar'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'

const store = useUserStore()
const router = useRouter()
const calendarRef = ref(null)

const TYPE_COLORS = {
  assignment: '#6366f1',
  exam: '#22c55e',
  courseExam: '#f59e0b'
}

const calendarOptions = ref({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,timeGridWeek'
  },
  locale: 'zh-cn',
  firstDay: 1,
  height: 'auto',
  events: [],
  eventClick: (info) => {
    const e = info.event.extendedProps
    if (e.eventType === 'assignment' && e.courseId) {
      router.push('/my-assignments')
    } else if (e.eventType === 'exam' && e.courseId) {
      router.push('/my-exams')
    }
  },
  eventDidMount: (info) => {
    info.el.style.border = 'none'
    info.el.style.borderRadius = '4px'
    info.el.style.fontSize = '12px'
    info.el.style.padding = '2px 4px'
  }
})

onMounted(async () => {
  const data = await getCalendarEvents(store.user.id, store.user.role) || []
  await nextTick()
  const api = calendarRef.value?.getApi()
  if (!api) return
  data.forEach(e => {
    const colors = TYPE_COLORS[e.eventType] || '#64748b'
    api.addEvent({
      id: e.eventType + '-' + e.eventId,
      title: '[' + (e.courseName || '') + '] ' + e.title,
      start: e.startTime,
      end: e.endTime || undefined,
      allDay: !e.endTime,
      backgroundColor: colors,
      textColor: '#fff',
      extendedProps: {
        eventType: e.eventType,
        courseId: e.courseId,
        courseName: e.courseName
      }
    })
  })
})
</script>

<style scoped>
.title-tip {
  font-size: 13px;
  font-weight: 400;
  color: #94a3b8;
  margin-left: 10px;
}

.legend-bar {
  display: flex;
  gap: 18px;
  margin-bottom: 14px;
  padding: 10px 16px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #eef2f6;
  font-size: 13px;
  color: #475569;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
  flex-shrink: 0;
}

.cal-card {
  border-radius: 12px !important;
  border: 1px solid #eef2f6;
}

.cal-card :deep(.fc) {
  font-size: 13px;
}

.cal-card :deep(.fc .fc-toolbar-title) {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.cal-card :deep(.fc .fc-button-primary) {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-weight: 500;
  transition: all 0.2s;
}

.cal-card :deep(.fc .fc-button-primary:not(:disabled):hover) {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.cal-card :deep(.fc .fc-button-primary.fc-button-active) {
  background: #1677ff;
  border-color: #1677ff;
  color: #fff;
}

.cal-card :deep(.fc .fc-daygrid-day-number) {
  font-size: 13px;
  color: #334155;
  padding: 6px 8px 0 0;
}

.cal-card :deep(.fc .fc-col-header-cell-cushion) {
  font-weight: 600;
  color: #64748b;
  padding: 8px 0;
}

.cal-card :deep(.fc .fc-day-today) {
  background: #eff6ff !important;
}

.cal-card :deep(.fc .fc-daygrid-day-frame) {
  min-height: 90px;
}

.cal-card :deep(.fc .fc-event) {
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}

.cal-card :deep(.fc .fc-event:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
}

.cal-card :deep(.fc .fc-more-popover) {
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}
</style>
