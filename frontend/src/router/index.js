import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

// 登录/注册/布局 — 首屏必需，同步导入
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'

// 业务页面 — 懒加载
const Dashboard = () => import('../views/Dashboard.vue')
const CrudPage = () => import('../views/CrudPage.vue')
const MealCalendar = () => import('../views/MealCalendar.vue')
const Profile = () => import('../views/Profile.vue')
const AiChat = () => import('../views/ai/AiChat.vue')
const RagQuery = () => import('../views/ai/RagQuery.vue')
const KnowledgeBase = () => import('../views/ai/KnowledgeBase.vue')
const HealthAnalysis = () => import('../views/ai/HealthAnalysis.vue')
const CareRecommendation = () => import('../views/ai/CareRecommendation.vue')

// ==========【最终菜单：用户管理 独立显示，无嵌套子项】==========
export const menuGroups = [
  // 首页
  {
    groupName: '',
    children: [
      { path: 'dashboard', title: '运营仪表盘', icon: 'DataAnalysis', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE'], type: 'dashboard' },
    ]
  },
  // 分组1：入住管理
  {
    groupName: '入住管理',
    icon: 'House',
    children: [
      { path: 'customers', title: '客户管理', resource: 'customers', icon: 'Avatar', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
      { path: 'beds', title: '床位管理', resource: 'beds', icon: 'House', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
    ]
  },
  // 分组2：出入登记
  {
    groupName: '出入登记',
    icon: 'Document',
    children: [
      { path: 'check-ins', title: '入住登记', resource: 'check-ins', icon: 'CirclePlus', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
      { path: 'check-outs', title: '退住登记', resource: 'check-outs', icon: 'Remove', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
      { path: 'outings', title: '外出登记', resource: 'outings', icon: 'Van', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
    ]
  },

  // 分组4：服务管理
  {
    groupName: '服务管理',
    icon: 'Star',
    children: [
      { path: 'service-relations', title: '服务对象', resource: 'service-relations', icon: 'Connection', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
      { path: 'care-services', title: '服务关注', resource: 'care-services', icon: 'Star', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'] },
      { path: 'service-purchases', title: '服务购买', resource: 'service-purchases', icon: 'ShoppingCart', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'] },
    ]
  },
  // 分组3：膳食管理
  {
    groupName: '膳食管理',
    icon: 'Food',
    children: [
      { path: 'meals', title: '膳食管理', resource: 'meals', icon: 'Food', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
      { path: 'meal-calendar', title: '膳食日历', resource: 'meals', icon: 'Calendar', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'], calendar: true },
    ]
  },
  // 分组5：护理模块
  {
    groupName: '护理管理',
    icon: 'Medal',
    children: [
      { path: 'nursing-levels', title: '护理级别', resource: 'nursing-levels', icon: 'Medal', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
      { path: 'nursing-items', title: '护理内容', resource: 'nursing-items', icon: 'FirstAidKit', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
      { path: 'nursing-records', title: '护理记录', resource: 'nursing-records', icon: 'DocumentChecked', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
      { path: 'nurse-areas', title: '负责区域', resource: 'nurse-areas', icon: 'Location', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
    ]
  },
  // 分组6：AI智能服务
  {
    groupName: 'AI智能服务',
    icon: 'ChatDotRound',
    children: [
      { path: 'ai-chat', title: 'AI对话', icon: 'ChatDotRound', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'], ai: 'AiChat' },
      { path: 'rag-query', title: 'RAG检索问答', icon: 'Search', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'], ai: 'RagQuery' },
      { path: 'knowledge-base', title: '知识库管理', icon: 'Collection', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'], ai: 'KnowledgeBase' },
      { path: 'health-analysis', title: '健康评估', icon: 'FirstAidKit', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'], ai: 'HealthAnalysis' },
      { path: 'care-recommendation', title: '护理方案推荐', icon: 'Memo', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'], ai: 'CareRecommendation' },
    ]
  },
  // ✅ 独立菜单项：用户管理（无分组、无子节点，直接显示）
  {
    groupName: '',
    children: [
      { path: 'users', title: '用户管理', resource: 'users', icon: 'UserFilled', roles: ['ROLE_ADMIN'] }
    ]
  }
]

const allModules = menuGroups.flatMap(group => group.children)

const aiComponentMap = { AiChat, RagQuery, KnowledgeBase, HealthAnalysis, CareRecommendation }

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: Layout,
    redirect: (to) => {
      const userStore = useUserStore()
      return userStore.role === 'ROLE_USER' ? '/customers' : '/dashboard'
    },
    children: [
      { path: 'profile', component: Profile, meta: { title: '个人信息' } },
      ...allModules.map((item) => ({
        path: item.path,
        component: item.ai ? aiComponentMap[item.ai] : (item.type === 'dashboard' ? Dashboard : (item.calendar ? MealCalendar : CrudPage)),
        props: { module: item },
        meta: { title: item.title, roles: item.roles }
      }))
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (!['/login', '/register'].includes(to.path) && !userStore.token) return '/login'
  const roles = to.meta.roles
  if (roles?.length && !roles.includes(userStore.role)) return userStore.role === 'ROLE_USER' ? '/customers' : '/dashboard'
})

export default router