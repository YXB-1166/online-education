<template>
  <div>
    <div class="page-title">发布作业</div>
    <el-card style="max-width:600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入作业要求" />
        </el-form-item>
        <el-form-item label="满分" prop="fullScore">
          <el-input-number v-model="form.fullScore" :min="0" :max="200" />
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker v-model="form.deadline" type="datetime" format="YYYY-MM-DD HH:mm:ss" placeholder="请选择截止时间" style="width:100%" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" round>发布作业</el-button>
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
import { addAssignment } from '../../api/exam'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const form = reactive({ title: '', content: '', fullScore: 100, deadline: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'blur' }]
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  await addAssignment({ courseId: Number(route.params.id), teacherId: store.user.id, ...form })
  ElMessage.success('发布成功')
  router.push('/teacher/courses')
}
</script>
