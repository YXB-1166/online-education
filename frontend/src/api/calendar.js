import request from './request'

export function getCalendarEvents(userId, role) {
  return request({ url: '/calendar/events', method: 'get', params: { userId, role } })
}
