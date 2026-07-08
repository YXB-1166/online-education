<template>
  <el-container style="min-height: 100vh">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="sidebar-header" :style="{ justifyContent: isCollapse ? 'center' : 'flex-start' }">
        <el-icon :size="28" color="#fff"><Reading /></el-icon>
        <span v-show="!isCollapse" class="sidebar-title">在线教育系统</span>
      </div>
      <el-menu :collapse="isCollapse" :default-active="route.path" router class="sidebar-menu">
        <el-menu-item index="/courses">
          <el-icon><Monitor /></el-icon><span>课程广场</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/my-courses">
          <el-icon><Notebook /></el-icon><span>我的课程</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/my-grades">
          <el-icon><Document /></el-icon><span>我的成绩</span>
        </el-menu-item>
        <el-menu-item v-if="store.user?.role === 1" index="/learning-progress">
          <el-icon><TrendCharts /></el-icon><span>学习进度</span>
        </el-menu-item>
        <el-sub-menu v-if="store.user?.role === 2" index="/teacher">
          <template #title>
            <el-icon><User /></el-icon><span>教师管理</span>
          </template>
          <el-menu-item index="/teacher/courses">我的课程</el-menu-item>
          <el-menu-item index="/teacher/courses/create">创建课程</el-menu-item>
          <el-menu-item index="/teacher/pending-approvals">选课审核</el-menu-item>
        </el-sub-menu>
        <el-sub-menu v-if="store.user?.role === 3" index="/admin">
          <template #title>
            <el-icon><Setting /></el-icon><span>系统管理</span>
          </template>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/courses">课程管理</el-menu-item>
          <el-menu-item index="/admin/dashboard">数据概览</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div class="topbar-left">
          <el-button @click="isCollapse = !isCollapse" text>
            <el-icon :size="20"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta?.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <el-badge v-if="store.user?.role === 1" :value="unreadCount" :hidden="unreadCount === 0" class="notif-badge">
          <el-button text @click="showNotifPanel = !showNotifPanel">
            <el-icon :size="22"><Bell /></el-icon>
          </el-button>
        </el-badge>
        <el-dropdown @command="handleCommand">
          <div class="user-avatar-wrap">
            <el-avatar :size="36" style="background:linear-gradient(135deg,#667eea,#764ba2)">
              {{ store.user?.realName?.charAt(0) || 'U' }}
            </el-avatar>
            <span class="user-name">{{ store.user?.realName || store.user?.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
      <AssistantChat v-if="store.user?.role === 1 || store.user?.role === 2" />
    </el-container>

    <el-drawer v-model="showNotifPanel" title="课程通知" :size="380" direction="rtl" v-if="store.user?.role === 1">
      <div v-if="notifications.length === 0" style="text-align:center;color:#999;padding:40px 0">暂无通知</div>
      <div v-for="n in notifications" :key="n.id" class="notif-item">
        <div class="notif-title">{{ n.title }}</div>
        <div class="notif-content">{{ n.content }}</div>
        <div class="notif-time">{{ n.createTime }}</div>
      </div>
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import AssistantChat from '../components/AssistantChat.vue'
import { getNotifications } from '../api/course'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const isCollapse = ref(false)
const showNotifPanel = ref(false)
const notifications = ref([])

const unreadCount = computed(() => notifications.value.length)

let notifTimer = null

onMounted(async () => {
  if (store.user?.role === 1) {
    await loadNotifications()
    notifTimer = setInterval(loadNotifications, 30000)
  }
})

onUnmounted(() => { if (notifTimer) clearInterval(notifTimer) })

async function loadNotifications() {
  try {
    notifications.value = await getNotifications(store.user.id)
  } catch (_) {}
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    store.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.sidebar {
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
  transition: width 0.25s ease;
  overflow: hidden;
}
.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #fff;
}
.sidebar-header .el-icon {
  color: #6366f1 !important;
}
.sidebar-title {
  color: #1e293b;
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
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
  background: #eef2ff !important;
  color: #4f46e5 !important;
}
.sidebar-menu .el-menu-item.is-active {
  background: #eef2ff !important;
  color: #4f46e5 !important;
  font-weight: 700;
  border-right: 4px solid #6366f1;
  margin: 2px 8px;
  border-radius: 8px 0 0 8px;
}
.sidebar-menu .el-sub-menu.is-opened > .el-sub-menu__title {
  color: #4f46e5 !important;
  background: #eef2ff !important;
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
  background: #eef2ff !important;
  color: #4f46e5 !important;
  font-weight: 700;
  border-right: 4px solid #6366f1;
  margin: 2px 8px;
  border-radius: 8px 0 0 8px;
}
.topbar {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-avatar-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  transition: background 0.2s;
}
.user-avatar-wrap:hover {
  background: #f1f5f9;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}
.main-content {
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
  padding: 24px;
}
.notif-badge { margin-right: 8px; }
.notif-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}
.notif-title {
  font-weight: 600;
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 4px;
}
.notif-content {
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
}
.notif-time {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 6px;
}
</style>
