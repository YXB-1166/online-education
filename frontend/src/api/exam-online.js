import request from './request'

export function examPage(params) {
  return request({ url: '/exam/page', method: 'get', params })
}

export function getExam(id) {
  return request({ url: `/exam/${id}`, method: 'get' })
}

export function getExamSilent(id) {
  return request({ url: `/exam/${id}`, method: 'get', silent: true })
}

export function addExam(data) {
  return request({ url: '/exam', method: 'post', data })
}

export function updateExam(data) {
  return request({ url: '/exam', method: 'put', data })
}

export function deleteExam(id) {
  return request({ url: `/exam/${id}`, method: 'delete' })
}

export function publishExam(id) {
  return request({ url: `/exam/${id}/publish`, method: 'post' })
}

export function getExamQuestions(examId) {
  return request({ url: `/exam/${examId}/questions`, method: 'get' })
}

export function saveExamQuestions(examId, questions) {
  return request({ url: `/exam/${examId}/questions`, method: 'post', data: questions })
}

export function autoGenerateQuestions(examId, choiceCount, judgeCount) {
  return request({ url: `/exam/${examId}/auto-generate`, method: 'post', params: { choiceCount, judgeCount } })
}

export function submitExam(examId, studentId, answers) {
  return request({ url: `/exam/${examId}/submit`, method: 'post', params: { studentId }, data: answers })
}

export function getExamRecord(examId, studentId) {
  return request({ url: `/exam/${examId}/record`, method: 'get', params: { studentId } })
}

export function getExamRecords(examId) {
  return request({ url: `/exam/${examId}/records`, method: 'get' })
}

export function getMyExamRecords(studentId) {
  return request({ url: '/exam/my-records', method: 'get', params: { studentId } })
}
