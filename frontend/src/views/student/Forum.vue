<template>
  <div>
    <div class="page-title">课程论坛</div>
    <div style="margin-bottom:20px">
      <el-button type="primary" @click="showPostDialog = true" round>
        <el-icon><Edit /></el-icon>发布帖子
      </el-button>
    </div>

    <el-card v-for="p in posts" :key="p.id" style="margin-bottom:16px">
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:8px">
          <el-avatar :size="36" style="background:linear-gradient(135deg,#667eea,#764ba2)">
            {{ userMap[p.userId]?.charAt(0) || '?' }}
          </el-avatar>
          <div>
            <div style="font-weight:600">{{ p.title }}</div>
            <div style="font-size:12px;color:#94a3b8">
              {{ userMap[p.userId] || '未知' }} · {{ p.createTime }}
            </div>
          </div>
          <div style="margin-left:auto;display:flex;align-items:center;gap:12px">
            <span style="font-size:13px;color:#94a3b8">{{ p.replyCount }} 条回复</span>
            <el-button size="small" :type="p.liked ? 'danger' : 'default'" plain circle @click="handleLike(p)" style="font-size:16px;line-height:1">
              {{ p.liked ? '\u2665' : '\u2661' }}
            </el-button>
            <span v-if="p.likeCount" style="font-size:12px;color:#94a3b8;margin-left:-8px">{{ p.likeCount }}</span>
            <el-button size="small" plain @click="toggleReplies(p)" round>
              {{ p.showReplies ? '收起回复' : '回复' }}
            </el-button>
          </div>
        </div>
      <p style="color:#475569;margin:0 0 12px 48px;white-space:pre-wrap">{{ p.content }}</p>

      <div v-if="p.showReplies" style="margin-left:48px;padding:12px;background:#f8fafc;border-radius:8px">
        <div v-for="r in p.replies" :key="r.id" style="padding:8px 0;border-bottom:1px solid #e2e8f0">
          <div style="font-size:13px;color:#64748b">
            {{ replyUserMap[r.userId] || '未知' }} · {{ r.createTime }}
          </div>
          <div style="margin-top:4px;color:#1e293b">{{ r.content }}</div>
        </div>
        <div v-if="!p.replies" style="color:#94a3b8;padding:8px">暂无回复</div>
        <div style="margin-top:8px;display:flex;gap:8px">
          <el-input v-model="p.replyContent" placeholder="输入回复..." size="small" />
          <el-button size="small" type="primary" @click="handleReply(p)">回复</el-button>
        </div>
      </div>
    </el-card>

    <div v-if="total > pageSize" style="margin-top:24px;text-align:center">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchData"
      />
    </div>
    <el-empty v-if="!loading && posts.length === 0" description="暂无帖子" />

    <el-dialog v-model="showPostDialog" title="发布帖子" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="postForm" :rules="postRules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="postForm.content" type="textarea" :rows="5" placeholder="请输入帖子内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPostDialog = false" round>取消</el-button>
        <el-button type="primary" @click="handleAddPost" round>发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { forumPostPage, addForumPost, getForumReplies, addForumReply, toggleLike } from '../../api/course'
import { listUsers } from '../../api/user'

const route = useRoute()
const store = useUserStore()
const posts = ref([])
const loading = ref(false)
const showPostDialog = ref(false)
const formRef = ref(null)
const postForm = reactive({ title: '', content: '' })
const postRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}
const userMap = ref({})
const replyUserMap = ref({})
const total = ref(0)
const pageNum = ref(1)
const pageSize = 5

onMounted(async () => {
  const users = await listUsers()
  users.forEach(u => { userMap.value[u.id] = u.realName; replyUserMap.value[u.id] = u.realName })
  fetchData()
})

async function fetchData() {
  loading.value = true
  const res = await forumPostPage({ courseId: route.params.id, pageNum: pageNum.value, pageSize, userId: store.user.id })
  posts.value = res.list.map(p => ({ ...p, showReplies: false, replies: null, replyContent: '' }))
  total.value = res.total
  loading.value = false
}

async function handleLike(p) {
  try {
    const liked = await toggleLike(p.id, store.user.id)
    p.liked = liked
    p.likeCount = (p.likeCount || 0) + (liked ? 1 : -1)
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function toggleReplies(p) {
  p.showReplies = !p.showReplies
  if (p.showReplies && !p.replies) {
    p.replies = await getForumReplies(p.id)
    p.replyCount = p.replies.length
  }
}

async function handleReply(p) {
  if (!p.replyContent.trim()) return
  await addForumReply({ postId: p.id, userId: store.user.id, content: p.replyContent })
  ElMessage.success('回复成功')
  p.replyContent = ''
  p.replies = await getForumReplies(p.id)
  p.replyCount = p.replies.length
}

async function handleAddPost() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  await addForumPost({ courseId: Number(route.params.id), userId: store.user.id, title: postForm.title, content: postForm.content })
  ElMessage.success('发布成功')
  showPostDialog.value = false
  pageNum.value = 1
  fetchData()
}
</script>
