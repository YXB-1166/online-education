import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    component: () => import('../layouts/LoginLayout.vue'),
    children: [
      { path: '', name: 'Login', component: () => import('../views/login/Login.vue') }
    ]
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/courses',
    children: [
      { path: 'courses', name: 'CourseList', component: () => import('../views/student/CourseList.vue'), meta: { title: '课程广场' } },
      { path: 'my-courses', name: 'MyCourses', component: () => import('../views/student/MyCourses.vue'), meta: { title: '我的课程' } },
      { path: 'courses/:id', name: 'CourseDetail', component: () => import('../views/student/CourseDetail.vue'), meta: { title: '课程详情' } },
      { path: 'courses/:cid/assignments/:aid/submit', name: 'SubmitAssignment', component: () => import('../views/student/SubmitAssignment.vue'), meta: { title: '提交作业' } },
      { path: 'my-grades', name: 'MyGrades', component: () => import('../views/student/MyGrades.vue'), meta: { title: '我的成绩' } },
      { path: 'courses/:id/forum', name: 'Forum', component: () => import('../views/student/Forum.vue'), meta: { title: '课程论坛' } },
      { path: 'learning-progress', name: 'LearningProgress', component: () => import('../views/student/LearningProgress.vue'), meta: { title: '学习进度' } },
      { path: 'teacher/courses', name: 'TeacherCourses', component: () => import('../views/teacher/MyCourses.vue'), meta: { title: '我的课程' } },
      { path: 'teacher/courses/create', name: 'CourseCreate', component: () => import('../views/teacher/CourseCreate.vue'), meta: { title: '创建课程' } },
      { path: 'teacher/courses/:id/edit', name: 'CourseEdit', component: () => import('../views/teacher/CourseEdit.vue'), meta: { title: '编辑课程' } },
      { path: 'teacher/courses/:id/assignments/create', name: 'AssignmentCreate', component: () => import('../views/teacher/AssignmentCreate.vue'), meta: { title: '发布作业' } },
      { path: 'teacher/courses/:cid/assignments/:aid/grade', name: 'GradeSubmission', component: () => import('../views/teacher/GradeSubmission.vue'), meta: { title: '批改作业' } },
      { path: 'teacher/courses/:id/assignments', name: 'AssignmentList', component: () => import('../views/teacher/AssignmentList.vue'), meta: { title: '作业管理' } },
      { path: 'teacher/courses/:id/forum', name: 'TeacherForum', component: () => import('../views/teacher/ForumManage.vue'), meta: { title: '论坛管理' } },
      { path: 'teacher/pending-approvals', name: 'PendingApprovals', component: () => import('../views/teacher/PendingApprovals.vue'), meta: { title: '选课审核' } },
      { path: 'teacher/courses/:id/students', name: 'StudentList', component: () => import('../views/teacher/StudentList.vue'), meta: { title: '学生名单' } },
      { path: 'admin/users', name: 'UserManage', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'admin/courses', name: 'AdminCourses', component: () => import('../views/admin/CourseManage.vue'), meta: { title: '课程管理' } },
      { path: 'admin/dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '数据概览' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta?.title ? to.meta.title + ' - 在线教育系统' : '在线教育辅助教学系统'
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (to.name !== 'Login' && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
