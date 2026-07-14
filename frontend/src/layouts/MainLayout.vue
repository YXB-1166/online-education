<template>
  <el-container style="min-height: 100vh">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="app-sidebar">
      <div class="sidebar-brand" :style="{ justifyContent: isCollapse ? 'center' : 'flex-start' }">
        <div class="brand-mark">EDU</div>
        <div v-show="!isCollapse" class="brand-text">
          <strong>辅助教学系统</strong>
          <span>Teaching Console</span>
        </div>
      </div>
      <el-menu :collapse="isCollapse" :default-active="route.path" router class="sidebar-menu">
        <el-menu-item index="/courses">
          <el-icon><Monitor /></el-icon><span>课程广场</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/my-courses">
          <el-icon><Notebook /></el-icon><span>我的课程</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/my-assignments">
          <el-icon><Edit /></el-icon><span>我的作业</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/my-exams">
          <el-icon><Tickets /></el-icon><span>在线考试</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/learning-progress">
          <el-icon><TrendCharts /></el-icon><span>学习进度</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/grade-trend">
          <el-icon><DataLine /></el-icon><span>成绩趋势</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/messages">
          <el-icon><ChatDotSquare /></el-icon><span>站内信<small v-if="msgUnread > 0" class="msg-badge">{{ msgUnread }}</small></span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/student/profile">
          <el-icon><User /></el-icon><span>个人设置</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 2" index="/teacher/courses">
          <el-icon><Notebook /></el-icon><span>我的课程</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 2" index="/teacher/courses/create">
          <el-icon><Plus /></el-icon><span>创建课程</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 2" index="/teacher/grade-stats">
          <el-icon><DataAnalysis /></el-icon><span>成绩统计</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 2" index="/teacher/pending-approvals">
          <el-icon><Finished /></el-icon><span>选课审核</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 2" index="/messages">
          <el-icon><ChatDotSquare /></el-icon><span>站内信<small v-if="msgUnread > 0" class="msg-badge">{{ msgUnread }}</small></span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 2" index="/teacher/profile">
          <el-icon><User /></el-icon><span>个人设置</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 3" index="/admin/users">
          <el-icon><User /></el-icon><span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 3" index="/admin/courses">
          <el-icon><Notebook /></el-icon><span>课程管理</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 3" index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>数据概览</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <el-button @click="isCollapse = !isCollapse" text>
            <el-icon :size="20"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta?.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notif-badge">
            <el-button text @click="showNotifPanel = !showNotifPanel">
              <el-icon :size="20"><Bell /></el-icon>
            </el-button>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <div class="user-trigger">
              <el-avatar :size="34" class="user-avatar">
                {{ store.user?.realName?.charAt(0) || store.user?.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ store.user?.realName || store.user?.username }}</span>
              <small class="user-role">{{ store.user?.role === 1 ? '学生' : store.user?.role === 2 ? '教师' : '管理员' }}</small>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="store.user?.role === 1 || store.user?.role === 2" command="profile">
                  <el-icon><User /></el-icon>个人设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
      <AssistantChat v-if="store.user?.role === 1 || store.user?.role === 2" />
    </el-container>

    <el-drawer v-model="showNotifPanel" title="课程通知" :size="380" direction="rtl" class="notif-drawer">
      <div v-if="notifications.length === 0" class="notif-empty">暂无通知</div>
      <div v-for="n in notifications" :key="n.id" class="notif-item" :class="{ 'notif-unread': !n.isRead }" @click="markAsRead(n)">
        <div class="notif-title">
          <span v-if="!n.isRead" class="notif-dot" />
          {{ n.title }}
        </div>
        <div class="notif-content">{{ n.content }}</div>
        <div class="notif-time">{{ n.createTime }}</div>
      </div>
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import AssistantChat from '../components/AssistantChat.vue'
import { getNotifications, getUnreadCount, markNotificationRead } from '../api/course'
import { getMessageUnreadCount } from '../api/message'
import { Plus, Finished, DataAnalysis, Edit, Tickets, DataLine, ChatDotSquare } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const isCollapse = ref(false)
const showNotifPanel = ref(false)
const notifications = ref([])
const unreadCount = ref(0)
const msgUnread = ref(0)

let notifTimer = null

onMounted(async () => {
  await Promise.all([loadMsgUnread()])
  if (store.user?.role === 1 || store.user?.role === 2) {
    await Promise.all([loadNotifications(), loadUnreadCount()])
    notifTimer = setInterval(() => { loadNotifications(); loadUnreadCount(); loadMsgUnread() }, 30000)
  }
})

onUnmounted(() => { if (notifTimer) clearInterval(notifTimer) })

async function loadNotifications() {
  try { notifications.value = await getNotifications(store.user.id) } catch (_) {}
}

async function loadUnreadCount() {
  try { unreadCount.value = await getUnreadCount(store.user.id) } catch (_) {}
}

async function loadMsgUnread() {
  try { msgUnread.value = await getMessageUnreadCount(store.user.id) } catch (_) {}
}

async function markAsRead(n) {
  if (n.isRead) return
  try {
    await markNotificationRead(n.id, store.user.id)
    n.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  } catch (_) {}
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    store.logout()
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push(store.user?.role === 1 ? '/student/profile' : '/teacher/profile')
  }
}
</script>

<style scoped>
.app-sidebar {
  background: linear-gradient(180deg, #fafcff 0%, #f0f4ff 100%);
  border-right: 1px solid #e2e8f0;
  transition: width 0.25s ease;
  overflow: hidden;
}

.sidebar-brand {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 18px;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #1e40af, #1677ff);
}

.brand-mark {
  width: 32px; height: 32px;
  display: grid; place-items: center;
  border-radius: 8px;
  background: rgba(255,255,255,0.2);
  color: #fff;
  font-size: 13px; font-weight: 820;
  letter-spacing: 1px;
  flex-shrink: 0;
}

.brand-text strong {
  display: block;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.2;
}

.brand-text span {
  display: block;
  color: rgba(255,255,255,0.7);
  font-size: 11px;
  letter-spacing: 1px;
}

.sidebar-menu {
  background: transparent !important;
  border-right: none !important;
  padding-top: 8px;
}

.sidebar-menu .el-menu-item {
  color: #334155 !important;
  font-weight: 500;
  margin: 2px 8px;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
}

.sidebar-menu .el-sub-menu__title {
  color: #334155 !important;
  font-weight: 600;
  margin: 2px 8px;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background: #e8f0fe !important;
  color: #1677ff !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: #e8f0fe !important;
  color: #1677ff !important;
  font-weight: 700;
  border-right: 3px solid #1677ff;
  margin: 2px 8px;
  border-radius: 8px 0 0 8px;
}

.sidebar-menu .el-sub-menu.is-opened > .el-sub-menu__title {
  color: #1677ff !important;
  background: #e8f0fe !important;
  font-weight: 600;
}

.sidebar-menu .el-sub-menu .el-menu {
  background: transparent !important;
  border-left: none !important;
  margin: 0;
  padding-left: 16px;
}

.sidebar-menu .el-sub-menu .el-menu .el-menu-item {
  height: 38px;
  line-height: 38px;
  font-size: 13px;
}

.sidebar-menu .el-sub-menu .el-menu .el-menu-item.is-active {
  background: #e8f0fe !important;
  color: #1677ff !important;
  font-weight: 700;
  border-right: 3px solid #1677ff;
  margin: 2px 8px;
  border-radius: 8px 0 0 8px;
}

.app-header {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.header-left { display: flex; align-items: center; gap: 16px; }
.header-right { display: flex; align-items: center; gap: 12px; }

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  transition: background 0.2s;
}

.user-trigger:hover { background: #f1f5f9; }

.user-avatar {
  background: linear-gradient(135deg, #1677ff, #60a5fa) !important;
  font-weight: 700;
}

.user-name { font-size: 14px; font-weight: 500; color: #1e293b; }
.user-role { font-size: 12px; color: #94a3b8; }

.app-main {
  background: #f1f5f9;
  min-height: calc(100vh - 64px);
  padding: 24px;
}

.notif-badge { margin-right: 4px; }

.msg-badge {
  display: inline-block;
  min-width: 16px; height: 16px;
  line-height: 16px;
  padding: 0 5px;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  border-radius: 8px;
  margin-left: 4px;
  vertical-align: middle;
}

.notif-drawer :deep(.el-drawer__header) { color: #1e293b; font-weight: 700; }

.notif-empty { text-align: center; color: #94a3b8; padding: 40px 0; font-size: 14px; }

.notif-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.notif-item:hover { background: #f8fafc; }
.notif-unread { background: #eff6ff; }
.notif-unread:hover { background: #dbeafe; }

.notif-dot {
  display: inline-block;
  width: 8px; height: 8px;
  background: #1677ff;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}

.notif-title {
  font-weight: 600; font-size: 14px; color: #1e293b; margin-bottom: 4px;
}

.notif-content {
  font-size: 13px; color: #475569; line-height: 1.5;
}

.notif-time {
  font-size: 12px; color: #94a3b8; margin-top: 6px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
