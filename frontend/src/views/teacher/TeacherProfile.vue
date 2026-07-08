<template>
  <div>
    <div class="page-title">个人设置</div>
    <el-card style="max-width:500px" v-loading="loading">
      <template #header><span style="font-weight:600">联系方式</span></template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input :model-value="store.user?.realName" disabled />
        </el-form-item>
        <el-form-item label="职称">
          <el-input :model-value="store.user?.title || '教师'" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="saving" round>保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { updateProfile } from '../../api/user'

const store = useUserStore()
const loading = ref(false)
const saving = ref(false)
const form = reactive({ email: '', phone: '' })

onMounted(() => {
  form.email = store.user?.email || ''
  form.phone = store.user?.phone || ''
})

async function handleSubmit() {
  saving.value = true
  try {
    await updateProfile({ email: form.email, phone: form.phone })
    store.user.email = form.email
    store.user.phone = form.phone
    ElMessage.success('保存成功')
  } catch (_) {}
  saving.value = false
}
</script>
