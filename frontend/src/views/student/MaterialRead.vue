<template>
  <div>
    <div class="page-title">课程资料 - {{ course?.courseName || '' }}</div>
    <div style="display:grid;grid-template-columns:280px 1fr;gap:24px;align-items:start">
      <el-card>
        <template #header>
          <div style="display:flex;align-items:center;justify-content:space-between">
            <span style="font-weight:600">资料列表</span>
            <el-switch v-model="showUnfinishedOnly" active-text="仅显示未完成" size="small" />
          </div>
        </template>
        <div v-for="m in filteredMaterials" :key="m.id"
          class="material-item"
          :class="{ active: currentMaterial?.id === m.id }"
          @click="selectMaterial(m)">
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="font-weight:500;font-size:14px">{{ m.title }}</span>
            <el-tag v-if="m.completed" type="success" size="small">已读</el-tag>
            <el-tag v-else type="info" size="small">未读</el-tag>
          </div>
        </div>
      </el-card>

      <div v-loading="loading">
        <el-card v-if="currentMaterial">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
            <div>
              <div style="font-size:18px;font-weight:700">{{ currentMaterial.title }}</div>
              <div style="color:#94a3b8;font-size:13px;margin-top:4px">
                需阅读 {{ currentMaterial.required_seconds }} 秒后方可完成
              </div>
            </div>
          </div>
          <el-divider />
          <div style="color:#475569;line-height:1.8;white-space:pre-wrap">{{ currentMaterial.content }}</div>
          <el-divider />
          <div style="text-align:center">
            <el-button v-if="readCompleted" type="success" round disabled>已完成阅读</el-button>
            <el-button v-else-if="elapsed >= currentMaterial.required_seconds" type="primary" @click="handleComplete" :loading="completing" round>标记完成</el-button>
            <el-button v-else type="info" round disabled>阅读中...（还需 {{ remaining }} 秒）</el-button>
          </div>
        </el-card>
        <el-card v-else style="text-align:center;padding:40px 0;color:#94a3b8">
          请从左侧选择一个资料
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { getCourse } from '../../api/course'
import { listMaterialsWithStatus, startReadMaterial, completeReadMaterial } from '../../api/material'

const route = useRoute()
const store = useUserStore()
const course = ref(null)
const materials = ref([])
const currentMaterial = ref(null)
const loading = ref(false)
const completing = ref(false)
const elapsed = ref(0)
const readCompleted = ref(false)
const showUnfinishedOnly = ref(false)
let timer = null

const filteredMaterials = computed(() => {
  if (!showUnfinishedOnly.value) return materials.value
  return materials.value.filter(m => !m.completed)
})

const displayTime = computed(() => {
  const m = Math.floor(elapsed.value / 60)
  const s = elapsed.value % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const remaining = computed(() => {
  if (!currentMaterial.value) return 0
  const r = currentMaterial.value.required_seconds - elapsed.value
  return r > 0 ? r : 0
})

onMounted(async () => {
  loading.value = true
  course.value = await getCourse(route.params.id)
  materials.value = await listMaterialsWithStatus(route.params.id, store.user.id)
  if (materials.value.length > 0) {
    currentMaterial.value = materials.value[0]
    await checkReadStatus()
  }
  loading.value = false
})

onUnmounted(() => { if (timer) clearInterval(timer) })

watch(currentMaterial, async () => {
  if (timer) clearInterval(timer)
  elapsed.value = 0
  readCompleted.value = false
  if (currentMaterial.value) {
    await checkReadStatus()
  }
})

async function checkReadStatus() {
  try {
    const record = await startReadMaterial(currentMaterial.value.id, store.user.id)
    const startTime = record?.readTime
    if (record?.completed === 1) {
      readCompleted.value = true
      currentMaterial.value.completed = true
      return
    }
    if (startTime) {
      const start = new Date(startTime).getTime()
      if (!isNaN(start)) {
        elapsed.value = Math.max(0, Math.floor((Date.now() - start) / 1000))
      }
    }
    timer = setInterval(() => { elapsed.value++ }, 1000)
  } catch (_) {}
}

async function selectMaterial(m) {
  currentMaterial.value = m
}

async function handleComplete() {
  completing.value = true
  await completeReadMaterial(currentMaterial.value.id, store.user.id)
  readCompleted.value = true
  currentMaterial.value.completed = true
  ElMessage.success('已完成阅读')
  if (timer) clearInterval(timer)
  completing.value = false
}
</script>

<style scoped>
.material-item {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 4px;
  transition: background 0.2s;
}
.material-item:hover { background: #f1f5f9; }
.material-item.active { background: #eef2ff; }
</style>
