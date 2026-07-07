<template>
  <el-container class="layout">
    <el-aside width="252px" class="aside">
      <div class="logo">
        <div class="mark">
          <svg viewBox="0 0 128 128" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="aside-bg" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#2f6f62" />
                <stop offset="100%" style="stop-color:#1a4a3e" />
              </linearGradient>
            </defs>
            <rect width="128" height="128" rx="28" fill="url(#aside-bg)" />
            <path
              d="M64 95 C40 72, 28 58, 28 46 C28 35, 37 28, 46 28 C52 28, 58 32, 64 38 C70 32, 76 28, 82 28 C91 28, 100 35, 100 46 C100 58, 88 72, 64 95Z"
              fill="white" opacity="0.95" />
            <rect x="58" y="50" width="12" height="28" rx="3" fill="#2f6f62" />
            <rect x="50" y="58" width="28" height="12" rx="3" fill="#2f6f62" />
          </svg>
        </div>
        <div>
          <strong>东软颐养中心</strong>
          <span>Care Operations</span>
        </div>
      </div>

      <div class="menu-wrap">
        <el-menu router :default-active="$route.path" background-color="transparent" text-color="#dce8e0"
          active-text-color="#f2c96d" :unique-opened="false">

          <template v-for="group in filteredMenuGroups" :key="group.groupName || 'default-group'">
            <template v-if="!group.groupName">
              <el-menu-item v-for="item in group.children" :key="item.path" :index="`/${item.path}`">
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </el-menu-item>
            </template>

            <el-sub-menu v-else :index="group.groupName">
              <template #title>
                <el-icon>
                  <component :is="group.icon" />
                </el-icon>
                <span>{{ group.groupName }}</span>
              </template>
              <el-menu-item v-for="item in group.children" :key="item.path" :index="`/${item.path}`">
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </el-menu-item>
            </el-sub-menu>
          </template>

          <div class="menu-divider"></div>

          <el-menu-item index="/profile">
            <el-icon>
              <UserFilled />
            </el-icon>
            <span>个人信息</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="aside-footer">
        <span>Neusoft &copy; 2026</span>
      </div>
    </el-aside>

    <el-container class="right-wrap">
      <el-header class="header">
        <div class="header-left">
          <h2>{{ $route.meta.title || '东软颐养中心管理系统' }}</h2>
          <p>今天也把复杂的照护工作梳理得稳稳当当。</p>
        </div>
        <el-dropdown trigger="click">
          <div class="user">
            <el-avatar :size="36" class="user-avatar">
              {{ userStore.user?.name?.slice(0, 1) }}
            </el-avatar>
            <div class="user-meta">
              <span class="user-name">{{ userStore.user?.name }}</span>
              <span class="user-role">{{ roleName }}</span>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/profile')">
                <el-icon>
                  <User />
                </el-icon>个人信息
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon>
                  <SwitchButton />
                </el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component, route }">
          <transition name="page-fade">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { menuGroups } from '../router'
import { useUserStore } from '../stores/user'
import {
  DataAnalysis, UserFilled, Avatar, House, CirclePlus, Remove, Van,
  Food, Calendar, Connection, Star, ShoppingCart, Medal, FirstAidKit,
  DocumentChecked, User, Document, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const roleName = computed(() => ({
  ROLE_ADMIN: '管理员',
  ROLE_MANAGER: '健康管家',
  ROLE_NURSE: '护士',
  ROLE_USER: '入住老人'
}[userStore.role] || '未知'))

const filteredMenuGroups = computed(() =>
  menuGroups
    .map(group => ({
      ...group,
      children: group.children.filter(item => item.roles.includes(userStore.role))
    }))
    .filter(group => group.children.length > 0)
)

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  height: 100vh;
  overflow: hidden;
}

.aside {
  height: 100%;
  padding: 24px 14px 12px;
  background:
    radial-gradient(circle at 20% 10%, rgba(242, 201, 109, 0.26), transparent 10rem),
    linear-gradient(180deg, #244b42, #142d28);
  display: flex;
  flex-direction: column;
}

.menu-wrap {
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
}

.menu-wrap::-webkit-scrollbar {
  width: 4px;
}

.menu-wrap::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px 24px;
  color: #fff;
  flex-shrink: 0;
}

.mark {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.mark svg {
  width: 100%;
  height: 100%;
  display: block;
}

.logo strong {
  font-size: 15px;
  letter-spacing: 0.02em;
}

.logo span {
  display: block;
  margin-top: 4px;
  color: #b8ccc4;
  font-size: 12px;
}

.menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  margin: 8px 16px;
}

.aside-footer {
  padding: 12px 10px 0;
  flex-shrink: 0;
}

.aside-footer span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.2);
  letter-spacing: 0.05em;
}

.el-menu {
  border: 0;
}

:deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 2px;
  transition: all 0.2s ease;
}

:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

:deep(.el-menu-item.is-active) {
  background: rgba(242, 201, 109, 0.12) !important;
}

:deep(.el-sub-menu__title) {
  padding-left: 20px !important;
  border-radius: 10px;
  transition: all 0.2s ease;
}

:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 54px !important;
  min-width: auto;
}

:deep(.el-sub-menu__icon-arrow) {
  color: #dce8e0 !important;
}

.right-wrap {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f8f7;
}

.header {
  height: 82px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid #e8f0ed;
  flex-shrink: 0;
}

.header-left h2 {
  margin: 0 0 4px;
  font-size: 20px;
  color: #244b42;
}

.header-left p {
  margin: 0;
  color: #8aaa9e;
  font-size: 13px;
}

.user {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 12px;
  transition: background 0.2s ease;
}

.user:hover {
  background: #f0f7f5;
}

.user-avatar {
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  color: #fff;
  font-weight: 700;
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  font-weight: 600;
  color: #244b42;
  font-size: 14px;
}

.user-role {
  font-size: 12px;
  color: #8aaa9e;
}

.main {
  padding: 26px;
  flex: 1;
  overflow-y: auto;
  position: relative;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.page-fade-enter-active {
  z-index: 1;
}

.page-fade-leave-active {
  position: absolute;
  width: 100%;
  z-index: 0;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
