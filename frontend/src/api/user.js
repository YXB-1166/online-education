import request from './request'

export function login(username, password) {
  return request({ url: '/user/login', method: 'post', data: { username, password } })
}

export function register(data) {
  return request({ url: '/user/register', method: 'post', data })
}

export function listUsers(params) {
  return request({ url: '/user/list', method: 'get', params })
}

export function getUser(id) {
  return request({ url: `/user/${id}`, method: 'get' })
}

export function addUser(data) {
  return request({ url: '/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/user', method: 'put', data })
}

export function updateProfile(data) {
  return request({ url: '/user/profile', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/user/${id}`, method: 'delete' })
}

export function userPage(params) {
  return request({ url: '/user/page', method: 'get', params })
}
