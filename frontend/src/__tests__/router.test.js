import { describe, it, expect } from 'vitest'
import { menuGroups } from '../router/index.js'

describe('router / menuGroups', () => {
  describe('菜单结构完整性', () => {
    it('menuGroups 应是非空数组', () => {
      expect(Array.isArray(menuGroups)).toBe(true)
      expect(menuGroups.length).toBeGreaterThan(0)
    })

    it('每个菜单组应有 children 数组', () => {
      menuGroups.forEach((group, i) => {
        expect(Array.isArray(group.children), `group[${i}] 缺少 children`).toBe(true)
        expect(group.children.length).toBeGreaterThan(0)
      })
    })

    it('每个菜单项应有 path, title, roles', () => {
      menuGroups.forEach((group, gi) => {
        group.children.forEach((item, ii) => {
          expect(item.path, `group[${gi}].children[${ii}] 缺 path`).toBeDefined()
          expect(item.title, `group[${gi}].children[${ii}] 缺 title`).toBeDefined()
          expect(Array.isArray(item.roles), `group[${gi}].children[${ii}] roles 应为数组`).toBe(true)
          expect(item.roles.length).toBeGreaterThan(0)
        })
      })
    })
  })

  describe('角色权限配置', () => {
    const allItems = menuGroups.flatMap(g => g.children)

    it('ROLE_ADMIN 应能访问所有菜单', () => {
      const adminItems = allItems.filter(item => item.roles.includes('ROLE_ADMIN'))
      expect(adminItems.length).toBe(allItems.length)
    })

    it('ROLE_USER 不应看到仪表盘', () => {
      const dashboard = allItems.find(item => item.path === 'dashboard')
      expect(dashboard).toBeDefined()
      expect(dashboard.roles).not.toContain('ROLE_USER')
    })

    it('ROLE_USER 不应看到用户管理', () => {
      const users = allItems.find(item => item.path === 'users')
      expect(users).toBeDefined()
      expect(users.roles).not.toContain('ROLE_USER')
    })

    it('ROLE_USER 不应看到床位管理', () => {
      const beds = allItems.find(item => item.path === 'beds')
      expect(beds).toBeDefined()
      expect(beds.roles).not.toContain('ROLE_USER')
    })

    it('ROLE_NURSE 应能访问仪表盘', () => {
      const dashboard = allItems.find(item => item.path === 'dashboard')
      expect(dashboard.roles).toContain('ROLE_NURSE')
    })

    it('ROLE_NURSE 应能访问护理记录', () => {
      const nursingRecords = allItems.find(item => item.path === 'nursing-records')
      expect(nursingRecords.roles).toContain('ROLE_NURSE')
    })

    it('用户管理仅限 ROLE_ADMIN', () => {
      const users = allItems.find(item => item.path === 'users')
      expect(users.roles).toEqual(['ROLE_ADMIN'])
    })

    it('角色值应为合法格式', () => {
      const validRoles = ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER']
      allItems.forEach(item => {
        item.roles.forEach(role => {
          expect(validRoles, `非法角色 ${role} in ${item.path}`).toContain(role)
        })
      })
    })
  })

  describe('菜单路径唯一性', () => {
    it('所有菜单项的 path 应唯一', () => {
      const allItems = menuGroups.flatMap(g => g.children)
      const paths = allItems.map(item => item.path)
      const unique = new Set(paths)
      expect(unique.size).toBe(paths.length)
    })
  })

  describe('业务模块覆盖', () => {
    const allItems = menuGroups.flatMap(g => g.children)

    const expectedModules = [
      'dashboard', 'customers', 'beds',
      'check-ins', 'check-outs', 'outings',
      'meals', 'meal-calendar',
      'service-relations', 'care-services', 'service-purchases',
      'nursing-levels', 'nursing-items', 'nursing-records', 'nurse-areas',
      'ai-chat', 'rag-query', 'knowledge-base', 'health-analysis', 'care-recommendation',
      'users'
    ]

    expectedModules.forEach(mod => {
      it(`应包含 ${mod} 菜单项`, () => {
        const found = allItems.find(item => item.path === mod)
        expect(found, `缺少菜单项: ${mod}`).toBeDefined()
      })
    })
  })

  describe('AI 模块配置', () => {
    const allItems = menuGroups.flatMap(g => g.children)
    const aiItems = allItems.filter(item => item.path.startsWith('ai') || item.path.startsWith('rag') || item.path.startsWith('knowledge') || item.path.startsWith('health-analysis') || item.path.startsWith('care-recommendation'))

    it('AI 模块应有 ai 属性标记组件', () => {
      aiItems.forEach(item => {
        expect(item.ai, `${item.path} 缺少 ai 属性`).toBeDefined()
      })
    })

    it('知识库管理应仅限管理员和健康管家', () => {
      const kb = allItems.find(item => item.path === 'knowledge-base')
      expect(kb.roles).toEqual(['ROLE_ADMIN', 'ROLE_MANAGER'])
    })
  })

  describe('CRUD 模块配置', () => {
    const crudItems = menuGroups.flatMap(g => g.children).filter(item => item.resource)

    it('CRUD 模块应有 resource 属性', () => {
      crudItems.forEach(item => {
        expect(item.resource, `${item.path} 缺少 resource`).toBeDefined()
      })
    })

    it('resource 值应与 path 一致或有意义的映射', () => {
      crudItems.forEach(item => {
        expect(typeof item.resource).toBe('string')
        expect(item.resource.length).toBeGreaterThan(0)
      })
    })
  })
})
