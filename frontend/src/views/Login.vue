<template>
  <div class="auth-shell">
    <div class="brand fade-up">
      <div class="brand-mark">
        <svg viewBox="0 0 128 128" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="login-bg" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#2f6f62" />
              <stop offset="100%" style="stop-color:#1a4a3e" />
            </linearGradient>
          </defs>
          <rect width="128" height="128" rx="28" fill="url(#login-bg)" />
          <path
            d="M64 95 C40 72, 28 58, 28 46 C28 35, 37 28, 46 28 C52 28, 58 32, 64 38 C70 32, 76 28, 82 28 C91 28, 100 35, 100 46 C100 58, 88 72, 64 95Z"
            fill="white" opacity="0.95" />
          <rect x="58" y="50" width="12" height="28" rx="3" fill="#2f6f62" />
          <rect x="50" y="58" width="28" height="12" rx="3" fill="#2f6f62" />
        </svg>
      </div>

      <p class="eyebrow">Neusoft Elderly Care</p>
      <h1>东软颐养中心<br />管理系统</h1>
      <p class="brand-desc">把入住、床位、护理、膳食与服务关注放进同一个温柔又可靠的工作台。</p>
    </div>

    <el-card class="auth-card fade-up">
      <h2>登录系统</h2>
      <p class="auth-sub">请输入您的账号信息</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="13800000000" clearable size="large" :prefix-icon="Iphone" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="123456" clearable size="large"
            :prefix-icon="Lock" @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" @click="submit" :loading="loading">
          登录
        </el-button>
      </el-form>

      <div class="hint">
        <div class="hint-title">演示账号</div>
        <div class="hint-row"><span>管理员</span><code>13800000000</code></div>
        <div class="hint-row"><span>健康管家</span><code>13800000001</code></div>
        <div class="hint-row"><span>护士</span><code>13900000100</code></div>
        <div class="hint-row"><span>入住老人</span><code>13800000002</code></div>
        <div class="hint-row"><span>密码</span><code>123456</code></div>
      </div>

      <router-link to="/register" class="auth-link">
        没有账号？注册普通客户
      </router-link>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ phone: '13800000000', password: '123456' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch { return }
  loading.value = true
  try {
    await userStore.login({ phone: form.phone.trim(), password: form.password.trim() })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (err) {
    // 错误已在 request.js 拦截器中统一处理，此处不再重复提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.1fr 440px;
  gap: 48px;
  align-items: center;
  padding: 64px 9vw;
  background:
    radial-gradient(ellipse at 10% 80%, rgba(47, 111, 98, 0.06), transparent 60%),
    radial-gradient(ellipse at 80% 20%, rgba(242, 201, 109, 0.08), transparent 50%),
    linear-gradient(135deg, #f8fbf9 0%, #eef7f2 100%);
}

.brand {
  max-width: 720px;
}

.brand-mark {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  flex-shrink: 0;
}

.brand-mark svg {
  width: 100%;
  height: 100%;
  display: block;
}

.eyebrow {
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: #2f6f62;
  font-weight: 700;
  font-size: 14px;
  margin: 0 0 12px;
}

h1 {
  font-size: 52px;
  line-height: 1.12;
  margin: 0 0 20px;
  color: #244b42;
}

.brand-desc {
  font-size: 18px;
  color: #6b8a7f;
  line-height: 1.7;
  margin: 0;
}

/* 表单卡片 */
.auth-card {
  border: 0;
  border-radius: 28px;
  padding: 44px 40px;
  box-shadow:
    0 24px 64px rgba(47, 111, 98, 0.12),
    0 4px 12px rgba(0, 0, 0, 0.04);
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(10px);
}

.auth-card h2 {
  margin: 0 0 4px;
  font-size: 24px;
  font-weight: 700;
  color: #244b42;
  text-align: center;
}

.auth-sub {
  text-align: center;
  color: #8aaa9e;
  font-size: 14px;
  margin: 0 0 28px;
}

:deep(.el-form-item__label) {
  color: #244b42;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  box-shadow: none;
  border: 1.5px solid #d5e5e0;
  border-radius: 12px !important;
  transition: all 0.2s ease;
  padding: 4px 12px;
}

:deep(.el-input__wrapper:hover) {
  border-color: #8abfb0;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #2f6f62;
  box-shadow: 0 0 0 3px rgba(47, 111, 98, 0.1) !important;
}

:deep(.el-input__prefix) {
  color: #a0b5ae;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  margin-top: 8px;
  background: #2f6f62;
  border-color: #2f6f62;
  letter-spacing: 0.08em;
  transition: all 0.25s ease;
}

.submit-btn:hover {
  background: #275d52 !important;
  border-color: #275d52 !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(47, 111, 98, 0.3);
}

.hint {
  margin: 24px 0;
  padding: 16px 18px;
  background: #f5faf8;
  border-radius: 14px;
  border: 1px solid #e8f0ed;
  font-size: 13px;
  color: #6b8a7f;
}

.hint-title {
  font-weight: 600;
  color: #244b42;
  margin-bottom: 10px;
  font-size: 13px;
}

.hint-row {
  display: flex;
  justify-content: space-between;
  padding: 3px 0;
}

.hint-row span {
  color: #6b8a7f;
}

.hint-row code {
  font-family: 'DM Mono', monospace;
  color: #2f6f62;
  font-weight: 500;
  background: rgba(47, 111, 98, 0.08);
  padding: 1px 8px;
  border-radius: 6px;
  font-size: 12px;
}

.auth-link {
  display: block;
  text-align: center;
  color: #2f6f62;
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  transition: opacity 0.2s ease;
}

.auth-link:hover {
  opacity: 0.7;
}
</style>
