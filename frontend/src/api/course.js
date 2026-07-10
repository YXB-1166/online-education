import request from './request'

export function listCourses(params) {
  return request({ url: '/course/list', method: 'get', params })
}

export function getCourse(id) {
  return request({ url: `/course/${id}`, method: 'get' })
}

export function addCourse(data) {
  return request({ url: '/course', method: 'post', data })
}

export function updateCourse(data) {
  return request({ url: '/course', method: 'put', data })
}

export function deleteCourse(id) {
  return request({ url: `/course/${id}`, method: 'delete' })
}

export function selectCourse(studentId, courseId) {
  return request({ url: '/course/select', method: 'post', params: { studentId, courseId } })
}

export function dropCourse(studentId, courseId) {
  return request({ url: '/course/drop', method: 'post', params: { studentId, courseId } })
}

export function listForumPosts(params) {
  return request({ url: '/forum/list', method: 'get', params })
}

export function getForumPost(id) {
  return request({ url: `/forum/${id}`, method: 'get' })
}

export function addForumPost(data) {
  return request({ url: '/forum', method: 'post', data })
}

export function deleteForumPost(id) {
  return request({ url: `/forum/${id}`, method: 'delete' })
}

export function getForumReplies(postId) {
  return request({ url: `/forum/${postId}/replies`, method: 'get' })
}

export function addForumReply(data) {
  return request({ url: '/forum/reply', method: 'post', data })
}

export function coursePage(params) {
  return request({ url: '/course/page', method: 'get', params })
}

export function forumPostPage(params) {
  return request({ url: '/forum/page', method: 'get', params })
}

export function myCourses(studentId) {
  return request({ url: '/course/my', method: 'get', params: { studentId } })
}

export function listPendingSelections(teacherId) {
  return request({ url: '/course/selection/pending', method: 'get', params: { teacherId } })
}

export function approveSelection(id) {
  return request({ url: `/course/selection/approve/${id}`, method: 'put' })
}

export function rejectSelection(id) {
  return request({ url: `/course/selection/reject/${id}`, method: 'put' })
}

export function listSelectionsByCourse(courseId, teacherId) {
  return request({ url: '/course/selection/list', method: 'get', params: { courseId, teacherId } })
}

export function askAssistant(data) {
  return request({ url: '/assistant/ask', method: 'post', data })
}

export function getChapters(courseId) {
  return request({ url: `/course/${courseId}/chapters`, method: 'get' })
}

export function generateChapters(courseId) {
  return request({ url: `/course/${courseId}/chapters/generate`, method: 'post' })
}

export function myTeaching(teacherId) {
  return request({ url: '/course/my-teaching', method: 'get', params: { teacherId } })
}

export function listPendingCourses() {
  return request({ url: '/course/pending', method: 'get' })
}

export function approveCourse(id) {
  return request({ url: `/course/approve/${id}`, method: 'put' })
}

export function rejectCourse(id) {
  return request({ url: `/course/reject/${id}`, method: 'put' })
}

export function startCourse(id, homeworkRatio, examRatio, examTime) {
  return request({ url: `/course/${id}/start`, method: 'put', params: { homeworkRatio, examRatio, examTime } })
}

export function endCourse(id) {
  return request({ url: `/course/${id}/end`, method: 'put' })
}

export function setExamTime(id, examTime) {
  return request({ url: `/course/${id}/exam-time`, method: 'put', params: { examTime } })
}

export function getNotifications(studentId) {
  return request({ url: '/course/notifications', method: 'get', params: { studentId } })
}

export function getUnreadCount(studentId) {
  return request({ url: '/course/notifications/unread-count', method: 'get', params: { studentId } })
}

export function markNotificationRead(notificationId, studentId) {
  return request({ url: `/course/notification/${notificationId}/read`, method: 'put', params: { studentId } })
}

export function getCourseNotifications(courseId) {
  return request({ url: `/course/${courseId}/notifications`, method: 'get' })
}
