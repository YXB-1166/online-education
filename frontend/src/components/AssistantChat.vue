<template>
  <div class="assistant-btn-wrap" v-if="!visible">
    <el-button class="assistant-btn" @click="visible = true" circle>
      <el-icon :size="24" color="#fff">
        <ChatDotSquare />
      </el-icon>
    </el-button>
  </div>
  <div class="assistant-wrap" ref="wrapRef" :style="wrapStyle" v-show="visible">
    <transition name="slide-up">
      <div v-if="visible" class="assistant-panel" :style="{ width: panelWidth + 'px', height: panelHeight + 'px', right: panelRight }">
        <div class="panel-header"
          @mousedown.prevent="startDrag"
          @touchstart.prevent="startDrag">
          <el-icon :size="22" color="#fff"><MagicStick /></el-icon>
          <span class="panel-title">智能助手</span>
          <span class="drag-hint">≡ 拖拽</span>
          <el-button text @click="visible = false" style="margin-left:auto;color:#fff;font-size:18px">✕</el-button>
        </div>
        <div class="resize-handle-e" @mousedown.stop.prevent="startResize('e', $event)" @touchstart.stop.prevent="startResize('e', $event)"></div>
        <div class="resize-handle-s" @mousedown.stop.prevent="startResize('s', $event)" @touchstart.stop.prevent="startResize('s', $event)"></div>
        <div class="resize-handle-se" @mousedown.stop.prevent="startResize('se', $event)" @touchstart.stop.prevent="startResize('se', $event)"></div>
        <div class="panel-body" ref="bodyRef">
          <div v-if="messages.length === 0" class="welcome">
            <el-icon :size="36" color="#6366f1"><MagicStick /></el-icon>
            <p>你好！我是智能助手，可以帮你：</p>
            <template v-if="role === 1">
              <p class="hint">• 推荐适合你的课程</p>
              <p class="hint">• 查询课程详细信息</p>
              <p class="hint">• 查看已选课程情况</p>
              <p class="hint">• 了解各课程学分</p>
              <p class="hint">• 查询学习进度</p>
            </template>
            <template v-else>
              <p class="hint">• 查看课程统计概览</p>
              <p class="hint">• 查询选课待审批数量</p>
              <p class="hint">• 查看各课程学生名单</p>
              <p class="hint">• 课程选课情况分析</p>
            </template>
          </div>
          <div v-for="(msg, i) in messages" :key="i" :class="'msg ' + msg.role">
            <div class="msg-bubble">
              <span style="white-space:pre-wrap">{{ msg.content }}</span>
            </div>
          </div>
          <div v-if="suggestions.length > 0" class="suggestions">
            <el-tag v-for="(s, j) in suggestions" :key="j"
              @click="send(s)" class="sug-tag" size="small" effect="plain">
              {{ s }}
              <el-icon style="margin-left:4px"><CaretRight /></el-icon>
            </el-tag>
          </div>
        </div>
        <div class="panel-footer">
          <el-input v-model="input" placeholder="输入你的问题..."
            @keyup.enter="send(input)" clearable>
            <template #append>
              <el-button @click="send(input)" :icon="Promotion" />
            </template>
          </el-input>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { askAssistant } from '../api/course'
import { useUserStore } from '../stores/user'
import { ChatDotSquare, Promotion, CaretRight, MagicStick } from '@element-plus/icons-vue'

const store = useUserStore()
const visible = ref(false)
const input = ref('')
const messages = ref([])
const suggestions = ref([])
const bodyRef = ref(null)
const wrapRef = ref(null)
const role = ref(store.user?.role || 1)

const MIN_W = 280, MIN_H = 360
const panelWidth = ref(380)
const panelHeight = ref(520)
const panelRight = ref('0px')

const posX = ref(null)
const posY = ref(null)
const dragging = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)
const dragOrigX = ref(0)
const dragOrigY = ref(0)

const resizing = ref(false)
const resizeDir = ref(null)
const resizeStartX = ref(0)
const resizeStartY = ref(0)
const resizeOrigW = ref(0)
const resizeOrigH = ref(0)
const resizeOrigRight = ref(0)

const wrapStyle = ref({})

function scrollBottom() {
  nextTick(() => {
    if (bodyRef.value) {
      bodyRef.value.scrollTop = bodyRef.value.scrollHeight
    }
  })
}

async function send(text) {
  if (!text || !text.trim()) return
  const q = text.trim()
  input.value = ''
  messages.value.push({ role: 'user', content: q })
  suggestions.value = []
  scrollBottom()
  try {
    const res = await askAssistant({ question: q, userId: store.user.id, role: store.user.role })
    messages.value.push({ role: 'bot', content: res.answer || '暂无回复' })
    suggestions.value = res.suggestions || []
  } catch {
    messages.value.push({ role: 'bot', content: '请求失败，请稍后再试。' })
  }
  scrollBottom()
}

function startDrag(e) {
  if (resizing.value) return
  dragging.value = true
  const ev = e.touches ? e.touches[0] : e
  dragStartX.value = ev.clientX
  dragStartY.value = ev.clientY
  if (posX.value === null) {
    const rect = wrapRef.value.getBoundingClientRect()
    posX.value = rect.left
    posY.value = rect.top
  }
  dragOrigX.value = posX.value
  dragOrigY.value = posY.value
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', onDrag, { passive: false })
  document.addEventListener('touchend', stopDrag)
}

function onDrag(e) {
  if (!dragging.value) return
  e.preventDefault()
  const ev = e.touches ? e.touches[0] : e
  const dx = ev.clientX - dragStartX.value
  const dy = ev.clientY - dragStartY.value
  posX.value = dragOrigX.value + dx
  posY.value = dragOrigY.value + dy
  wrapStyle.value = {
    position: 'fixed',
    left: posX.value + 'px',
    top: posY.value + 'px',
    bottom: 'auto',
    right: 'auto',
    zIndex: 9999
  }
}

function stopDrag() {
  dragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', stopDrag)
}

function startResize(dir, e) {
  resizing.value = true
  resizeDir.value = dir
  const ev = e.touches ? e.touches[0] : e
  resizeStartX.value = ev.clientX
  resizeStartY.value = ev.clientY
  resizeOrigW.value = panelWidth.value
  resizeOrigH.value = panelHeight.value
  resizeOrigRight.value = parseFloat(panelRight.value) || 0
  document.addEventListener('mousemove', onResize)
  document.addEventListener('mouseup', stopResize)
  document.addEventListener('touchmove', onResize, { passive: false })
  document.addEventListener('touchend', stopResize)
}

function onResize(e) {
  if (!resizing.value) return
  e.preventDefault()
  const ev = e.touches ? e.touches[0] : e
  const dx = ev.clientX - resizeStartX.value
  const dy = ev.clientY - resizeStartY.value
  const dir = resizeDir.value
  if (dir === 'e' || dir === 'se') {
    const r = resizeOrigRight.value - dx
    panelRight.value = r + 'px'
    panelWidth.value = Math.max(MIN_W, resizeOrigW.value + dx)
  }
  if (dir === 's' || dir === 'se') {
    panelHeight.value = Math.max(MIN_H, resizeOrigH.value + dy)
  }
}

function stopResize() {
  resizing.value = false
  resizeDir.value = null
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
  document.removeEventListener('touchmove', onResize)
  document.removeEventListener('touchend', stopResize)
}

onUnmounted(() => {
  stopDrag()
  stopResize()
})
</script>

<style scoped>
.assistant-btn-wrap {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
}
.assistant-wrap {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
}
.assistant-btn {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(99,102,241,0.4);
  transition: transform 0.2s, box-shadow 0.2s;
}
.assistant-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(99,102,241,0.5);
}
.assistant-btn.active {
  transform: rotate(90deg);
}
.assistant-panel {
  position: absolute;
  bottom: 68px;
  right: 0;
  width: 380px;
  height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.panel-header {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: grab;
  user-select: none;
}
.panel-header:active { cursor: grabbing; }
.panel-title {
  color: #fff;
  font-weight: 700;
  font-size: 16px;
}
.drag-hint {
  color: rgba(255,255,255,0.5);
  font-size: 12px;
  margin-left: 4px;
}
.resize-handle-e,
.resize-handle-s,
.resize-handle-se {
  position: absolute;
  z-index: 10;
}
.resize-handle-e {
  right: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  cursor: ew-resize;
}
.resize-handle-s {
  left: 0;
  right: 0;
  bottom: 0;
  height: 6px;
  cursor: ns-resize;
}
.resize-handle-se {
  right: 0;
  bottom: 0;
  width: 14px;
  height: 14px;
  cursor: nwse-resize;
  background: linear-gradient(135deg, transparent 50%, rgba(99,102,241,0.35) 50%);
  border-bottom-right-radius: 16px;
}
.resize-handle-e:hover,
.resize-handle-s:hover,
.resize-handle-se:hover {
  background-color: rgba(99,102,241,0.08);
}
.resize-handle-e:hover { background-color: rgba(99,102,241,0.06); }
.resize-handle-s:hover { background-color: rgba(99,102,241,0.06); }
.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8fafc;
}
.welcome {
  text-align: center;
  padding: 24px 12px;
  color: #475569;
  font-size: 14px;
  line-height: 1.8;
}
.welcome p { margin: 4px 0; }
.hint { color: #94a3b8; font-size: 13px; }
.msg { margin-bottom: 12px; }
.msg.user { display: flex; justify-content: flex-end; }
.msg.bot { display: flex; justify-content: flex-start; }
.msg-bubble {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.msg.user .msg-bubble {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.msg.bot .msg-bubble {
  background: #fff;
  color: #1e293b;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 4px;
}
.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
  margin-bottom: 8px;
}
.sug-tag {
  cursor: pointer;
  border-radius: 16px !important;
  padding: 0 10px !important;
  font-size: 12px !important;
  transition: all 0.2s;
}
.sug-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(99,102,241,0.25);
}
.panel-footer {
  padding: 12px;
  border-top: 1px solid #e2e8f0;
  background: #fff;
}
.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from, .slide-up-leave-to {
  opacity: 0;
  transform: translateY(16px);
}
</style>
