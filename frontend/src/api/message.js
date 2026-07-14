import request from './request'

export function sendMessage(data) {
  return request({ url: '/message/send', method: 'post', data })
}

export function listConversations(userId) {
  return request({ url: '/message/conversations', method: 'get', params: { userId } })
}

export function getConversation(userId, otherId) {
  return request({ url: '/message/conversation', method: 'get', params: { userId, otherId } })
}

export function getMessageUnreadCount(userId) {
  return request({ url: '/message/unread-count', method: 'get', params: { userId } })
}
