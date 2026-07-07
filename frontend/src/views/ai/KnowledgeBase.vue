<template>
  <div class="kb-container ai-page">
    <!-- 标题区 -->
    <div class="page-header fade-up">
      <div class="header-icon">
        <el-icon :size="24" color="#fff"><Collection /></el-icon>
      </div>
      <div>
        <h2>知识库管理</h2>
        <p>上传文档到知识库，支持 RAG 检索增强生成</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row fade-up" style="animation-delay:0.1s">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon :size="20"><Document /></el-icon>
        </div>
        <div>
          <strong class="stat-value">{{ stats.totalDocuments ?? '—' }}</strong>
          <span class="stat-label">知识文档数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon :size="20"><Coin /></el-icon>
        </div>
        <div>
          <strong class="stat-value stat-text">{{ stats.storeType || '—' }}</strong>
          <span class="stat-label">存储类型</span>
        </div>
      </div>
    </div>

    <!-- 上传表单 -->
    <el-card class="ai-card fade-up" style="animation-delay:0.2s" :body-style="{ padding: '24px' }">
      <template #header>
        <div class="card-title">
          <el-icon :size="16" color="#2f6f62"><Upload /></el-icon>
          <span>上传知识文档</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleUpload">
        <el-form-item label="文档标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文档标题" maxlength="100" show-word-limit :disabled="uploading" />
        </el-form-item>
        <el-form-item label="文档分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" :disabled="uploading" style="width:100%">
            <el-option label="健康" value="health" />
            <el-option label="服务" value="service" />
            <el-option label="政策" value="policy" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="文档内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            maxlength="50000"
            show-word-limit
            placeholder="请粘贴或输入文档内容…"
            :disabled="uploading"
          />
        </el-form-item>
        <el-button type="primary" :icon="Upload" :loading="uploading" @click="handleUpload">
          上传并分块向量化
        </el-button>
      </el-form>
    </el-card>

    <!-- 上传结果 -->
    <el-card v-if="uploadResult" class="ai-card result-card fade-up" :body-style="{ padding: '20px 24px' }">
      <div class="result-row">
        <el-icon :size="20" color="#2f6f62"><CircleCheck /></el-icon>
        <span>上传成功！已将文档分为 <strong>{{ uploadResult.chunkCount }}</strong> 个片段并完成向量化。</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Collection, Document, Coin, Upload, CircleCheck } from '@element-plus/icons-vue'
import { knowledgeStats, knowledgeUpload } from '../../api/ai'
import { ElMessage } from 'element-plus'

const stats = ref({})
const formRef = ref(null)
const uploading = ref(false)
const uploadResult = ref(null)
const destroyed = ref(false)

const form = reactive({ title: '', content: '', category: 'health' })

const rules = {
  title: [{ required: true, message: '请输入文档标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文档内容', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const loadStats = async () => {
  try {
    const res = await knowledgeStats()
    if (destroyed.value) return
    // withMock 已解包，res 就是 data 层
    stats.value = res || {}
  } catch (e) {
    console.warn('加载统计失败', e)
  }
}

const handleUpload = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch { return }
  if (uploading.value) return
  uploading.value = true
  uploadResult.value = null
  try {
    const res = await knowledgeUpload({ ...form })
    if (destroyed.value) return
    const data = res || {}
    uploadResult.value = { chunkCount: data.chunks ?? data.chunkCount ?? 0 }
    ElMessage.success('文档上传成功')
    form.title = ''
    form.content = ''
    form.category = 'health'
    loadStats()
  } catch (e) {
    if (!destroyed.value) ElMessage.error('上传失败，请稍后重试')
  } finally {
    if (!destroyed.value) uploading.value = false
  }
}

onMounted(() => { loadStats() })
onUnmounted(() => { destroyed.value = true })
</script>

<style scoped>
.kb-container {
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

/* ===== 统计卡片 ===== */
.stats-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 22px;
  padding: 22px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 18px 45px rgba(55, 82, 72, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(47, 111, 98, 0.1);
}

.stat-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: rgba(47, 111, 98, 0.07);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2f6f62;
  flex-shrink: 0;
}

.stat-value {
  display: block;
  font-size: 28px;
  color: #244b42;
  font-weight: 700;
  line-height: 1.2;
}

.stat-text {
  font-size: 15px;
  font-weight: 600;
}

.stat-label {
  display: block;
  font-size: 13px;
  color: #8aaba2;
  margin-top: 4px;
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
.result-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #3a5a50;
}

.result-row strong {
  color: #2f6f62;
  font-size: 18px;
}
</style>
