import request from '../utils/request'

// ==================== Mock 数据（后端未启动或 LLM 不可用时兜底） ====================
const MOCK = {
  chat: (message) =>
    `您好！我是东软颐养中心的 AI 助手。\n\n您提到的"${message}"，我已记录。目前系统处于演示模式，实际部署后将接入大模型为您提供更精准的养老护理建议。\n\n如需帮助，请联系管理员。`,

  ragQuery: (query) =>
    `【RAG 检索结果】\n\n问题：${query}\n\n基于知识库检索，以下是参考回答：\n\n1. 东软颐养中心提供全方位的养老服务，包括生活照料、医疗护理、康复训练等。\n2. 护理等级分为自理、半自理、全护理三个级别，根据老人身体状况评估确定。\n3. 每位入住老人均建立健康档案，定期进行健康评估。\n\n（演示模式 — 实际回答将基于知识库文档生成）`,

  knowledgeUpload: () => ({
    title: '演示文档',
    chunks: 5,
    message: '文档上传成功（演示模式）'
  }),

  knowledgeStats: () => ({
    totalDocuments: 0,
    storeType: 'In-Memory SimpleVectorStore'
  }),

  healthAnalysis: (customerName) =>
    `【健康评估报告】\n\n老人姓名：${customerName}\n评估时间：${new Date().toLocaleString('zh-CN')}\n\n一、总体评估\n该老人身体状况良好，生命体征平稳，精神状态佳。\n\n二、主要指标\n• 血压：130/80 mmHg（正常偏高，建议关注）\n• 血糖：5.6 mmol/L（正常）\n• 心率：72 次/分（正常）\n• BMI：23.5（正常范围）\n\n三、建议\n1. 保持规律作息和适度运动\n2. 饮食注意低盐低脂\n3. 定期监测血压变化\n4. 建议每季度进行一次全面体检\n\n（演示模式 — 实际评估将基于真实健康数据生成）`,

  careRecommendation: (customerName) =>
    `【护理方案推荐】\n\n老人姓名：${customerName}\n生成时间：${new Date().toLocaleString('zh-CN')}\n\n一、护理等级建议\n根据当前状况，建议护理等级：半自理级\n\n二、日常护理要点\n1. 生活照料：协助洗浴、如厕提醒、衣物整理\n2. 饮食护理：三餐两点，注意软烂易消化\n3. 健康监测：每日测量血压、体温，记录出入量\n4. 康复训练：每日散步 30 分钟，手指操 15 分钟\n\n三、心理关怀\n• 每周安排 2 次社交活动\n• 鼓励与家人视频通话\n• 关注情绪变化，及时疏导\n\n四、注意事项\n• 防跌倒：夜间留灯，走廊安装扶手\n• 用药管理：按时提醒服药，记录用药反应\n\n（演示模式 — 实际方案将基于老人具体情况生成）`
}

// ==================== 带 mock 兜底的请求封装 ====================
async function withMock(apiCall, mockFn) {
  try {
    return await apiCall()
  } catch (e) {
    console.warn('[AI Mock] 后端请求失败，使用演示数据:', e.message)
    return typeof mockFn === 'function' ? mockFn() : mockFn
  }
}

// ==================== AI 对话 ====================
export const aiChat = (data) =>
  withMock(
    () => request.post('/ai/chat', data),
    () => MOCK.chat(data.message)
  )

// ==================== RAG 检索问答 ====================
export const ragQuery = (data) =>
  withMock(
    () => request.post('/ai/rag/query', data),
    () => MOCK.ragQuery(data.query)
  )

// ==================== 知识库上传 ====================
export const knowledgeUpload = (data) =>
  withMock(
    () => request.post('/ai/knowledge/upload', data),
    () => MOCK.knowledgeUpload()
  )

// ==================== 知识库统计 ====================
export const knowledgeStats = () =>
  withMock(
    () => request.get('/ai/knowledge/stats'),
    () => MOCK.knowledgeStats()
  )

// ==================== 健康评估 ====================
export const healthAnalysis = (data) =>
  withMock(
    () => request.post('/ai/analyze/health', data),
    () => MOCK.healthAnalysis(data.customerName)
  )

// ==================== 护理方案推荐 ====================
export const careRecommendation = (data) =>
  withMock(
    () => request.post('/ai/analyze/care', data),
    () => MOCK.careRecommendation(data.customerName)
  )
