<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <el-card class="login-card" shadow="lg">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="36" color="#fff"><Reading /></el-icon>
        </div>
        <h2>在线教育辅助教学系统</h2>
        <p class="subtitle">Online Education Platform</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%;height:44px;font-size:16px;border-radius:10px">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <p>测试账号：zhangsan / 123456（学生）</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { login } from '../../api/user'

const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form.username, form.password)
    localStorage.setItem('token', res.token)
    store.setUser(res.user)
    router.push('/')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}
.login-bg {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: rgba(255,255,255,0.05);
  top: -200px;
  right: -200px;
}
.login-bg::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255,255,255,0.05);
  bottom: -300px;
  left: -100px;
}
.login-card {
  width: 400px;
  padding: 32px;
  border-radius: 20px !important;
  position: relative;
  z-index: 1;
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header .logo {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}
.login-header h2 {
  margin: 0 0 4px;
  font-size: 22px;
  color: #1e293b;
}
.login-header .subtitle {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
  letter-spacing: 2px;
}
.login-footer {
  text-align: center;
  margin-top: 16px;
}
.login-footer p {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}
</style>
