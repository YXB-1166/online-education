import request from './request'

export function getGradeStats(courseId) {
  return request({ url: `/grade/stats/${courseId}`, method: 'get' })
}

export function getGradeTrend(courseId, studentId) {
  return request({ url: '/grade/trend', method: 'get', params: { courseId, studentId } })
}

export function exportGrades(courseId, courseName) {
  return request({ url: `/grade/export/${courseId}`, method: 'get', params: { courseName }, responseType: 'blob' })
}
