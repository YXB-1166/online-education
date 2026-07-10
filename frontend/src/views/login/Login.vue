<template>
  <main class="login-shell">
    <section class="hero-section">
      <div class="hero-overlay"></div>
      <div class="geo geo-one"></div>
      <div class="geo geo-two"></div>
      <div class="geo geo-three"></div>
      <div class="hero-content">
        <header class="hero-brand">
          <div class="brand-symbol">
            <el-icon :size="28"><ChatDotRound /></el-icon>
          </div>
          <div>
            <h1>在线教育辅助教学系统</h1>
            <p>Online Education Platform</p>
          </div>
        </header>

        <section class="hero-intro">
          <span class="hero-kicker">高校教务系统 · AI 教育平台</span>
          <h2>打造融合人工智能的在线教育平台</h2>
          <p>支持课程学习、在线考试、课程论坛、AI 课程助手、学习预警等功能，为教师教学管理与学生自主学习提供稳定、高效、清晰的数字化支撑。</p>
        </section>

        <section class="capability-grid">
          <article v-for="item in capabilities" :key="item.title" class="capability-card">
            <div class="capability-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </article>
        </section>

        <section class="stats-panel">
          <div v-for="item in stats" :key="item.label" class="stat-item">
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </section>

        <footer class="hero-footer">© 2026 在线教育辅助教学系统</footer>
      </div>
    </section>

    <section class="login-side">
      <div class="login-card" :class="{ 'admin-card': isAdminMode }">
        <div class="login-heading">
          <span class="login-eyebrow">{{ isAdminMode ? 'Admin Access' : '欢迎加入' }}</span>
          <h2>{{ isAdminMode ? '管理员登录' : '用户登录' }}</h2>
          <p>{{ isAdminMode ? 'Administrator Portal' : 'Sign in to your account' }}</p>
        </div>

        <el-tabs v-model="tab" stretch class="login-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top" size="large" class="modern-form" @keyup.enter="handleLogin" @submit.prevent>
              <el-form-item label="账号" prop="username">
                <el-input v-model="loginForm.username" autocomplete="username" placeholder="请输入账号" :prefix-icon="User" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="loginForm.password" type="password" autocomplete="current-password" show-password placeholder="请输入密码" :prefix-icon="Lock" />
              </el-form-item>
              <template v-if="!isAdminMode">
                <el-form-item label="验证码" prop="captcha">
                  <div class="captcha-row">
                    <el-input v-model="loginForm.captcha" placeholder="请输入验证码" :prefix-icon="Key" />
                    <button class="captcha-code" type="button" title="点击刷新" @click="refreshCaptcha">
                      <span>{{ captchaCode }}</span>
                      <el-icon><RefreshRight /></el-icon>
                    </button>
                  </div>
                </el-form-item>
                <div class="form-options">
                  <el-checkbox v-model="rememberMe">记住登录</el-checkbox>
                </div>
              </template>
              <el-button class="login-submit" :loading="loginLoading" type="primary" @click="handleLogin">登 录</el-button>
              <div v-if="!isAdminMode" class="register-line">
                <span>还没有账号？</span>
                <el-button text type="primary" @click="tab = 'register'">注册账号</el-button>
              </div>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-position="top" size="large" class="modern-form" @keyup.enter="handleRegister" @submit.prevent>
              <el-form-item label="用户名" prop="username">
                <el-input v-model="regForm.username" placeholder="请设置用户名" :prefix-icon="User" />
              </el-form-item>
              <el-form-item label="姓名" prop="realName">
                <el-input v-model="regForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="regForm.password" type="password" show-password placeholder="请设置密码" :prefix-icon="Lock" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="regForm.confirmPassword" type="password" show-password placeholder="请再次输入密码" :prefix-icon="Lock" />
              </el-form-item>
              <el-button class="login-submit" :loading="regLoading" type="primary" @click="handleRegister">注 册</el-button>
              <div class="register-line">
                <span>已有账号？</span>
                <el-button text type="primary" @click="tab = 'login'">返回登录</el-button>
              </div>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="mode-switch">
          <el-button v-if="!isAdminMode" text type="primary" @click="switchMode(true)">
            <el-icon><UserFilled /></el-icon>管理员身份登录
          </el-button>
          <el-button v-else text type="primary" @click="switchMode(false)">
            <el-icon><User /></el-icon>返回普通用户登录
          </el-button>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key, RefreshRight, ChatDotRound, Reading, DocumentChecked, DataAnalysis, Notebook, Aim, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { login, register } from '../../api/user'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const tab = ref('login')
const isAdminMode = ref(false)
const rememberMe = ref(true)
const loginLoading = ref(false)
const regLoading = ref(false)
const loginFormRef = ref(null)
const regFormRef = ref(null)
const captchaCode = ref(generateCaptcha())

const loginForm = reactive({ username: '', password: '', captcha: '' })
const regForm = reactive({ username: '', realName: '', password: '', confirmPassword: '' })

const loginRules = {
  username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  captcha: [{ required: true, message: '验证码不能为空', trigger: 'blur' }]
}

const regRules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (rule, value, cb) => value === regForm.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
  ]
}

const capabilities = [
  { icon: Reading, title: '智能课程问答', description: '围绕课程资料提供即时答疑与学习引导。' },
  { icon: DocumentChecked, title: 'AI 作业评语', description: '辅助教师生成结构化反馈，提升批改效率。' },
  { icon: DataAnalysis, title: '学习进度分析', description: '汇总学习行为，识别进度变化与薄弱环节。' },
  { icon: Notebook, title: '知识点摘要', description: '自动提炼章节重点，帮助学生快速复习。' },
  { icon: Aim, title: '智能组卷', description: '按知识点和难度组合练习题与阶段测验。' }
]

const stats = [
  { label: '课程数量', value: '120+' },
  { label: '教师', value: '80+' },
  { label: '学生', value: '3500+' },
  { label: '今日在线', value: '850+' }
]

function generateCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let result = ''
  for (let i = 0; i < 4; i++) result += chars[Math.floor(Math.random() * chars.length)]
  return result
}

function refreshCaptcha() {
  captchaCode.value = generateCaptcha()
  loginForm.captcha = ''
}

function switchMode(admin) {
  isAdminMode.value = admin
  loginForm.username = ''
  loginForm.password = ''
  loginForm.captcha = ''
  loginFormRef.value?.clearValidate()
  if (!admin) refreshCaptcha()
}

async function handleLogin() {
  const valid = await loginFormRef.value.validate().catch(() => {})
  if (!valid) return
  if (!isAdminMode.value && loginForm.captcha.trim().toUpperCase() !== captchaCode.value) {
    ElMessage.error('验证码不正确')
    refreshCaptcha()
    return
  }
  loginLoading.value = true
  try {
    const res = await login(loginForm.username.trim(), loginForm.password)
    if (isAdminMode.value && res.user.role !== 3) {
      ElMessage.error('请使用管理员账号登录')
      return
    }
    if (!isAdminMode.value && res.user.role === 3) {
      ElMessage.error('管理员账号请切换至管理员登录')
      refreshCaptcha()
      return
    }
    localStorage.setItem('token', res.token)
    store.setUser(res.user)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/courses')
  } catch (e) {
    if (!isAdminMode.value) refreshCaptcha()
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  const valid = await regFormRef.value.validate().catch(() => {})
  if (!valid) return
  regLoading.value = true
  try {
    const res = await register({ username: regForm.username.trim(), password: regForm.password, realName: regForm.realName })
    ElMessage.success('注册成功')
    localStorage.setItem('token', res.token)
    store.setUser(res.user)
    router.push('/courses')
  } catch (_) {
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 60fr) minmax(420px, 40fr);
  overflow: hidden;
}

.hero-section {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  color: #fff;
  background: linear-gradient(135deg, rgba(13, 71, 161, 0.94), rgba(22, 119, 255, 0.9)), #1677ff;
  animation: heroIn 640ms ease both;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(255,255,255,0.08), transparent 36%), linear-gradient(180deg, transparent, rgba(3,33,83,0.2));
}

.geo {
  position: absolute;
  border: 1px solid rgba(255,255,255,0.16);
  background: rgba(255,255,255,0.06);
  pointer-events: none;
}
.geo-one { width: 220px; height: 220px; right: 10%; top: 9%; transform: rotate(24deg); }
.geo-two { width: 320px; height: 320px; right: -120px; bottom: 16%; border-radius: 50%; }
.geo-three { width: 180px; height: 100px; left: 8%; bottom: 12%; transform: skewX(-16deg); }

.hero-content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 30px;
  justify-content: center;
  max-width: 980px;
  padding: 48px clamp(40px, 7vw, 80px);
}

.hero-brand {
  display: flex;
  gap: 16px;
  align-items: center;
}
.brand-symbol {
  width: 56px; height: 56px;
  display: grid; place-items: center;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 12px;
  background: rgba(255,255,255,0.16);
  box-shadow: 0 18px 40px rgba(6,36,86,0.2);
}
.hero-brand h1 { font-size: 25px; line-height: 1.25; font-weight: 800; }
.hero-brand p { margin-top: 5px; color: rgba(255,255,255,0.76); font-size: 14px; }

.hero-intro { max-width: 720px; }
.hero-kicker { display: block; margin-bottom: 14px; color: rgba(255,255,255,0.78); font-size: 14px; font-weight: 700; }
.hero-intro h2 { font-size: clamp(34px, 4.4vw, 52px); line-height: 1.12; font-weight: 820; }
.hero-intro p { max-width: 680px; margin-top: 18px; color: rgba(255,255,255,0.82); font-size: 17px; line-height: 1.8; }

.capability-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}
.capability-card {
  min-height: 140px;
  display: flex; flex-direction: column; gap: 14px;
  padding: 18px;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 8px;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(10px);
  transition: transform 180ms ease, background 180ms ease, box-shadow 180ms ease;
}
.capability-card:hover {
  transform: translateY(-3px) scale(1.015);
  background: rgba(255,255,255,0.17);
  box-shadow: 0 18px 36px rgba(3,28,72,0.2);
}
.capability-icon {
  width: 38px; height: 38px;
  display: grid; place-items: center;
  border-radius: 8px;
  background: rgba(255,255,255,0.18);
}
.capability-icon .el-icon { font-size: 21px; }
.capability-card h3 { font-size: 16px; line-height: 1.35; font-weight: 780; }
.capability-card p { margin-top: 7px; color: rgba(255,255,255,0.76); font-size: 13px; line-height: 1.55; }

.stats-panel {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
.stat-item { padding: 18px; border: 1px solid rgba(255,255,255,0.18); border-radius: 8px; background: rgba(255,255,255,0.12); }
.stat-item strong { display: block; color: #fff; font-size: 28px; line-height: 1; font-weight: 820; }
.stat-item span { display: block; margin-top: 10px; color: rgba(255,255,255,0.72); font-size: 14px; }

.hero-footer { color: rgba(255,255,255,0.66); font-size: 13px; }

@keyframes heroIn { from { opacity: 0; transform: translateX(-12px); } to { opacity: 1; transform: translateX(0); } }

.login-side {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: radial-gradient(circle at 84% 12%, rgba(22,119,255,0.14), transparent 30%), linear-gradient(135deg, #eef6ff 0%, #f7fbff 46%, #fff 100%);
  animation: fadeIn 560ms ease both;
}

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.login-card {
  width: min(100%, 420px);
  padding: 36px;
  border: 1px solid rgba(220,232,246,0.94);
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 24px 60px rgba(29,74,132,0.14);
  animation: cardIn 620ms ease both;
}

@keyframes cardIn { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }

.login-heading { margin-bottom: 24px; }
.login-eyebrow { display: block; margin-bottom: 8px; color: #1677ff; font-size: 13px; font-weight: 800; }
.login-heading h2 { color: #162033; font-size: 26px; line-height: 1.2; font-weight: 820; }
.login-heading p { margin-top: 6px; color: #77869a; font-size: 14px; }

.login-tabs :deep(.el-tabs__item) { font-size: 15px; font-weight: 600; }
.login-tabs :deep(.el-tabs__item.is-active) { color: #1677ff; }
.login-tabs :deep(.el-tabs__active-bar) { background: #1677ff; }

.modern-form { display: grid; gap: 2px; }
.modern-form :deep(.el-form-item__label) { color: #2d3b50; font-weight: 700; }
.modern-form :deep(.el-input__wrapper) { min-height: 46px; border-radius: 8px; box-shadow: 0 0 0 1px #dbe6f2 inset; }
.modern-form :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px rgba(22,119,255,0.5) inset; }
.modern-form :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #1677ff inset, 0 0 0 3px rgba(22,119,255,0.12); }

.captcha-row {
  width: 100%;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 118px;
  gap: 10px;
}
.captcha-code {
  height: 46px;
  display: inline-flex; align-items: center; justify-content: center; gap: 8px;
  border: 1px solid #dbe6f2;
  border-radius: 8px;
  background: linear-gradient(135deg, #f1f7ff, #fff);
  color: #1677ff;
  font: inherit;
  font-weight: 820;
  letter-spacing: 2px;
  cursor: pointer;
  transition: border-color 180ms ease, box-shadow 180ms ease, transform 180ms ease;
}
.captcha-code:hover {
  border-color: rgba(22,119,255,0.58);
  box-shadow: 0 10px 24px rgba(22,119,255,0.12);
  transform: translateY(-1px);
}
.captcha-code .el-icon { font-size: 15px; letter-spacing: 0; }

.form-options { display: flex; align-items: center; gap: 12px; margin: 2px 0 16px; }

.login-submit {
  width: 100%; height: 46px; border-radius: 8px;
  background: #1677ff;
  font-weight: 780;
  transition: box-shadow 180ms ease, transform 180ms ease, background 180ms ease;
}
.login-submit:hover {
  background: #176bf0;
  box-shadow: 0 12px 26px rgba(22,119,255,0.28);
  transform: translateY(-1px);
}

.register-line { display: flex; align-items: center; justify-content: center; gap: 4px; margin-top: 18px; color: #7a8798; font-size: 14px; }
.mode-switch { margin-top: 16px; }
.mode-switch .el-button { width: 100%; }

@media (max-width: 1100px) {
  .login-shell { grid-template-columns: 1fr; }
  .hero-section, .hero-content { min-height: auto; }
  .hero-content { padding: 36px 24px; }
  .hero-section { display: none; }
  .login-side { min-height: 100vh; padding: 36px 18px 44px; }
}

@media (max-width: 700px) {
  .capability-grid, .stats-panel { display: none; }
  .hero-content { gap: 24px; }
  .hero-intro h2 { font-size: 30px; }
  .hero-brand h1 { font-size: 20px; }
}

@media (max-width: 520px) {
  .login-card { padding: 26px 18px; border-radius: 12px; }
  .captcha-row { grid-template-columns: 1fr; }
}
</style>
