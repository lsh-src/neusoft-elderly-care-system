<template>
  <div class="analysis-container ai-page">
    <!-- 标题区 -->
    <div class="page-header fade-up">
      <div class="header-icon">
        <el-icon :size="24" color="#fff"><FirstAidKit /></el-icon>
      </div>
      <div>
        <h2>健康评估</h2>
        <p>基于 AI 对老人进行健康评估分析</p>
      </div>
    </div>

    <!-- 评估表单 -->
    <el-card class="ai-card fade-up" style="animation-delay:0.1s" :body-style="{ padding: '24px' }">
      <template #header>
        <div class="card-title">
          <el-icon :size="16" color="#2f6f62"><EditPen /></el-icon>
          <span>填写评估信息</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleAnalyze">
        <el-form-item label="老人姓名" prop="customerName">
          <el-select v-model="form.customerName" placeholder="请选择老人" filterable :disabled="loading" style="width:100%">
            <el-option v-for="c in customerList" :key="c.id" :label="c.name" :value="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康信息" prop="healthInfo">
          <el-input
            v-model="form.healthInfo"
            type="textarea"
            :rows="6"
            placeholder="请描述老人的健康状况，如血压、血糖、既往病史、日常表现等…"
            :disabled="loading"
          />
        </el-form-item>
        <el-button type="primary" :icon="FirstAidKit" :loading="loading" @click="handleAnalyze" :disabled="!form.customerName.trim() || !form.healthInfo.trim()">
          开始健康评估
        </el-button>
      </el-form>
    </el-card>

    <!-- 加载中提示 -->
    <el-card v-if="loading" class="ai-card result-card fade-up" :body-style="{ padding: '24px' }">
      <div style="display:flex;align-items:center;gap:12px;color:#2f6f62">
        <el-icon class="is-loading" :size="20"><Loading /></el-icon>
        <span>AI 正在生成评估报告，请稍候…</span>
      </div>
    </el-card>

    <!-- 评估结果 -->
    <el-card v-if="result" class="ai-card result-card fade-up" :body-style="{ padding: '24px' }">
      <template #header>
        <div class="result-header">
          <el-icon :size="18" color="#2f6f62"><DataAnalysis /></el-icon>
          <span>评估报告</span>
          <el-tag type="success" size="small" effect="plain">AI 生成</el-tag>
        </div>
      </template>
      <div class="result-content" v-html="formatResult(result)"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { FirstAidKit, EditPen, DataAnalysis, Loading } from '@element-plus/icons-vue'
import { healthAnalysis } from '../../api/ai'
import { pageApi } from '../../api/crud'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const loading = ref(false)
const result = ref('')
const destroyed = ref(false)
const customerList = ref([])

const form = reactive({ customerName: '', healthInfo: '' })

const rules = {
  customerName: [{ required: true, message: '请选择老人', trigger: 'change' }],
  healthInfo: [{ required: true, message: '请输入健康信息', trigger: 'blur' }]
}

onMounted(async () => {
  const cacheKey = 'cache_customers'
  try {
    const cached = sessionStorage.getItem(cacheKey)
    if (cached) {
      customerList.value = JSON.parse(cached)
    } else {
      const res = await pageApi('customers', { current: 1, size: 500 })
      customerList.value = res?.records || []
      sessionStorage.setItem(cacheKey, JSON.stringify(customerList.value))
    }
  } catch {
    customerList.value = []
  }
})

const escapeHtml = (str) => str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
const formatResult = (text) => {
  return escapeHtml(text)
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/•\s/g, '<span class="bullet">•</span> ')
}

const handleAnalyze = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch { return }
  if (loading.value) return
  loading.value = true
  result.value = ''
  try {
    const res = await healthAnalysis({ ...form })
    if (destroyed.value) return
    result.value = typeof res === 'string' ? res : (res?.data || res || '暂无评估结果')
  } catch (e) {
    if (!destroyed.value) ElMessage.error('评估失败，请稍后重试')
  } finally {
    if (!destroyed.value) loading.value = false
  }
}

onUnmounted(() => { destroyed.value = true })
</script>

<style scoped>
.analysis-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px;
  min-height: 100%;
  box-sizing: border-box;
}

/* ===== 标题区 ===== */
.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 4px 0;
}

.header-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(47, 111, 98, 0.2);
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #244b42;
}

.page-header p {
  margin: 2px 0 0;
  font-size: 14px;
  color: #8aaba2;
}

/* ===== 卡片标题 ===== */
.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #244b42;
}

/* ===== 结果区 ===== */
.result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #244b42;
}

.result-content {
  font-size: 14px;
  line-height: 1.9;
  color: #3a5a50;
  padding: 8px 0;
}

:deep(.bullet) {
  color: #2f6f62;
  font-weight: 700;
  margin-right: 4px;
}
</style>
