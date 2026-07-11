<template>
  <div>
    <div class="page-title" v-if="record">考试结果</div>
    <div class="page-title" v-else>在线考试</div>

    <el-card v-if="!exam" v-loading="loading" style="text-align:center;padding:60px">
      加载中...
    </el-card>

    <template v-else-if="record">
      <el-card>
        <div style="text-align:center;padding:20px">
          <h2 style="margin-bottom:12px">{{ exam.title }}</h2>
          <el-result :icon="passed ? 'success' : 'warning'" :title="'得分：' + record.score + ' / ' + exam.totalScore" :sub-title="passed ? '通过' : '未通过'">
            <template #extra>
              <el-tag v-if="record.autoScore !== null" type="info">自动批改得分：{{ record.autoScore }}</el-tag>
            </template>
          </el-result>
        </div>
        <el-divider />
        <div v-for="(q, i) in questions" :key="q.id" class="review-item">
          <div class="q-header">
            <span class="q-num">{{ i + 1 }}.</span>
            <el-tag :type="q.type === 1 ? 'primary' : 'warning'" size="small">{{ q.type === 1 ? '选择' : '判断' }}</el-tag>
            <span style="margin-left:8px;color:#475569;font-size:13px">{{ q.score }}分</span>
          </div>
          <div class="q-content">{{ q.content }}</div>
          <div v-if="q.type === 1" class="review-options">
            <div v-for="opt in parsedOptions(q)" :key="opt.k" class="opt-line" :class="{ 'opt-correct': opt.k === q.answer, 'opt-wrong': opt.k === userAnswer(q.id) && opt.k !== q.answer }">
              <span class="opt-label">{{ opt.k }}.</span>
              <span>{{ opt.v }}</span>
              <el-icon v-if="opt.k === q.answer" color="#67c23a"><Check /></el-icon>
              <el-icon v-if="opt.k === userAnswer(q.id) && opt.k !== q.answer" color="#f56c6c"><Close /></el-icon>
            </div>
          </div>
          <div v-else class="review-answer">
            <span>你的答案：<strong :class="userAnswer(q.id) === q.answer ? 'text-green' : 'text-red'">{{ userAnswer(q.id) === 'T' ? '正确' : userAnswer(q.id) === 'F' ? '错误' : '未作答' }}</strong></span>
            <span style="margin-left:16px">正确答案：<strong class="text-green">{{ q.answer === 'T' ? '正确' : '错误' }}</strong></span>
          </div>
        </div>
        <div style="text-align:center;margin-top:20px">
          <el-button @click="$router.push('/my-exams')" round>返回考试列表</el-button>
        </div>
      </el-card>
    </template>

    <template v-else>
      <el-card style="margin-bottom:16px">
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <h2>{{ exam.title }}</h2>
            <p style="color:#64748b;font-size:14px;margin-top:6px">
              时长：{{ exam.duration }} 分钟 &nbsp;|&nbsp; 总分：{{ exam.totalScore }} &nbsp;|&nbsp;
              共 {{ questions.length }} 题
            </p>
          </div>
          <div style="text-align:right">
            <div style="font-size:24px;font-weight:700;color:#1677ff">{{ formatTime(remaining) }}</div>
            <div style="color:#94a3b8;font-size:13px">剩余时间</div>
          </div>
        </div>
      </el-card>

      <el-card>
        <div v-for="(q, i) in questions" :key="q.id" class="question-card">
          <div class="q-header">
            <span class="q-num">{{ i + 1 }}.</span>
            <el-tag :type="q.type === 1 ? 'primary' : 'warning'" size="small">{{ q.type === 1 ? '选择题' : '判断题' }}</el-tag>
            <span style="margin-left:8px;color:#475569;font-size:13px">{{ q.score }}分</span>
          </div>
          <div class="q-content">{{ q.content }}</div>
          <div v-if="q.type === 1" class="choice-group">
            <el-radio-group v-model="answers[q.id]">
              <div v-for="opt in parsedOptions(q)" :key="opt.k" class="choice-item">
                <el-radio :value="opt.k" size="large">{{ opt.k }}. {{ opt.v }}</el-radio>
              </div>
            </el-radio-group>
          </div>
          <div v-else class="judge-group">
            <el-radio-group v-model="answers[q.id]">
              <el-radio value="T" size="large">正确</el-radio>
              <el-radio value="F" size="large">错误</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div style="text-align:center;margin-top:24px">
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit" round>提交答卷</el-button>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { getExam, getExamQuestions, getExamRecord, getExamSilent, submitExam } from '../../api/exam-online'
import { Check, Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const examId = route.params.id

const loading = ref(true)
const exam = ref(null)
const questions = ref([])
const answers = reactive({})
const submitting = ref(false)
const record = ref(null)
let timer = null
const remaining = ref(0)

const passed = computed(() => record.value && (exam.value ? record.value.score >= exam.value.totalScore * 0.6 : true))

onMounted(async () => {
  try {
    const existing = await getExamRecord(examId, store.user.id).catch(() => null)
    if (existing) {
      record.value = existing
      getExamSilent(examId).then(e => exam.value = e).catch(() => {})
      getExamQuestions(examId).then(q => questions.value = q).catch(() => {})
    } else {
      exam.value = await getExam(examId)
      questions.value = await getExamQuestions(examId)
      remaining.value = exam.value.duration * 60
      timer = setInterval(() => { if (remaining.value > 0) remaining.value-- }, 1000)
    }
  } catch (_) { ElMessage.warning('考试不存在或已删除'); router.push('/my-exams') }
  loading.value = false
})

onUnmounted(() => { if (timer) clearInterval(timer) })

function parsedOptions(q) {
  if (!q.options) return []
  return q.options.split('|').filter(p => p).map(p => {
    const [k, ...rest] = p.split(':')
    return { k, v: rest.join(':') }
  })
}

function userAnswer(qid) {
  try {
    const map = JSON.parse(record.value.answers)
    return map[qid] || ''
  } catch { return '' }
}

function formatTime(sec) {
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return m + ':' + String(s).padStart(2, '0')
}

async function handleSubmit() {
  const unanswered = questions.value.filter(q => !answers[q.id])
  if (unanswered.length > 0) {
    const ok = await ElMessageBox.confirm('还有 ' + unanswered.length + ' 道题未作答，确认提交？', '提示', { confirmButtonText: '确认提交', cancelButtonText: '继续答题', type: 'warning' }).catch(() => false)
    if (!ok) return
  }
  submitting.value = true
  try {
    record.value = await submitExam(examId, store.user.id, { ...answers })
    if (timer) clearInterval(timer)
    ElMessage.success('提交成功')
  } catch (_) { ElMessage.error('提交失败') }
  submitting.value = false
}
</script>

<style scoped>
.question-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}
.q-header { display: flex; align-items: center; margin-bottom: 8px; }
.q-num { font-weight: 700; color: #1677ff; margin-right: 6px; }
.q-content { font-size: 15px; margin-bottom: 12px; line-height: 1.6; }
.choice-group .choice-item { padding: 8px 0; }
.judge-group { display: flex; gap: 24px; }
.review-item { border-bottom: 1px solid #f0f0f0; padding: 14px 0; }
.review-options .opt-line { padding: 4px 8px; border-radius: 4px; margin: 2px 0; display: flex; align-items: center; gap: 6px; }
.opt-correct { background: #f0f9eb; }
.opt-wrong { background: #fef0f0; }
.review-answer { font-size: 14px; }
.text-green { color: #67c23a; }
.text-red { color: #f56c6c; }
</style>
