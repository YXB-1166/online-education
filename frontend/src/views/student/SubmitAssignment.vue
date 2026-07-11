<template>
  <div>
    <div class="page-title">{{ isReadonly ? '查看提交' : '提交作业' }}</div>
    <el-card style="max-width:700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="作业编号">
          <el-tag>{{ $route.params.aid }}</el-tag>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            v-if="!isReadonly"
            :http-request="uploadFile"
            :before-upload="beforeUpload"
            :file-list="fileList"
            :limit="1"
            :on-exceed="() => ElMessage.warning('只能上传一个文件')"
            list-type="text"
          >
            <el-button type="primary" plain round size="small">选择文件</el-button>
            <template #tip>
              <span style="font-size:12px;color:#999;margin-left:8px">支持 .doc .docx .pdf .zip .rar .jpg .png，最大 50MB</span>
            </template>
          </el-upload>
          <div v-else-if="existingSubmission?.attachmentUrl" style="display:flex;align-items:center;gap:8px">
            <el-tag type="info" effect="plain">已上传附件</el-tag>
            <el-link :href="existingSubmission.attachmentUrl" target="_blank" type="primary" :underline="false">下载查看</el-link>
          </div>
          <span v-else-if="isReadonly" style="color:#94a3b8;font-size:13px">无附件</span>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" :disabled="isReadonly" :placeholder="isReadonly ? '' : '请输入作业内容...'" />
        </el-form-item>
        <el-form-item v-if="isReadonly && existingSubmission?.submitTime" label="提交时间">
          <span style="color:#64748b;font-size:13px">{{ existingSubmission.submitTime?.replace('T', ' ') }}</span>
        </el-form-item>
        <el-form-item v-if="isReadonly && existingSubmission?.score != null" label="得分">
          <span style="font-size:18px;font-weight:700;color:#4f46e5">{{ existingSubmission.score }} 分</span>
        </el-form-item>
        <el-form-item v-if="isReadonly && existingSubmission?.comment" label="评语">
          <p style="color:#475569;line-height:1.6;margin:0;white-space:pre-wrap">{{ existingSubmission.comment }}</p>
        </el-form-item>
        <el-form-item>
          <el-button v-if="!isReadonly" type="primary" @click="handleSubmit" :loading="submitting" round>提交作业</el-button>
          <el-button @click="$router.back()" round>{{ isReadonly ? '返回' : '取消' }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { addSubmission, uploadFile as uploadFileApi, listSubmissions } from '../../api/exam'
import { getCourse } from '../../api/course'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const submitting = ref(false)
const isReadonly = ref(route.query.readonly === '1')
const existingSubmission = ref(null)
const form = reactive({ content: '' })
const attachmentUrl = ref('')
const fileList = ref([])
const rules = { content: [{ required: !isReadonly.value, message: '请输入作业内容', trigger: 'blur' }] }

function beforeUpload(file) {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

async function uploadFile(options) {
  try {
    const res = await uploadFileApi(options.file)
    attachmentUrl.value = res.url
    fileList.value = [{ name: res.originalName || options.file.name, url: res.url }]
    ElMessage.success('文件上传成功')
  } catch (_) {
    options.onError()
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  submitting.value = true
  try {
    await addSubmission({
      assignmentId: Number(route.params.aid),
      studentId: store.user.id,
      content: form.content,
      attachmentUrl: attachmentUrl.value || null
    })
    ElMessage.success('提交成功')
    router.back()
  } catch (_) {
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  if (!isReadonly.value) {
    try {
      const course = await getCourse(route.params.cid)
      if (String(course.status) === '3') {
        ElMessage.warning('课程已结课，无法提交作业')
        router.back()
        return
      }
    } catch (_) { router.back(); return }
  }
  if (isReadonly.value) {
    const subs = await listSubmissions({ assignmentId: Number(route.params.aid), studentId: store.user.id })
    if (subs && subs.length > 0) {
      existingSubmission.value = subs[0]
      form.content = subs[0].content || ''
    }
  }
})
</script>
