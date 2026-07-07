<template>
  <div class="dashboard-container">
    <!-- 骨架屏加载 -->
    <template v-if="loading">
      <div class="welcome-area">
        <el-skeleton animated>
          <template #template>
            <div style="display:flex;align-items:center;gap:16px">
              <el-skeleton-item variant="circle" style="width:52px;height:52px" />
              <div>
                <el-skeleton-item variant="h3" style="width:150px;height:22px" />
                <el-skeleton-item variant="text" style="width:190px;height:14px;margin-top:8px" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
      <div class="cards">
        <div v-for="i in 5" :key="i" class="metric-card">
          <el-skeleton animated>
            <template #template>
              <div style="display:flex;justify-content:space-between;align-items:flex-start">
                <el-skeleton-item variant="text" style="width:60%;height:14px" />
                <el-skeleton-item variant="circle" style="width:42px;height:42px" />
              </div>
              <el-skeleton-item variant="h1" style="width:45%;height:32px;margin-top:16px" />
            </template>
          </el-skeleton>
        </div>
      </div>
      <div class="charts">
        <el-card v-for="i in 2" :key="i" class="chart-card" :body-style="{ padding: '20px' }">
          <el-skeleton :rows="8" animated />
        </el-card>
        <el-card class="chart-card chart-card-wide" :body-style="{ padding: '20px' }">
          <el-skeleton :rows="6" animated />
        </el-card>
      </div>
    </template>

    <!-- 正式内容 -->
    <template v-else>
      <!-- 欢迎区域 -->
      <div class="welcome-area fade-up">
        <div class="welcome-avatar">
          <el-icon :size="24" color="#fff">
            <UserFilled />
          </el-icon>
        </div>
        <div>
          <h2 class="welcome-title">{{ greeting }}</h2>
          <p class="welcome-date">{{ todayStr }}</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="cards">
        <div v-for="(card, idx) in cards" :key="card.label" class="metric-card fade-up"
          :style="{ animationDelay: 0.1 + idx * 0.07 + 's' }">
          <div class="metric-top">
            <span class="metric-label">{{ card.label }}</span>
            <div class="metric-icon">
              <el-icon :size="20">
                <component :is="card.icon" />
              </el-icon>
            </div>
          </div>
          <strong class="metric-value">{{ formatNum(card.value) }}</strong>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="charts">
        <el-card class="chart-card fade-up" style="animation-delay:0.5s" :body-style="{ padding: 0 }">
          <div class="chart-title">近 7 日入住趋势</div>
          <div ref="trendRef" class="chart-box"></div>
        </el-card>
        <el-card class="chart-card fade-up" style="animation-delay:0.58s" :body-style="{ padding: 0 }">
          <div class="chart-title">床位使用率</div>
          <div ref="bedRef" class="chart-box"></div>
        </el-card>
        <el-card class="chart-card chart-card-wide fade-up" style="animation-delay:0.66s" :body-style="{ padding: 0 }">
          <div class="chart-title">护理结果统计</div>
          <div ref="nursingRef" class="chart-box"></div>
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts/core'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([LineChart, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])
import { UserFilled, HomeFilled, Grid, FirstAidKit, ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const userStore = useUserStore()

const stats = ref({})
const trendRef = ref()
const bedRef = ref()
const nursingRef = ref()
const chartInstances = ref([])
const loading = ref(true)

// 欢迎语（根据时段 + 用户姓名）
const greeting = computed(() => {
  const name = userStore.user?.name || '用户'
  const h = new Date().getHours()
  if (h < 6) return `夜深了，${name}`
  if (h < 12) return `早上好，${name}`
  if (h < 14) return `中午好，${name}`
  if (h < 18) return `下午好，${name}`
  return `晚上好，${name}`
})

// 今日日期
const todayStr = computed(() => {
  const d = new Date()
  const week = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()} 年 ${d.getMonth() + 1} 月 ${d.getDate()} 日 星期${week[d.getDay()]}`
})

// 数字千分位
const formatNum = (n) => (n ?? 0).toLocaleString()

// 统计卡片配置
const cards = computed(() => [
  { label: '总客户数', value: stats.value.totalCustomers ?? 0, icon: UserFilled },
  { label: '在住人数', value: stats.value.checkedInCustomers ?? 0, icon: HomeFilled },
  { label: '空闲床位', value: stats.value.freeBeds ?? 0, icon: Grid },
  { label: '护理记录', value: stats.value.nursingRecords ?? 0, icon: FirstAidKit },
  { label: '服务订单', value: stats.value.servicePurchases ?? 0, icon: ShoppingCart }
])

// 公共 tooltip 样式
const tipStyle = {
  backgroundColor: 'rgba(255,255,255,0.96)',
  borderColor: '#e8f0ed',
  borderWidth: 1,
  padding: [10, 14],
  textStyle: { color: '#244b42', fontSize: 13 },
  extraCssText: 'border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,0.06);'
}

let renderTimer = null

onMounted(async () => {
  try {
    stats.value = await request.get('/dashboard/stats')
  } catch (e) {
    ElMessage.error('仪表盘数据加载失败')
  } finally {
    loading.value = false
  }

  await nextTick()
  renderTimer = setTimeout(() => {
    renderTimer = null
    renderCharts()
  }, 100)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (renderTimer) { clearTimeout(renderTimer); renderTimer = null }
  window.removeEventListener('resize', handleResize)
  chartInstances.value.forEach(c => c.dispose())
})

// 渲染图表
const renderCharts = () => {
  if (!trendRef.value || !bedRef.value || !nursingRef.value) return
  chartInstances.value.forEach(c => c.dispose())
  chartInstances.value = []

  // ── 入住趋势折线图 ──
  const trendChart = echarts.init(trendRef.value)
  const trendData = stats.value.checkInTrend || []
  trendChart.setOption({
    tooltip: { trigger: 'axis', ...tipStyle },
    grid: { left: 48, right: 24, bottom: 28, top: 16 },
    xAxis: {
      type: 'category',
      data: trendData.map(i => i.date),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#888', fontSize: 12, margin: 12 }
    },
    yAxis: {
      type: 'value',
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: '#888', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      type: 'line',
      smooth: 0.4,
      symbol: 'circle',
      symbolSize: 8,
      showSymbol: trendData.length <= 7,
      itemStyle: { color: '#fff', borderColor: '#2f6f62', borderWidth: 2.5 },
      lineStyle: { width: 3, color: '#2f6f62' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(47,111,98,0.22)' },
          { offset: 1, color: 'rgba(47,111,98,0.02)' }
        ])
      },
      data: trendData.map(i => i.count),
      animationDuration: 1200,
      animationEasing: 'cubicOut'
    }]
  })
  if (trendData.length === 0) {
    trendChart.showLoading({
      text: '暂无数据', color: '#2f6f62', textColor: '#888',
      maskColor: 'rgba(255,255,255,0.85)',
      fontSize: 14, showSpinner: false
    })
  }
  chartInstances.value.push(trendChart)

  // ── 床位使用率饼图 ──
  const bedChart = echarts.init(bedRef.value)
  const bedData = Object.entries(stats.value.bedStats || {}).map(([name, value]) => ({ name, value }))
  bedChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}：{c} 个（{d}%）', ...tipStyle },
    legend: {
      bottom: 8, left: 'center',
      itemWidth: 10, itemHeight: 10, itemGap: 20,
      textStyle: { color: '#888', fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['42%', '70%'],
      center: ['50%', '46%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#244b42' },
        itemStyle: { shadowBlur: 12, shadowColor: 'rgba(0,0,0,0.08)' }
      },
      data: bedData,
      color: ['#2f6f62', '#5aad9d', '#8cc7bb', '#c5e5dd', '#e8f4f0'],
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDuration: 1000
    }]
  })
  if (bedData.length === 0) {
    bedChart.showLoading({
      text: '暂无数据', color: '#2f6f62', textColor: '#888',
      maskColor: 'rgba(255,255,255,0.85)',
      fontSize: 14, showSpinner: false
    })
  }
  chartInstances.value.push(bedChart)

  // ── 护理结果柱状图 ──
  const nursingChart = echarts.init(nursingRef.value)
  const nursingData = stats.value.nursingStats || []
  nursingChart.setOption({
    tooltip: { trigger: 'axis', ...tipStyle },
    grid: { left: 48, right: 24, bottom: 28, top: 16 },
    xAxis: {
      type: 'category',
      data: nursingData.map(i => i.name),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#888', fontSize: 12, margin: 12 }
    },
    yAxis: {
      type: 'value',
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: '#888', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      type: 'bar',
      data: nursingData.map(i => i.value),
      barWidth: '35%',
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#2f6f62' },
          { offset: 1, color: '#68a090' }
        ])
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#275d52' },
            { offset: 1, color: '#2f6f62' }
          ])
        }
      },
      animationDelay: (idx) => idx * 120,
      animationDuration: 800,
      animationEasing: 'cubicOut'
    }]
  })
  if (nursingData.length === 0) {
    nursingChart.showLoading({
      text: '暂无数据', color: '#2f6f62', textColor: '#888',
      maskColor: 'rgba(255,255,255,0.85)',
      fontSize: 14, showSpinner: false
    })
  }
  chartInstances.value.push(nursingChart)
}

const handleResize = () => {
  chartInstances.value.forEach(c => c.resize())
}
</script>

<style scoped>
/* ===== 容器 ===== */
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px;
  min-height: 100%;
  box-sizing: border-box;
}

/* ===== 欢迎区 ===== */
.welcome-area {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 4px 0;
}

.welcome-avatar {
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

.welcome-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #244b42;
  line-height: 1.4;
}

.welcome-date {
  margin: 2px 0 0;
  font-size: 14px;
  color: #8aaba2;
}

/* ===== 统计卡片 ===== */
.cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.metric-card {
  background: #fff;
  border-radius: 22px;
  padding: 22px 24px;
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.3s ease;
  box-shadow: 0 2px 12px rgba(47, 111, 98, 0.06);
  cursor: default;
}

.metric-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(47, 111, 98, 0.1);
}

.metric-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.metric-label {
  font-size: 14px;
  color: #8aaba2;
  font-weight: 400;
}

.metric-icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: rgba(47, 111, 98, 0.07);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2f6f62;
  transition: background 0.3s ease;
}

.metric-card:hover .metric-icon {
  background: rgba(47, 111, 98, 0.13);
}

.metric-value {
  display: block;
  margin-top: 16px;
  font-size: 32px;
  line-height: 1;
  color: #244b42;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

/* ===== 图表区域 ===== */
.charts {
  flex: 1;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
  min-height: 0;
}

.chart-card {
  border: none;
  border-radius: 22px;
  box-shadow: 0 2px 12px rgba(47, 111, 98, 0.06);
  overflow: hidden;
}

.chart-card-wide {
  grid-column: 1 / -1;
}

.chart-title {
  padding: 20px 24px 0;
  font-size: 15px;
  font-weight: 600;
  color: #244b42;
}

.chart-box {
  width: 100%;
  min-height: 240px;
  padding: 8px 12px 16px;
  box-sizing: border-box;
}

/* ===== 入场动画 ===== */
.fade-up {
  opacity: 0;
  transform: translateY(16px);
  animation: fadeUp 0.55s cubic-bezier(0.22, 1, 0.36, 1) forwards;
}

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts {
    grid-template-columns: 1fr;
  }
}
</style>
