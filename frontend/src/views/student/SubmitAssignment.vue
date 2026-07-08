<template>
  <div>
    <div class="page-title">提交作业</div>
    <el-card style="max-width:700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="作业编号">
          <el-tag>{{ $route.params.aid }}</el-tag>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入作业内容..." />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" round>提交作业</el-button>
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
import { addSubmission } from '../../api/exam'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const form = reactive({ content: '' })
const rules = { content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }] }

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  await addSubmission({ assignmentId: Number(route.params.aid), studentId: store.user.id, content: form.content })
  ElMessage.success('提交成功')
  router.back()
}
</script>
