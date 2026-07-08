<template>
  <div>
    <div class="page-title">我的课程</div>
    <el-card>
      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column prop="maxStudents" label="容量" width="80" />
        <el-table-column prop="enrolledCount" label="已选" width="80" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === '1'" type="success" size="small">即将开课</el-tag>
            <el-tag v-else-if="row.status === '2'" type="primary" size="small">授课中</el-tag>
            <el-tag v-else-if="row.status === '3'" size="small">已结课</el-tag>
            <el-tag v-else-if="row.status === '4'" type="danger" size="small">审核不通过</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="520">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.id + '/edit')" round>编辑</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.id + '/students')" round>学生名单</el-button>
            <el-button size="small" type="primary" @click="$router.push('/teacher/courses/' + row.id + '/assignments')" round>作业管理</el-button>
            <el-button size="small" @click="$router.push('/teacher/courses/' + row.id + '/forum')" round>论坛</el-button>
            <el-button size="small" type="warning" @click="$router.push('/teacher/pending-approvals')" round>审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { myTeaching } from '../../api/course'

const store = useUserStore()
const courses = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  courses.value = await myTeaching(store.user.id)
  loading.value = false
})
</script>
