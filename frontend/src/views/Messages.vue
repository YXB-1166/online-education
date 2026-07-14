<template>
  <div>
    <div class="page-title">站内信</div>
    <div class="msg-layout">
      <div class="sidebar">
        <div class="sidebar-header">
          <span>联系人</span>
          <el-button size="small" type="primary" round @click="showNewDialog = true">新消息</el-button>
        </div>
        <div v-if="loading" style="text-align:center;padding:20px;color:#94a3b8">加载中...</div>
        <div v-else-if="conversations.length === 0" style="text-align:center;padding:20px;color:#94a3b8">暂无消息</div>
        <div v-else class="contact-list">
          <div v-for="c in conversations" :key="c.id" class="contact-item" :class="{ active: currentOther === otherUserId(c) }" @click="openConversation(c)">
            <div class="contact-avatar">{{ contactName(c).charAt(0) }}</div>
            <div class="contact-info">
              <div class="contact-name">{{ contactName(c) }}</div>
              <div class="contact-preview">{{ c.content }}</div>
            </div>
            <div class="contact-meta">
              <div class="contact-time">{{ timeStr(c.createTime) }}</div>
              <div v-if="c.isRead === 0 && c.receiverId === store.user.id" class="unread-dot" />
            </div>
          </div>
        </div>
      </div>
      <div class="chat-area">
        <div v-if="!currentOther" class="chat-placeholder">选择左侧联系人开始对话</div>
        <template v-else>
          <div class="chat-header">{{ currentName }}</div>
          <div ref="chatBody" class="chat-body">
            <div v-for="m in messages" :key="m.id" class="msg-bubble" :class="{ mine: m.senderId === store.user.id }">
              <div class="msg-sender">{{ m.senderId === store.user.id ? '我' : m.senderName }}</div>
              <div class="msg-content">{{ m.content }}</div>
              <div class="msg-time">{{ timeStr(m.createTime) }}</div>
            </div>
          </div>
          <div class="chat-input-bar">
            <el-input v-model="inputText" type="textarea" :rows="2" placeholder="输入消息..." @keydown.ctrl.enter="doSend" />
            <el-button type="primary" @click="doSend" style="margin-left:8px;align-self:flex-end">发送</el-button>
          </div>
        </template>
      </div>
    </div>

    <el-dialog v-model="showNewDialog" title="发送新消息" width="400px">
      <el-form>
        <el-form-item label="收件人">
          <el-select v-model="newReceiverId" filterable placeholder="选择收件人" style="width:100%">
            <el-option v-for="u in users" :key="u.id" :label="`${u.realName} (${u.role === 1 ? '学生' : '教师'})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="newContent" type="textarea" :rows="3" placeholder="请输入消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewDialog = false">取消</el-button>
        <el-button type="primary" @click="sendNew">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useUserStore } from '../stores/user'
import { sendMessage, listConversations, getConversation, getMessageUnreadCount } from '../api/message'
import { listUsers } from '../api/user'

const store = useUserStore()
const loading = ref(true)
const conversations = ref([])
const messages = ref([])
const currentOther = ref(null)
const currentName = ref('')
const inputText = ref('')
const chatBody = ref(null)
const showNewDialog = ref(false)
const newReceiverId = ref(null)
const newContent = ref('')
const users = ref([])

function otherUserId(c) {
  return c.senderId === store.user.id ? c.receiverId : c.senderId
}

function contactName(c) {
  return c.senderId === store.user.id ? c.receiverName : c.senderName
}

function timeStr(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = n => String(n).padStart(2, '0')
  if (d.toDateString() === now.toDateString()) return `${pad(d.getHours())}:${pad(d.getMinutes())}`
  return `${d.getMonth() + 1}/${d.getDate()}`
}

async function loadConversations() {
  conversations.value = await listConversations(store.user.id) || []
}

async function openConversation(c) {
  const otherId = otherUserId(c)
  currentOther.value = otherId
  currentName.value = contactName(c)
  messages.value = await getConversation(store.user.id, otherId) || []
  await loadConversations()
  await nextTick()
  if (chatBody.value) chatBody.value.scrollTop = chatBody.value.scrollHeight
}

async function doSend() {
  if (!inputText.value.trim() || !currentOther.value) return
  await sendMessage({ senderId: store.user.id, receiverId: currentOther.value, content: inputText.value.trim() })
  inputText.value = ''
  messages.value = await getConversation(store.user.id, currentOther.value) || []
  await loadConversations()
  await nextTick()
  if (chatBody.value) chatBody.value.scrollTop = chatBody.value.scrollHeight
}

async function sendNew() {
  if (!newReceiverId.value || !newContent.value.trim()) return
  await sendMessage({ senderId: store.user.id, receiverId: newReceiverId.value, content: newContent.value.trim() })
  showNewDialog.value = false
  newReceiverId.value = null
  newContent.value = ''
  await loadConversations()
}

onMounted(async () => {
  await loadConversations()
  loading.value = false
  try {
    users.value = await listUsers() || []
  } catch (_) {}
})
</script>

<style scoped>
.msg-layout { display:flex; height:calc(100vh - 180px); border:1px solid #eef2f6; border-radius:12px; overflow:hidden; background:#fff }
.sidebar { width:300px; border-right:1px solid #eef2f6; display:flex; flex-direction:column; flex-shrink:0 }
.sidebar-header { display:flex; justify-content:space-between; align-items:center; padding:14px 16px; border-bottom:1px solid #eef2f6; font-weight:600 }
.contact-list { flex:1; overflow-y:auto }
.contact-item { display:flex; align-items:center; gap:10px; padding:12px 16px; cursor:pointer; border-bottom:1px solid #f8fafc }
.contact-item:hover { background:#f8fafc }
.contact-item.active { background:#eef2ff }
.contact-avatar { width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,#6366f1,#8b5cf6);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700;flex-shrink:0 }
.contact-info { flex:1;min-width:0 }
.contact-name { font-size:13px;font-weight:600;color:#1e293b }
.contact-preview { font-size:12px;color:#94a3b8;overflow:hidden;text-overflow:ellipsis;white-space:nowrap }
.contact-meta { display:flex;flex-direction:column;align-items:flex-end;gap:2px;flex-shrink:0 }
.contact-time { font-size:11px;color:#94a3b8 }
.unread-dot { width:8px;height:8px;border-radius:50%;background:#ef4444 }
.chat-area { flex:1;display:flex;flex-direction:column }
.chat-placeholder { flex:1;display:flex;align-items:center;justify-content:center;color:#94a3b8;font-size:14px }
.chat-header { padding:14px 20px;font-weight:600;border-bottom:1px solid #eef2f6 }
.chat-body { flex:1;overflow-y:auto;padding:16px 20px }
.msg-bubble { margin-bottom:12px;max-width:70% }
.msg-bubble.mine { margin-left:auto }
.msg-sender { font-size:11px;color:#94a3b8;margin-bottom:2px }
.msg-bubble.mine .msg-sender { text-align:right }
.msg-content { padding:8px 14px;border-radius:12px;background:#f1f5f9;font-size:13px;line-height:1.5;color:#1e293b }
.msg-bubble.mine .msg-content { background:#6366f1;color:#fff }
.msg-time { font-size:10px;color:#94a3b8;margin-top:2px }
.msg-bubble.mine .msg-time { text-align:right }
.chat-input-bar { display:flex;padding:12px 16px;border-top:1px solid #eef2f6 }
</style>
