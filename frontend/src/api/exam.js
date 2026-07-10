import request from './request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/submission/upload', method: 'post', data: formData })
}

export function listAssignments(params) {
  return request({ url: '/assignment/list', method: 'get', params })
}

export function getAssignment(id) {
  return request({ url: `/assignment/${id}`, method: 'get' })
}

export function addAssignment(data) {
  return request({ url: '/assignment', method: 'post', data })
}

export function deleteAssignment(id) {
  return request({ url: `/assignment/${id}`, method: 'delete' })
}

export function listSubmissions(params) {
  return request({ url: '/submission/list', method: 'get', params })
}

export function getSubmission(id) {
  return request({ url: `/submission/${id}`, method: 'get' })
}

export function addSubmission(data) {
  return request({ url: '/submission', method: 'post', data })
}

export function gradeSubmission(submissionId, score, comment) {
  return request({ url: '/submission/grade', method: 'put', params: { submissionId, score, comment } })
}

export function assignmentPage(params) {
  return request({ url: '/assignment/page', method: 'get', params })
}

export function submissionPage(params) {
  return request({ url: '/submission/page', method: 'get', params })
}

export function autoComment(submissionId, score) {
  return request({ url: '/submission/auto-comment', method: 'get', params: { submissionId, score } })
}

export function grantResubmit(assignmentId, studentId, deadline) {
  return request({ url: '/assignment/grant-resubmit', method: 'post', params: { assignmentId, studentId, deadline } })
}

export function listResubmitOpportunities(studentId) {
  return request({ url: '/assignment/resubmit-opportunities', method: 'get', params: { studentId } })
}
