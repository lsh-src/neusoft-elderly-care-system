<template>
  <el-card class="page-card fade-up">
    <div class="toolbar">
      <h3>膳食日历</h3>
      <el-button type="primary" @click="load" :loading="loading">
        刷新菜单
      </el-button>
    </div>

    <!-- 自定义日历头部 -->
    <div class="custom-calendar-header">
      <span class="calendar-title">
        {{ currentDate.getFullYear() }} 年 {{ currentDate.getMonth() + 1 }} 月
      </span>
      <div class="calendar-buttons">
        <el-button type="primary" @click="prevMonth">上一月</el-button>
        <el-button type="primary" @click="toToday">今天</el-button>
        <el-button type="primary" @click="nextMonth">下一月</el-button>
      </div>
    </div>

    <!-- 日历主体（数据加载完成后才渲染，避免 el-calendar 内部状态异常） -->
    <el-calendar v-if="calendarReady" v-model="currentDate" class="clean-calendar">
      <template #date-cell="{ data }">
        <div class="day-cell" :class="{
          'is-today': isToday(data.day),
          'is-other-month': data.type !== 'current-month',
          'is-weekend': isWeekend(data.day),
          'has-meal-data': hasMealDate(data.day)
        }" @click="openMealDetail(data.day, data.type)">
          <strong class="day-number">{{ data.day.slice(8).replace(/^0/, '') }}</strong>
          <template v-if="data.type === 'current-month'">
            <div v-if="hasMealDate(data.day)" class="day-badge">
              <span class="day-dot"></span>
              {{ mealMap[data.day]?.length }} 人定制
            </div>
            <div v-else class="day-default-hint">默认餐食</div>
          </template>
        </div>
      </template>
    </el-calendar>
    <el-skeleton v-else-if="loading" :rows="8" animated />
  </el-card>

  <!-- 膳食详情弹窗 -->
  <el-dialog v-model="detailVisible" width="760px" :close-on-click-modal="false" :show-close="false"
    class="meal-dialog">
    <!-- 自定义弹窗头部 -->
    <template #header>
      <div class="dialog-header">
        <div class="dialog-header-left">
          <div class="dialog-date-icon">
            <el-icon :size="22" color="#fff">
              <Calendar />
            </el-icon>
          </div>
          <div>
            <h3 class="dialog-date-text">{{ formatDialogTitle(selectedDate) }}</h3>
            <span class="dialog-count-text">
              共 {{ displayMeals[0]?.isDefault ? 0 : displayMeals.length }} 条定制记录
            </span>
          </div>
        </div>
        <el-icon class="dialog-close-icon" @click="detailVisible = false">
          <Close />
        </el-icon>
      </div>
    </template>

    <!-- 弹窗内容：唯一滚动区 -->
    <div class="detail-scroll-box">

      <!-- 默认膳食 -->
      <div v-if="isDefaultMeal" class="default-meal-banner fade-up">
        <div class="default-meal-icon">
          <el-icon :size="28" color="#b8944a">
            <Dish />
          </el-icon>
        </div>
        <div class="default-meal-body">
          <div class="default-meal-title">今日暂无定制膳食，以下为系统默认菜单</div>
          <div class="default-meal-grid">
            <div class="default-meal-item">
              <span class="meal-type-tag meal-type-breakfast">早</span>
              <span>{{ displayMeals[0].breakfast }}</span>
            </div>
            <div class="default-meal-item">
              <span class="meal-type-tag meal-type-lunch">午</span>
              <span>{{ displayMeals[0].lunch }}</span>
            </div>
            <div class="default-meal-item">
              <span class="meal-type-tag meal-type-dinner">晚</span>
              <span>{{ displayMeals[0].dinner }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 定制膳食卡片列表 -->
      <div v-else class="meal-card-list">
        <div v-for="(meal, idx) in displayMeals" :key="meal.id || idx" class="meal-card fade-up"
          :style="{ animationDelay: idx * 0.08 + 's' }">
          <!-- 卡片左侧色条 -->
          <div class="meal-card-accent"></div>

          <!-- 卡片内容 -->
          <div class="meal-card-body">
            <!-- 客户信息栏 -->
            <div class="meal-customer-row">
              <div class="meal-customer-avatar">
                {{ (meal.customerName || '?').charAt(0) }}
              </div>
              <div class="meal-customer-info">
                <span class="meal-customer-name">{{ meal.customerName || '未知客户' }}</span>
                <span class="meal-customer-no">{{ meal.mealNo }}</span>
              </div>
              <el-tag v-if="meal.specialNeed" type="warning" size="small" round effect="plain" class="meal-special-tag">
                {{ meal.specialNeed }}
              </el-tag>
            </div>

            <!-- 膳食内容 -->
            <div class="meal-rows">
              <div class="meal-row-item">
                <span class="meal-type-tag meal-type-breakfast">早</span>
                <span class="meal-row-text">{{ meal.breakfast || '未填写' }}</span>
              </div>
              <div class="meal-row-item">
                <span class="meal-type-tag meal-type-lunch">午</span>
                <span class="meal-row-text">{{ meal.lunch || '未填写' }}</span>
              </div>
              <div class="meal-row-item">
                <span class="meal-type-tag meal-type-dinner">晚</span>
                <span class="meal-row-text">{{ meal.dinner || '未填写' }}</span>
              </div>
            </div>

            <!-- 膳食图片 -->
            <div v-if="meal.mealImg" class="meal-img-section">
              <el-image :src="meal.mealImg" fit="cover" class="meal-img-thumb" preview-teleported />
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button type="primary" @click="detailVisible = false" class="dialog-close-btn">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, onErrorCaptured } from 'vue'
import { pageApi } from '../api/crud'
import { ElMessage } from 'element-plus'
import { Calendar, Close, Dish } from '@element-plus/icons-vue'

const loading = ref(false)
const meals = ref([])
const mealMap = ref({})
const currentDate = ref(new Date())
const detailVisible = ref(false)
const selectedDate = ref('')
const isFirstLoad = ref(true)
const calendarReady = ref(false)
const destroyed = ref(false)

const DEFAULT_MEAL = {
  isDefault: true,
  breakfast: '小米粥、水煮鸡蛋、小咸菜',
  lunch: '软米饭、清炒时蔬、嫩豆腐',
  dinner: '南瓜粥、白面馒头、凉拌黄瓜'
}

const WEEK_NAMES = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

// 格式化弹窗标题日期
const formatDialogTitle = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getMonth() + 1} 月 ${d.getDate()} 日 ${WEEK_NAMES[d.getDay()]}`
}

// 构建映射
const buildMealMap = () => {
  const map = {}
  meals.value.forEach(item => {
    const date = item.mealDate
    if (!map[date]) map[date] = []
    map[date].push(item)
  })
  mealMap.value = map
}

const getMealsByDate = (dateStr) => {
  if (!dateStr) return [DEFAULT_MEAL]
  return mealMap.value[dateStr] || [DEFAULT_MEAL]
}

const hasMealDate = (dateStr) => !!mealMap.value[dateStr]
const isDefaultMeal = computed(() => displayMeals.value.length === 1 && displayMeals.value[0].isDefault)
const displayMeals = computed(() => getMealsByDate(selectedDate.value))

const isToday = (dayStr) => dayStr === new Date().toISOString().split('T')[0]
const isWeekend = (dayStr) => {
  const day = new Date(dayStr).getDay()
  return day === 0 || day === 6
}

const openMealDetail = (dayStr, type) => {
  if (type !== 'current-month') {
    ElMessage.info('仅支持查看当月膳食')
    return
  }
  selectedDate.value = dayStr
  detailVisible.value = true
}

const prevMonth = () => {
  calendarReady.value = false
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
  nextTick(() => { calendarReady.value = true })
}
const nextMonth = () => {
  calendarReady.value = false
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
  nextTick(() => { calendarReady.value = true })
}
const toToday = () => {
  calendarReady.value = false
  currentDate.value = new Date()
  nextTick(() => { calendarReady.value = true })
}

const load = async () => {
  loading.value = true
  calendarReady.value = false
  try {
    const res = await pageApi('meals', { current: 1, size: 1000 })
    if (destroyed.value) return
    meals.value = res.records || []
    buildMealMap()
    if (!isFirstLoad.value) ElMessage.success('菜单刷新成功')
    isFirstLoad.value = false
  } catch (e) {
    if (!destroyed.value) ElMessage.error('菜单刷新失败')
  } finally {
    if (!destroyed.value) {
      loading.value = false
      calendarReady.value = true
    }
  }
}

onMounted(() => { load() })

onUnmounted(() => { destroyed.value = true })

// 捕获子组件（el-calendar）错误，防止白屏扩散
onErrorCaptured((err) => {
  console.error('[MealCalendar] 子组件错误:', err)
  calendarReady.value = false
  return false // 阻止错误向上传播
})
</script>

<style scoped>
.page-card {
  border: 0;
  border-radius: 22px;
  padding: 24px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #244b42;
}

/* ===== 日历头部 ===== */
.custom-calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 14px;
  border-bottom: 1px solid #e8f0ed;
  margin-bottom: 12px;
}

.calendar-title {
  font-size: 16px;
  font-weight: 600;
  color: #244b42;
}

.calendar-buttons {
  display: flex;
  gap: 8px;
}

/* ===== 隐藏日历默认头部 ===== */
:deep(.clean-calendar .el-calendar__header) {
  display: none !important;
}

:deep(.el-calendar) {
  --el-calendar-cell-height: 108px;
  border: 0;
}

:deep(.el-calendar__body) {
  padding: 0;
}

/* ===== 星期标题 ===== */
:deep(.clean-calendar .el-calendar-table thead th) {
  padding: 10px 0;
  background: #f5faf8;
  font-size: 0;
}

:deep(.clean-calendar .el-calendar-table thead th::before) {
  font-size: 14px;
  color: #6b8a7f;
  font-weight: 600;
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(1)::before) {
  content: "日";
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(2)::before) {
  content: "一";
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(3)::before) {
  content: "二";
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(4)::before) {
  content: "三";
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(5)::before) {
  content: "四";
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(6)::before) {
  content: "五";
}

:deep(.clean-calendar .el-calendar-table thead th:nth-child(7)::before) {
  content: "六";
}

:deep(.el-calendar-table td) {
  padding: 4px;
  border: 1px solid #e8f0ed;
  vertical-align: top;
  cursor: pointer;
  transition: background 0.2s;
}

:deep(.el-calendar-table td:hover) {
  background: #f8fbf9;
}

/* ===== 日期格子 ===== */
.day-cell {
  height: 100%;
  min-height: 86px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 8px 4px;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.day-cell:hover {
  background: rgba(47, 111, 98, 0.04);
}

.day-cell.is-other-month {
  opacity: 0.35;
  pointer-events: none;
}

.day-cell.is-today .day-number {
  background: #2f6f62;
  color: #fff;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.day-cell.is-weekend .day-number {
  color: #d94444;
}

.day-cell.is-today.is-weekend .day-number {
  color: #fff;
}

.day-number {
  font-size: 17px;
  font-weight: 600;
  color: #244b42;
  line-height: 30px;
}

.day-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #2f6f62;
  font-weight: 600;
  background: rgba(47, 111, 98, 0.08);
  padding: 2px 8px;
  border-radius: 10px;
}

.day-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #2f6f62;
  flex-shrink: 0;
}

.day-default-hint {
  font-size: 11px;
  color: #c0c0c0;
}

/* ===== 弹窗头部 ===== */
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.dialog-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.dialog-date-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(47, 111, 98, 0.25);
}

.dialog-date-text {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #244b42;
  line-height: 1.3;
}

.dialog-count-text {
  font-size: 13px;
  color: #8aaba2;
}

.dialog-close-icon {
  cursor: pointer;
  font-size: 20px;
  color: #8aaba2;
  transition: color 0.2s;
  border-radius: 8px;
  padding: 4px;
}

.dialog-close-icon:hover {
  color: #244b42;
  background: #f5faf8;
}

/* ===== 弹窗主体滚动区 ===== */
.detail-scroll-box {
  max-height: 60vh;
  overflow-y: auto;
  padding: 4px 2px 12px;
}

.detail-scroll-box::-webkit-scrollbar {
  width: 5px;
}

.detail-scroll-box::-webkit-scrollbar-thumb {
  background: #c8d8d3;
  border-radius: 3px;
}

.detail-scroll-box::-webkit-scrollbar-track {
  background: transparent;
}

/* ===== 默认膳食横幅 ===== */
.default-meal-banner {
  display: flex;
  gap: 18px;
  padding: 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, #fdf8ee 0%, #fef5e0 100%);
  border: 1px solid #f0deb8;
}

.default-meal-icon {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(184, 148, 74, 0.12);
}

.default-meal-body {
  flex: 1;
  min-width: 0;
}

.default-meal-title {
  font-size: 14px;
  font-weight: 600;
  color: #8a7340;
  margin-bottom: 14px;
}

.default-meal-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.default-meal-item {
  display: flex;
  align-items: baseline;
  gap: 10px;
  font-size: 14px;
  color: #5a5030;
  line-height: 1.5;
}

/* ===== 定制膳食卡片列表 ===== */
.meal-card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.meal-card {
  display: flex;
  border-radius: 16px;
  border: 1px solid #e8f0ed;
  overflow: hidden;
  background: #fff;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.meal-card:hover {
  box-shadow: 0 4px 16px rgba(47, 111, 98, 0.08);
  transform: translateY(-1px);
}

.meal-card-accent {
  width: 5px;
  flex-shrink: 0;
  background: linear-gradient(180deg, #2f6f62, #4a9e8f);
}

.meal-card-body {
  flex: 1;
  padding: 18px 20px;
  min-width: 0;
}

/* ===== 客户信息栏 ===== */
.meal-customer-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid #f0f5f3;
}

.meal-customer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  flex-shrink: 0;
}

.meal-customer-info {
  flex: 1;
  min-width: 0;
}

.meal-customer-name {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #244b42;
  line-height: 1.3;
}

.meal-customer-no {
  display: block;
  font-size: 12px;
  color: #a0b5ae;
  margin-top: 2px;
}

.meal-special-tag {
  flex-shrink: 0;
  border-color: #f0deb8 !important;
  color: #8a7340 !important;
  background: #fdf8ee !important;
}

/* ===== 膳食内容行 ===== */
.meal-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meal-row-item {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.meal-type-tag {
  width: 28px;
  height: 22px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
  line-height: 1;
}

.meal-type-breakfast {
  background: linear-gradient(135deg, #e8a040, #f0c060);
}

.meal-type-lunch {
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
}

.meal-type-dinner {
  background: linear-gradient(135deg, #6b8a7f, #8aaba2);
}

.meal-row-text {
  font-size: 14px;
  color: #3a5a50;
  line-height: 1.6;
  word-break: break-word;
}

/* ===== 膳食图片 ===== */
.meal-img-section {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid #f0f5f3;
}

.meal-img-thumb {
  width: 180px;
  height: 120px;
  border-radius: 12px;
  border: 1px solid #e8f0ed;
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.meal-img-thumb:hover {
  transform: scale(1.03);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

/* ===== 弹窗按钮 ===== */
.dialog-close-btn {
  min-width: 80px;
}

/* ===== 入场动画 ===== */
.fade-up {
  opacity: 0;
  transform: translateY(14px);
  animation: fadeUp 0.5s cubic-bezier(0.22, 1, 0.36, 1) forwards;
}

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 全局按钮 & 弹窗圆角 ===== */
:deep(.el-button--primary) {
  background: #2f6f62;
  border-color: #2f6f62;
  border-radius: 12px;
}

:deep(.el-button--primary:hover) {
  background: #275d52 !important;
  border-color: #275d52 !important;
}

:deep(.meal-dialog .el-dialog) {
  border-radius: 22px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

:deep(.meal-dialog .el-dialog__header) {
  padding: 24px 24px 16px;
  flex-shrink: 0;
}

:deep(.meal-dialog .el-dialog__body) {
  flex: 1;
  min-height: 0;
  padding: 0 24px !important;
  overflow: hidden;
}

:deep(.meal-dialog .el-dialog__footer) {
  padding: 12px 24px 20px;
  flex-shrink: 0;
}
</style>
