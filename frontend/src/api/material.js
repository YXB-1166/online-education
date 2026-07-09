import request from './request'

export function listMaterials(courseId) {
  return request({ url: `/course/material/list/${courseId}`, method: 'get' })
}

export function listMaterialsWithStatus(courseId, studentId) {
  return request({ url: `/course/material/list-with-status/${courseId}`, method: 'get', params: { studentId } })
}

export function getMaterial(id) {
  return request({ url: `/course/material/${id}`, method: 'get' })
}

export function addMaterial(data) {
  return request({ url: '/course/material', method: 'post', data })
}

export function deleteMaterial(id) {
  return request({ url: `/course/material/${id}`, method: 'delete' })
}

export function startReadMaterial(materialId, studentId) {
  return request({ url: '/course/material/start-read', method: 'post', params: { materialId, studentId } })
}

export function completeReadMaterial(materialId, studentId) {
  return request({ url: '/course/material/complete-read', method: 'post', params: { materialId, studentId } })
}
