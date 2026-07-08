<template>
  <div>
    <div class="page-title">编辑课程</div>
    <el-card style="max-width:600px" v-loading="loading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="容量" prop="maxStudents">
          <el-input-number v-model="form.maxStudents" :min="1" :max="200" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" round>保存</el-button>
          <el-button @click="$router.back()" round>返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourse, updateCourse } from '../../api/course'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ courseName: '', description: '', credit: 4, maxStudents: 50, id: null })
const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
}

onMounted(async () => {
  loading.value = true
  const c = await getCourse(route.params.id)
  Object.assign(form, c)
  loading.value = false
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  await updateCourse(form)
  ElMessage.success('保存成功')
  router.push('/teacher/courses')
}
</script>
