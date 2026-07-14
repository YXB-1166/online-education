<template>
  <div>
    <div class="page-title">课程日程</div>

    <el-card shadow="hover">
      <FullCalendar :options="calendarOptions" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getCalendarEvents } from '../api/calendar'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'

const store = useUserStore()
const router = useRouter()

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
  calendarOptions.value.events = data.map(e => {
    const colors = TYPE_COLORS[e.eventType] || '#64748b'
    return {
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
    }
  })
})
</script>
