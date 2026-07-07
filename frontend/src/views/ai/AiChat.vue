<template>
  <div class="ai-chat-container ai-page">
    <!-- 顶部标题区 -->
    <div class="chat-header fade-up">
      <div class="header-icon">
        <el-icon :size="24" color="#fff"><ChatDotRound /></el-icon>
      </div>
      <div style="flex:1">
        <h2>AI 智能对话</h2>
        <p>与 AI 助手交流养老护理相关问题</p>
      </div>
      <el-button v-if="messages.length > 0" :icon="Delete" text type="danger" size="small" @click="clearChat">清空对话</el-button>
    </div>

    <!-- 对话区域 -->
    <el-card class="chat-card ai-card fade-up" style="animation-delay:0.1s" :body-style="{ padding: 0, display: 'flex', flexDirection: 'column', height: '100%' }">
      <!-- 消息列表 -->
      <div ref="messageListRef" class="message-list">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-msg">
          <div class="welcome-icon">
            <el-icon :size="36" color="#2f6f62"><ChatDotRound /></el-icon>
          </div>
          <h3>您好，我是东软颐养中心 AI 助手</h3>
          <p>我可以回答养老护理、健康管理、服务方案等方面的问题。</p>
          <div class="quick-asks">
            <el-button v-for="q in quickQuestions" :key="q" type="primary" plain round size="small" @click="sendMessage(q)">
              {{ q }}
            </el-button>
          </div>
        </div>

        <!-- 消息气泡 -->
        <div v-for="(msg, idx) in messages" :key="idx" :class="['msg-row', msg.role]">
          <div v-if="msg.role === 'assistant'" class="msg-avatar ai-avatar">
            <el-icon :size="18" color="#fff"><ChatDotRound /></el-icon>
          </div>
          <div class="msg-bubble">
            <div class="msg-text" v-html="formatMessage(msg.content)"></div>
            <span class="msg-time">{{ msg.time }}</span>
          </div>
          <div v-if="msg.role === 'user'" class="msg-avatar user-avatar">
            {{ userName?.slice(0, 1) || '我' }}
          </div>
        </div>

        <!-- 加载中 -->
        <div v-if="sending" class="msg-row assistant">
          <div class="msg-avatar ai-avatar">
            <el-icon :size="18" color="#fff"><ChatDotRound /></el-icon>
          </div>
          <div class="msg-bubble typing-bubble">
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
          </div>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="input-area">
        <el-input
          v-model="inputText"
          placeholder="请输入您的问题…"
          :rows="1"
          resize="none"
          @keydown.enter.exact.prevent="sendMessage()"
          :disabled="sending"
          class="chat-input"
        />
        <el-button type="primary" :icon="Promotion" circle :disabled="!inputText.trim() || sending" @click="sendMessage()" class="send-btn" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted, computed } from 'vue'
import { ChatDotRound, Promotion, Delete } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { aiChat } from '../../api/ai'
import { ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const userName = computed(() => userStore.user?.name)

const STORAGE_KEY = computed(() => `ai_chat_history_${userStore.user?.phone || 'guest'}`)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const messageListRef = ref(null)
const destroyed = ref(false)
const conversationId = ref(null)

// 保存对话到 localStorage
const saveHistory = () => {
  try {
    localStorage.setItem(STORAGE_KEY.value, JSON.stringify({
      messages: messages.value,
      conversationId: conversationId.value
    }))
  } catch { /* storage 满了就静默失败 */ }
}

// 从 localStorage 恢复对话
const loadHistory = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY.value)
    if (!raw) return
    const data = JSON.parse(raw)
    if (Array.isArray(data.messages) && data.messages.length > 0) {
      messages.value = data.messages
      conversationId.value = data.conversationId || null
    }
  } catch { /* 数据损坏就忽略 */ }
}

// 清空对话
const clearChat = async () => {
  try {
    await ElMessageBox.confirm('确定清空所有对话记录？', '提示', { type: 'warning' })
    messages.value = []
    conversationId.value = null
    localStorage.removeItem(STORAGE_KEY.value)
  } catch { /* 取消 */ }
}

const quickQuestions = [
  '老人日常护理注意事项有哪些？',
  '如何预防老人跌倒？',
  '半自理老人的饮食建议'
]

const escapeHtml = (str) => str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
const formatMessage = (text) => {
  return escapeHtml(text).replace(/\n/g, '<br>').replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
}

const getTime = () => {
  return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const sendMessage = async (text) => {
  const content = (text || inputText.value).trim()
  if (!content || sending.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content, time: getTime() })
  inputText.value = ''
  saveHistory()
  scrollToBottom()

  sending.value = true

  try {
    const res = await aiChat({ message: content, conversationId: conversationId.value })
    if (destroyed.value) return

    const fullText = typeof res === 'string' ? res : (res?.data || res || '抱歉，暂时无法回答。')

    // 推入真实消息，开始逐字动画（期间 sending 保持 true 防止重复发送）
    const idx = messages.value.length
    messages.value.push({ role: 'assistant', content: '', time: '' })

    // 逐字显示 — 必须通过数组下标修改才能触发 Vue 响应式
    for (let i = 0; i < fullText.length; i++) {
      if (destroyed.value) return
      messages.value[idx].content = fullText.slice(0, i + 1)
      if (i % 3 === 0) {
        await new Promise(r => setTimeout(r, 15))
        scrollToBottom()
      }
    }
    messages.value[idx].time = getTime()
    saveHistory()

    // 保存 conversationId 实现多轮对话
    if (!conversationId.value) {
      conversationId.value = 'conv_' + Date.now()
    }
  } catch (e) {
    sending.value = false
    if (!destroyed.value) {
      messages.value.push({ role: 'assistant', content: '抱歉，发生了错误，请稍后重试。', time: getTime() })
      saveHistory()
    }
  } finally {
    if (!destroyed.value) sending.value = false
    scrollToBottom()
  }
}

onMounted(() => {
  loadHistory()
  scrollToBottom()
})

onUnmounted(() => {
  destroyed.value = true
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px;
  height: calc(100vh - 140px);
  box-sizing: border-box;
}

/* ===== 标题区 ===== */
.chat-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 4px 0;
  flex-shrink: 0;
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

.chat-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #244b42;
}

.chat-header p {
  margin: 2px 0 0;
  font-size: 14px;
  color: #8aaba2;
}

/* ===== 对话卡片 ===== */
.chat-card {
  flex: 1;
  border: none;
  border-radius: 22px;
  box-shadow: 0 2px 12px rgba(47, 111, 98, 0.06);
  overflow: hidden;
  min-height: 0;
}

/* ===== 消息列表 ===== */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-list::-webkit-scrollbar {
  width: 4px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #d0e0db;
  border-radius: 4px;
}

/* ===== 欢迎消息 ===== */
.welcome-msg {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  text-align: center;
  gap: 12px;
  padding: 40px 20px;
}

.welcome-icon {
  width: 72px;
  height: 72px;
  border-radius: 22px;
  background: rgba(47, 111, 98, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-msg h3 {
  margin: 0;
  font-size: 18px;
  color: #244b42;
}

.welcome-msg p {
  margin: 0;
  color: #8aaba2;
  font-size: 14px;
}

.quick-asks {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  margin-top: 8px;
}

.quick-asks .el-button {
  border-color: #c5e5dd;
  color: #2f6f62;
  font-size: 13px;
}

.quick-asks .el-button:hover {
  background: rgba(47, 111, 98, 0.06);
}

/* ===== 消息行 ===== */
.msg-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  max-width: 80%;
}

.msg-row.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.msg-row.assistant {
  align-self: flex-start;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 700;
}

.ai-avatar {
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  box-shadow: 0 2px 8px rgba(47, 111, 98, 0.2);
}

.user-avatar {
  background: linear-gradient(135deg, #d9a441, #e8c06d);
  color: #fff;
  box-shadow: 0 2px 8px rgba(217, 164, 65, 0.2);
}

.msg-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.7;
  color: #244b42;
  position: relative;
  word-break: break-word;
}

.msg-row.assistant .msg-bubble {
  background: #fff;
  border: 1px solid #e8f0ed;
  border-top-left-radius: 4px;
}

.msg-row.user .msg-bubble {
  background: linear-gradient(135deg, #2f6f62, #3a8577);
  color: #fff;
  border-top-right-radius: 4px;
}

.msg-time {
  display: block;
  font-size: 11px;
  color: #b0c4bc;
  margin-top: 6px;
  text-align: right;
}

.msg-row.user .msg-time {
  color: rgba(255, 255, 255, 0.6);
}

/* ===== 打字动画 ===== */
.typing-bubble {
  display: flex;
  gap: 6px;
  align-items: center;
  padding: 16px 20px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #b0c4bc;
  animation: typingBounce 1.4s infinite;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typingBounce {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}

/* ===== 输入区 ===== */
.input-area {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e8f0ed;
  background: #fafcfb;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
}

:deep(.chat-input .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #d0e0db inset;
  padding: 8px 16px;
  font-size: 14px;
}

:deep(.chat-input .el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px #2f6f62 inset;
}

.send-btn {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  border: none;
  box-shadow: 0 2px 8px rgba(47, 111, 98, 0.25);
}

.send-btn:hover {
  background: linear-gradient(135deg, #275d52, #3a8577);
}
</style>
