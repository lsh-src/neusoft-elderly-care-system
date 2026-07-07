<template>
  <el-card class="page-card profile-card fade-up">
    <!-- 头部区域 -->
    <div class="profile-header">
      <!-- 用户头像 -->
      <div class="profile-avatar">
        {{ userStore.user?.name?.slice(0, 1) }}
      </div>
      <!-- 用户信息 -->
      <div class="profile-info">
        <h2>{{ userStore.user?.name }}</h2>
        <!-- 角色标签 -->
        <p class="role-tag">{{ roleName }}</p>
      </div>
      <!-- 用户ID -->
      <div class="profile-id">
        ID: {{ userStore.user?.id || '-' }}
      </div>
    </div>

    <!-- 信息卡片 -->
    <div class="info-grid">
      <div class="info-card">
        <div class="info-icon phone-icon">
          <el-icon :size="20">
            <Iphone />
          </el-icon>
        </div>
        <div>
          <span class="info-label">手机号</span>
          <span class="info-value">{{ userStore.user?.phone || '-' }}</span>
        </div>
      </div>

      <div class="info-card">
        <div class="info-icon gender-icon">
          <el-icon :size="20">
            <User />
          </el-icon>
        </div>
        <div>
          <span class="info-label">性别</span>
          <span class="info-value">{{ userStore.user?.gender || '-' }}</span>
        </div>
      </div>

      <div class="info-card">
        <div class="info-icon age-icon">
          <el-icon :size="20">
            <Calendar />
          </el-icon>
        </div>
        <div>
          <span class="info-label">年龄</span>
          <span class="info-value">{{ userStore.user?.age != null ? userStore.user.age + ' 岁' : '-' }}</span>
        </div>
      </div>

      <div class="info-card">
        <div class="info-icon time-icon">
          <el-icon :size="20">
            <Clock />
          </el-icon>
        </div>
        <div>
          <span class="info-label">注册时间</span>
          <span class="info-value">{{ userStore.user?.createTime || '-' }}</span>
        </div>
      </div>
    </div>

    <!-- 详细信息表格 -->
    <el-descriptions :column="2" border class="profile-descriptions"
      :label-style="{ width: '130px', fontWeight: 600, background: '#f5faf8', color: '#244b42' }">
      <el-descriptions-item label="用户ID">{{ userStore.user?.id || '-' }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ userStore.user?.name || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ userStore.user?.phone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ userStore.user?.age ?? '-' }} 岁</el-descriptions-item>
      <el-descriptions-item label="性别">{{ userStore.user?.gender || '-' }}</el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag :type="roleTagType" size="small" round>{{ roleName }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="注册时间" :span="2">{{ userStore.user?.createTime || '-' }}</el-descriptions-item>
    </el-descriptions>

    <!-- 修改密码按钮 -->
    <div style="text-align: center; margin-top: 24px;">
      <el-button type="primary" @click="pwdDialogVisible = true">修改密码</el-button>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px" :close-on-click-modal="false">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pwdForm.password" type="password" show-password placeholder="请输入新密码（至少6位）" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="submitPwd">确认修改</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useUserStore } from '../stores/user'
import { changePassword } from '../api/auth'
import { Iphone, User, Calendar, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const roleName = computed(() => ({
  ROLE_ADMIN: '系统管理员',
  ROLE_MANAGER: '健康管家',
  ROLE_NURSE: '护士',
  ROLE_USER: '入住老人'
}[userStore.role] || '未知角色'))

const roleTagType = computed(() => ({
  ROLE_ADMIN: 'danger',
  ROLE_MANAGER: 'success',
  ROLE_NURSE: 'warning',
  ROLE_USER: 'info'
}[userStore.role] || ''))

// 修改密码
const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref()
const pwdForm = reactive({ password: '', confirmPassword: '' })
const pwdRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.password) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const submitPwd = async () => {
  if (!pwdFormRef.value) return
  try { await pwdFormRef.value.validate() } catch { return }
  pwdLoading.value = true
  try {
    await changePassword({ password: pwdForm.password })
    ElMessage.success('密码修改成功，请重新登录')
    pwdDialogVisible.value = false
    pwdForm.password = ''
    pwdForm.confirmPassword = ''
    userStore.logout()
    window.location.href = '/login'
  } catch (err) {
    // 错误已在拦截器中处理
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.page-card {
  border: 0;
  border-radius: 22px;
  padding: 24px;
}

.profile-card {
  max-width: 860px;
  margin: 0 auto;
}

/* 头部 */
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e8f0ed;
}

.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  color: #fff;
  display: grid;
  place-items: center;
  font-size: 30px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 6px 20px rgba(47, 111, 98, 0.25);
}

.profile-info h2 {
  margin: 0 0 6px;
  font-size: 26px;
  color: #244b42;
}

.role-tag {
  display: inline-block;
  padding: 3px 14px;
  background: #e8f5f0;
  color: #2f6f62;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  margin: 0;
}

.profile-id {
  margin-left: auto;
  font-size: 13px;
  color: #a0b5ae;
  background: #f5faf8;
  padding: 4px 14px;
  border-radius: 8px;
  font-family: 'DM Mono', monospace;
}

/* 信息卡片网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}

.info-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 16px;
  background: #f8fbf9;
  border-radius: 14px;
  border: 1px solid #e8f0ed;
  transition: all 0.2s ease;
}

.info-card:hover {
  border-color: #c8ddd5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.info-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.phone-icon {
  background: rgba(47, 111, 98, 0.1);
  color: #2f6f62;
}

.gender-icon {
  background: rgba(242, 201, 109, 0.15);
  color: #c4982b;
}

.age-icon {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.time-icon {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.info-label {
  display: block;
  font-size: 12px;
  color: #8aaa9e;
  margin-bottom: 2px;
}

.info-value {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #244b42;
}

/* 描述列表 */
.profile-descriptions {
  border-radius: 14px;
  overflow: hidden;
}

:deep(.el-descriptions--border .el-descriptions__cell) {
  border: 1px solid #e8f0ed;
  padding: 14px 16px;
}

:deep(.el-descriptions__content) {
  color: #3a3a3a;
  font-size: 14px;
}
</style>
