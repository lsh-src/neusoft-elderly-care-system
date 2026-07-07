import { defineStore } from 'pinia'
import { login as loginApi, me } from '../api/auth'

export const useUserStore = defineStore('user', {
  state: () => {
    let user = null
    try { user = JSON.parse(localStorage.getItem('user') || 'null') } catch { user = null }
    return {
      token: localStorage.getItem('token') || '',
      user
    }
  },
  getters: {
    role: (state) => state.user?.role || '',
    isAdmin: (state) => state.user?.role === 'ROLE_ADMIN'
  },
  actions: {
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      this.user = data.user
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    },
    async fetchMe() {
      this.user = await me()
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
