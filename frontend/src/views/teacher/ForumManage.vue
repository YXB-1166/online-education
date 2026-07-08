<template>
  <div>
    <div class="page-title">论坛管理</div>
    <el-card v-loading="loading">
      <template v-if="posts.length === 0">
        <el-empty description="暂无帖子" />
      </template>
      <div v-for="p in posts" :key="p.id" class="post-item">
        <div class="post-header">
          <div class="post-user-avatar">{{ (userMap[p.userId] || '?').charAt(0) }}</div>
          <div class="post-meta">
            <span class="post-title">{{ p.title }}</span>
            <span class="post-info">{{ userMap[p.userId] || '未知' }} · {{ p.createTime }} · {{ p.replyCount || 0 }} 回复</span>
          </div>
          <el-button size="small" type="danger" @click="handleDelete(p.id)" round>删除</el-button>
        </div>
        <div class="post-content">{{ p.content }}</div>
        <el-button v-if="!p.showReplies" size="small" text @click="loadReplies(p)">查看回复 ({{ p.replyCount || 0 }})</el-button>
        <div v-if="p.showReplies" class="reply-section">
          <div v-if="p.replies && p.replies.length > 0">
            <div v-for="r in p.replies" :key="r.id" class="reply-item">
              <div class="reply-meta">{{ replyUserMap[r.userId] || '未知' }} · {{ r.createTime }}</div>
              <div class="reply-content">{{ r.content }}</div>
            </div>
          </div>
          <div v-else style="color:#94a3b8;padding:8px 0">暂无回复</div>
          <div class="reply-input">
            <el-input v-model="p.replyContent" placeholder="输入回复..." size="small" />
            <el-button size="small" type="primary" @click="handleReply(p)">回复</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { listForumPosts, deleteForumPost, getForumReplies, addForumReply } from '../../api/course'
import { listUsers } from '../../api/user'

const route = useRoute()
const store = useUserStore()
const posts = ref([])
const loading = ref(false)
const userMap = ref({})
const replyUserMap = ref({})

onMounted(async () => {
  loading.value = true
  const [postList, users] = await Promise.all([
    listForumPosts({ courseId: route.params.id }),
    listUsers()
  ])
  posts.value = postList
  users.forEach(u => { userMap.value[u.id] = u.realName; replyUserMap.value[u.id] = u.realName })
  loading.value = false
})

async function loadReplies(p) {
  p.showReplies = true
  p.replies = await getForumReplies(p.id)
}

async function handleReply(p) {
  if (!p.replyContent.trim()) return
  await addForumReply({ postId: p.id, userId: store.user.id, content: p.replyContent })
  ElMessage.success('回复成功')
  p.replyContent = ''
  p.replies = await getForumReplies(p.id)
  p.replyCount = (p.replyCount || 0) + 1
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该帖子？')
  await deleteForumPost(id)
  ElMessage.success('删除成功')
  posts.value = await listForumPosts({ courseId: route.params.id })
}
</script>

<style scoped>
.post-item {
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 0;
}
.post-item:last-child { border-bottom: none; }
.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.post-user-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 14px; flex-shrink: 0;
}
.post-meta { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.post-title { font-weight: 600; font-size: 15px; color: #1e293b; }
.post-info { font-size: 12px; color: #94a3b8; }
.post-content { font-size: 14px; color: #475569; line-height: 1.6; margin: 8px 0 8px 48px; }
.reply-section {
  margin: 8px 0 0 48px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}
.reply-item {
  padding: 8px 0;
  border-bottom: 1px solid #e2e8f0;
}
.reply-item:last-child { border-bottom: none; }
.reply-meta { font-size: 12px; color: #64748b; }
.reply-content { margin-top: 4px; font-size: 14px; color: #1e293b; }
.reply-input {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
</style>
