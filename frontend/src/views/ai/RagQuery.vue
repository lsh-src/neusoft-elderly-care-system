<template>
  <div class="rag-container ai-page">
    <!-- 标题区 -->
    <div class="page-header fade-up">
      <div class="header-icon">
        <el-icon :size="24" color="#fff"><Search /></el-icon>
      </div>
      <div>
        <h2>RAG 检索问答</h2>
        <p>基于知识库的检索增强生成，精准回答专业问题</p>
      </div>
    </div>

    <!-- 查询表单 -->
    <el-card class="ai-card fade-up" style="animation-delay:0.1s" :body-style="{ padding: '24px' }">
      <el-form :model="form" label-position="top" @submit.prevent="handleQuery">
        <el-form-item label="您的问题">
          <el-input
            v-model="form.query"
            type="textarea"
            :rows="3"
            placeholder="请输入您想咨询的养老护理问题…"
            :disabled="loading"
          />
        </el-form-item>
        <el-form-item label="检索数量 (Top-K)">
          <el-slider v-model="form.topK" :min="1" :max="10" :step="1" show-stops :marks="{ 1: '1', 5: '5', 10: '10' }" />
        </el-form-item>
        <el-button type="primary" :icon="Search" :loading="loading" @click="handleQuery" :disabled="!form.query.trim()">
          检索问答
        </el-button>
      </el-form>
    </el-card>

    <!-- 加载中提示 -->
    <el-card v-if="loading" class="ai-card result-card fade-up" :body-style="{ padding: '24px' }">
      <div style="display:flex;align-items:center;gap:12px;color:#2f6f62">
        <el-icon class="is-loading" :size="20"><Loading /></el-icon>
        <span>正在检索知识库并生成回答…</span>
      </div>
    </el-card>

    <!-- 结果展示 -->
    <template v-if="result">
      <el-card class="ai-card result-card fade-up" :body-style="{ padding: '24px' }">
        <template #header>
          <div class="result-header">
            <el-icon :size="18" color="#2f6f62"><Document /></el-icon>
            <span>检索结果</span>
            <el-tag type="success" size="small" effect="plain">Top-{{ form.topK }}</el-tag>
          </div>
        </template>
        <div class="result-content" v-html="formatResult(result)"></div>
      </el-card>
    </template>

    <!-- 空状态 -->
    <el-empty v-if="!result && !loading" description="输入问题后点击检索问答" :image-size="120" />
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { Search, Document, Loading } from '@element-plus/icons-vue'
import { ragQuery } from '../../api/ai'
import { ElMessage } from 'element-plus'

const form = reactive({ query: '', topK: 3 })
const result = ref('')
const loading = ref(false)
const destroyed = ref(false)

const escapeHtml = (str) => str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
const formatResult = (text) => {
  return escapeHtml(text)
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/(\d+)\.\s/g, '<span class="num-tag">$1.</span> ')
}

const handleQuery = async () => {
  if (!form.query.trim() || loading.value) return

  loading.value = true
  result.value = ''

  try {
    const res = await ragQuery({ query: form.query, topK: form.topK })
    if (destroyed.value) return
    result.value = typeof res === 'string' ? res : (res?.data || res || '暂无结果')
  } catch (e) {
    if (!destroyed.value) ElMessage.error('检索失败，请稍后重试')
  } finally {
    if (!destroyed.value) loading.value = false
  }
}

onUnmounted(() => { destroyed.value = true })
</script>

<style scoped>
.rag-container {
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

:deep(.el-slider__marks-text) {
  font-size: 12px;
  color: #8aaba2;
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

:deep(.num-tag) {
  display: inline-block;
  background: rgba(47, 111, 98, 0.1);
  color: #2f6f62;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 6px;
  font-size: 12px;
  margin-right: 4px;
}
</style>
