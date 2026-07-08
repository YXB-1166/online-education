<template>
  <div>
    <div class="page-title">创建课程</div>
    <el-card style="max-width:600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="容量" prop="maxStudents">
          <el-input-number v-model="form.maxStudents" :min="1" :max="200" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" round>创建课程</el-button>
          <el-button @click="$router.back()" round>返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { addCourse } from '../../api/course'

const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const form = reactive({ courseName: '', description: '', credit: 4, maxStudents: 50 })
const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  await addCourse({ ...form, teacherId: store.user.id })
  ElMessage.success('创建成功')
  router.push('/teacher/courses')
}
</script>
