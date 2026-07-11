<template>
  <div>
    <div class="page-title">{{ isEdit ? '编辑考试' : '创建考试' }}</div>
    <el-card style="margin-bottom:20px">
      <el-form :model="examForm" label-width="100px" size="large">
        <el-row :gutter="20">
          <el-col :xs="24" :md="14">
            <el-form-item label="考试标题" required>
              <el-input v-model="examForm.title" placeholder="输入考试标题" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="5">
            <el-form-item label="时长(分)" label-width="65px">
              <el-input v-model.number="examForm.duration" type="number" :min="5" :max="300" placeholder="60" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="5">
            <el-form-item label="自动批改" label-width="65px">
              <el-checkbox v-model="examForm.autoGradeChoice">选择</el-checkbox>
              <el-checkbox v-model="examForm.autoGradeJudge">判断</el-checkbox>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :xs="24" :md="10">
            <el-form-item label="截止时间">
              <el-date-picker v-model="examForm.endTime" type="datetime" placeholder="不设置则无截止" format="YYYY-MM-DD HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display:flex;align-items:center;gap:12px;flex-wrap:wrap">
          <span>题目列表</span>
          <el-divider direction="vertical" />
          <el-button size="small" @click="addQuestion(1)" round>添加选择题</el-button>
          <el-button size="small" @click="addQuestion(2)" round>添加判断题</el-button>
          <el-divider direction="vertical" />
          <span style="font-size:13px;color:#94a3b8">自动生成：</span>
          <el-input-number v-model="autoChoiceCount" :min="0" :max="50" size="small" style="width:100px" /> <span style="font-size:13px;color:#64748b">道选择</span>
          <el-input-number v-model="autoJudgeCount" :min="0" :max="50" size="small" style="width:100px" /> <span style="font-size:13px;color:#64748b">道判断</span>
          <el-button size="small" type="success" :loading="generating" @click="handleAutoGenerate" round>生成题目</el-button>
        </div>
      </template>
      <div v-if="questions.length === 0" style="text-align:center;padding:40px;color:#94a3b8">暂无题目，请添加</div>
      <div v-for="(q, i) in questions" :key="i" class="question-card">
        <div class="q-header">
          <span class="q-num">{{ i + 1 }}.</span>
          <el-tag :type="q.type === 1 ? 'primary' : 'warning'" size="small">{{ q.type === 1 ? '选择' : '判断' }}</el-tag>
          <el-button text type="danger" size="small" @click="questions.splice(i, 1)" style="margin-left:auto">删除</el-button>
        </div>
        <div class="q-content">
          <el-input v-model="q.content" :placeholder="'请输入题目' + (i + 1)" :rows="2" type="textarea" />
        </div>
        <div v-if="q.type === 1" class="q-options">
          <div v-for="opt in ['A','B','C','D']" :key="opt" class="opt-item">
            <span class="opt-label">{{ opt }}.</span>
            <el-input v-model="q._opts[opt]" :placeholder="'选项' + opt" size="small" />
          </div>
        </div>
        <div class="q-footer">
          <div class="q-answer">
            <span>正确答案：</span>
            <el-select v-model="q.answer" size="small" style="width:100px">
              <el-option v-if="q.type === 1" v-for="opt in 'ABCD'.split('')" :key="opt" :label="opt" :value="opt" />
              <el-option v-else label="正确" value="T" /><el-option label="错误" value="F" />
            </el-select>
          </div>
          <div class="q-score">
            <span>分值：</span>
            <el-input-number v-model="q.score" :min="1" :max="100" size="small" style="width:100px" />
          </div>
        </div>
      </div>
      <div style="margin-top:20px;text-align:center">
        <el-button type="primary" size="large" :loading="saving" @click="handleSave" round>{{ isEdit ? '保存修改' : '创建考试' }}</el-button>
        <el-button type="success" size="large" :loading="publishing" @click="handleSaveAndPublish" round>保存并发布</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { addExam, getExam, getExamQuestions, saveExamQuestions, updateExam, autoGenerateQuestions, publishExam } from '../../api/exam-online'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const courseId = route.params.id
const editExamId = route.params.examId
const isEdit = !!editExamId
const localExamId = ref(editExamId ? Number(editExamId) : null)
const saving = ref(false)
const publishing = ref(false)
const generating = ref(false)
const autoChoiceCount = ref(5)
const autoJudgeCount = ref(5)

const examForm = reactive({
  title: '',
  duration: 60,
  endTime: null,
  autoGradeChoice: true,
  autoGradeJudge: true
})

const questions = ref([])

onMounted(async () => {
  if (isEdit) {
    const exam = await getExam(editExamId)
    Object.assign(examForm, exam)
    examForm.autoGradeChoice = exam.autoGradeChoice === 1
    examForm.autoGradeJudge = exam.autoGradeJudge === 1
    const qs = await getExamQuestions(editExamId)
    questions.value = qs.map(q => {
      const opts = {}
      if (q.options) {
        q.options.split('|').forEach(p => { const [k, v] = p.split(':'); if (k) opts[k] = v || '' })
      }
      return { ...q, _opts: opts }
    })
  }
})

function addQuestion(type) {
  questions.value.push({
    examId: localExamId.value || 0,
    type,
    content: '',
    options: '',
    answer: type === 1 ? 'A' : 'T',
    score: 10,
    sortOrder: questions.value.length,
    _opts: { A: '', B: '', C: '', D: '' }
  })
}

function buildExamPayload() {
  const endTime = examForm.endTime
  return {
    ...examForm,
    courseId,
    teacherId: store.user.id,
    endTime: endTime ? formatDate(endTime) : null,
    autoGradeChoice: examForm.autoGradeChoice ? 1 : 0,
    autoGradeJudge: examForm.autoGradeJudge ? 1 : 0
  }
}

function formatDate(d) {
  if (!d) return null
  const dt = new Date(d)
  if (isNaN(dt.getTime())) return null
  const pad = n => String(n).padStart(2, '0')
  return dt.getFullYear() + '-' + pad(dt.getMonth()+1) + '-' + pad(dt.getDate()) + 'T' + pad(dt.getHours()) + ':' + pad(dt.getMinutes()) + ':' + pad(dt.getSeconds())
}

async function ensureExamCreated() {
  if (!localExamId.value) {
    const exam = await addExam(buildExamPayload())
    localExamId.value = exam.id
  }
  return localExamId.value
}

function validateQuestions() {
  if (!examForm.title.trim()) { ElMessage.warning('请输入考试标题'); return false }
  if (questions.value.length === 0) { ElMessage.warning('请至少添加一道题目'); return false }
  for (const q of questions.value) {
    if (!q.content.trim()) { ElMessage.warning('请填写所有题目内容'); return false }
    if (q.type === 1) {
      const optsStr = Object.entries(q._opts).filter(([_, v]) => v.trim()).map(([k, v]) => k + ':' + v).join('|')
      if (!optsStr) { ElMessage.warning('请填写选项内容'); return false }
      q.options = optsStr
    }
  }
  return true
}

async function saveCurrentQuestions() {
  await saveExamQuestions(localExamId.value, questions.value.map(q => ({
    examId: localExamId.value, type: q.type, content: q.content, options: q.options, answer: q.answer, score: q.score
  })))
}

async function handleAutoGenerate() {
  const count = autoChoiceCount.value + autoJudgeCount.value
  if (count === 0) { ElMessage.warning('请设置要生成的题目数量'); return }
  if (count > 100) { ElMessage.warning('单次最多生成100道题'); return }
  if (!examForm.title.trim()) { ElMessage.warning('请先填写考试标题'); return }
  generating.value = true
  try {
    const targetId = await ensureExamCreated()
    const qs = await autoGenerateQuestions(targetId, autoChoiceCount.value, autoJudgeCount.value)
    questions.value = qs.map(q => {
      const opts = {}
      if (q.options) {
        q.options.split('|').forEach(p => { const [k, v] = p.split(':'); if (k) opts[k] = v || '' })
      }
      return { ...q, _opts: opts }
    })
    ElMessage.success('已生成 ' + qs.length + ' 道题目')
  } catch (e) { ElMessage.error('生成失败：' + (e.message || '未知错误')) }
  generating.value = false
}

async function handleSave() {
  if (!validateQuestions()) return
  saving.value = true
  try {
    const targetId = await ensureExamCreated()
    questions.value.forEach(q => q.examId = targetId)
    if (isEdit) {
      await updateExam({ ...buildExamPayload(), id: targetId })
    }
    await saveCurrentQuestions()
    ElMessage.success(isEdit ? '已保存' : '创建成功')
    if (!isEdit) router.push('/teacher/courses/' + courseId + '/exams')
  } finally { saving.value = false }
}

async function handleSaveAndPublish() {
  if (!validateQuestions()) return
  publishing.value = true
  try {
    const targetId = await ensureExamCreated()
    questions.value.forEach(q => q.examId = targetId)
    if (isEdit) {
      await updateExam({ ...buildExamPayload(), id: targetId })
    }
    await saveCurrentQuestions()
    await publishExam(targetId)
    ElMessage.success('已保存并发布')
    router.push('/teacher/courses/' + courseId + '/exams')
  } catch (_) { ElMessage.error('保存发布失败') }
  publishing.value = false
}
</script>

<style scoped>
.question-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}
.q-header { display: flex; align-items: center; gap: 8px; margin-bottom: 10px; }
.q-num { font-weight: 700; font-size: 16px; color: #1677ff; }
.q-content { margin-bottom: 10px; }
.q-options { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 10px; }
.opt-item { display: flex; align-items: center; gap: 6px; }
.opt-label { font-weight: 600; color: #475569; width: 18px; flex-shrink: 0; }
.q-footer { display: flex; gap: 24px; align-items: center; }
.q-answer, .q-score { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #475569; }
</style>
