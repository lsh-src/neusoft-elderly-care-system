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
      <h1>加入东软<br />颐养中心</h1>
      <p class="brand-desc">注册成为我们的客户，享受专业、贴心的一站式养老照护服务。</p>
    </div>

    <el-card class="auth-card fade-up">
      <h2>客户注册</h2>
      <p class="auth-sub">请填写以下信息完成注册</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" clearable size="large" :prefix-icon="Iphone" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" clearable size="large"
            :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" clearable size="large" :prefix-icon="User" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="年龄" prop="age" class="form-row-item">
            <el-input-number v-model="form.age" :min="1" :max="120" controls-position="right" size="large"
              style="width: 100%" />
          </el-form-item>
          <el-form-item label="性别" prop="gender" class="form-row-item">
            <el-select v-model="form.gender" placeholder="请选择" size="large" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </div>
        <el-button type="primary" size="large" class="submit-btn" @click="submit" :loading="loading">
          立即注册
        </el-button>
      </el-form>

      <router-link to="/login" class="auth-link">
        已有账号？去登录
      </router-link>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, Lock, User } from '@element-plus/icons-vue'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ phone: '', password: '', name: '', age: 60, gender: '' })
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const submit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch { return }
  loading.value = true
  try {
    await register({
      phone: form.phone.trim(),
      password: form.password.trim(),
      name: form.name.trim(),
      age: form.age,
      gender: form.gender
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '注册失败')
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-row-item {
  margin-bottom: 0;
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

:deep(.el-select__wrapper) {
  box-shadow: none;
  border: 1.5px solid #d5e5e0;
  border-radius: 12px !important;
  transition: all 0.2s ease;
}

:deep(.el-select__wrapper:hover) {
  border-color: #8abfb0;
}

:deep(.el-select__wrapper.is-focus) {
  border-color: #2f6f62;
  box-shadow: 0 0 0 3px rgba(47, 111, 98, 0.1) !important;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: 12px !important;
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

.auth-link {
  display: block;
  text-align: center;
  margin-top: 20px;
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
