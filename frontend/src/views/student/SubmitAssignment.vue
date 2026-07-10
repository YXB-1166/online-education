<template>
  <div>
    <div class="page-title">提交作业</div>
    <el-card style="max-width:700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="作业编号">
          <el-tag>{{ $route.params.aid }}</el-tag>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
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
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入作业内容..." />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting" round>提交作业</el-button>
          <el-button @click="$router.back()" round>返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { addSubmission, uploadFile as uploadFileApi } from '../../api/exam'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const submitting = ref(false)
const form = reactive({ content: '' })
const attachmentUrl = ref('')
const fileList = ref([])
const rules = { content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }] }

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
</script>
