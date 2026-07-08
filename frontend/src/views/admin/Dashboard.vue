<template>
  <div>
    <div class="page-title">数据概览</div>
    <el-row :gutter="24">
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card" style="background:linear-gradient(135deg,#eff6ff,#dbeafe)">
          <div class="icon-wrap" style="background:#3b82f6;color:#fff"><el-icon :size="28"><User /></el-icon></div>
          <div class="stat-value" style="color:#1d4ed8">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card" style="background:linear-gradient(135deg,#f0fdf4,#dcfce7)">
          <div class="icon-wrap" style="background:#22c55e;color:#fff"><el-icon :size="28"><Reading /></el-icon></div>
          <div class="stat-value" style="color:#16a34a">{{ stats.courseCount }}</div>
          <div class="stat-label">课程总数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card" style="background:linear-gradient(135deg,#fefce8,#fef9c3)">
          <div class="icon-wrap" style="background:#eab308;color:#fff"><el-icon :size="28"><Notebook /></el-icon></div>
          <div class="stat-value" style="color:#ca8a04">{{ stats.selectionCount }}</div>
          <div class="stat-label">选课总数</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listUsers } from '../../api/user'
import { listCourses } from '../../api/course'

const stats = ref({ userCount: 0, courseCount: 0, selectionCount: 0 })

onMounted(async () => {
  const [users, courses] = await Promise.all([listUsers(), listCourses()])
  stats.value.userCount = users.length
  stats.value.courseCount = courses.length
  stats.value.selectionCount = courses.reduce((s, c) => s + (c.enrolledCount || 0), 0)
})
</script>
