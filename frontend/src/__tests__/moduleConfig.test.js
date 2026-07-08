import { describe, it, expect } from 'vitest'
import {
  fieldMap,
  getExtraColumns,
  dropdownSources,
  searchHints,
  toFields,
  roleFormatter,
  enabledFormatter,
  checkedInFormatter
} from '../views/moduleConfig.js'

describe('moduleConfig', () => {
  describe('fieldMap', () => {
    it('应包含所有业务模块的字段定义', () => {
      const expectedModules = [
        'users', 'customers', 'beds', 'check-ins', 'check-outs',
        'outings', 'meals', 'service-relations', 'care-services',
        'service-purchases', 'nursing-levels', 'nursing-items',
        'nursing-records', 'nurse-areas'
      ]
      expectedModules.forEach(mod => {
        expect(fieldMap[mod], `模块 ${mod} 缺失`).toBeDefined()
        expect(fieldMap[mod].length).toBeGreaterThan(0)
      })
    })

    it('每个字段配置应至少有 [prop, label, type] 三项', () => {
      Object.entries(fieldMap).forEach(([mod, fields]) => {
        fields.forEach((field, i) => {
          expect(field.length, `模块 ${mod} 第 ${i} 项配置不完整`).toBeGreaterThanOrEqual(3)
          expect(typeof field[0]).toBe('string') // prop
          expect(typeof field[1]).toBe('string') // label
          expect(typeof field[2]).toBe('string') // type
        })
      })
    })

    it('users 模块应包含 phone, password, name, age, gender, role, enabled 字段', () => {
      const props = fieldMap.users.map(f => f[0])
      expect(props).toContain('phone')
      expect(props).toContain('password')
      expect(props).toContain('name')
      expect(props).toContain('age')
      expect(props).toContain('gender')
      expect(props).toContain('role')
      expect(props).toContain('enabled')
    })

    it('customers 模块应包含 name, gender, age, phone 字段', () => {
      const props = fieldMap.customers.map(f => f[0])
      expect(props).toContain('name')
      expect(props).toContain('gender')
      expect(props).toContain('age')
      expect(props).toContain('phone')
    })

    it('beds 模块应包含 roomNo, bedNo, status 字段', () => {
      const props = fieldMap.beds.map(f => f[0])
      expect(props).toContain('roomNo')
      expect(props).toContain('bedNo')
      expect(props).toContain('status')
    })
  })

  describe('getExtraColumns', () => {
    it('customers 应返回 customerNo 额外列', () => {
      const cols = getExtraColumns('customers')
      expect(cols).toEqual([{ prop: 'customerNo', label: '客户编号' }])
    })

    it('beds 应返回 customerName 额外列', () => {
      const cols = getExtraColumns('beds')
      expect(cols).toEqual([{ prop: 'customerName', label: '入住客户' }])
    })

    it('check-ins 应返回 registerNo 额外列', () => {
      const cols = getExtraColumns('check-ins')
      expect(cols).toEqual([{ prop: 'registerNo', label: '登记编号' }])
    })

    it('无配置的模块应返回空数组', () => {
      expect(getExtraColumns('nonexistent')).toEqual([])
      expect(getExtraColumns('nursing-levels')).toEqual([])
    })
  })

  describe('dropdownSources', () => {
    it('check-ins 应有 customerId, bedId, operator 三个下拉源', () => {
      const sources = dropdownSources['check-ins']
      expect(sources).toHaveLength(3)
      expect(sources.map(s => s.prop)).toEqual(['customerId', 'bedId', 'operator'])
    })

    it('每个下拉源应包含 prop, resource, label, value', () => {
      Object.entries(dropdownSources).forEach(([mod, sources]) => {
        sources.forEach((src, i) => {
          expect(src.prop, `${mod}[${i}] 缺 prop`).toBeDefined()
          expect(src.resource, `${mod}[${i}] 缺 resource`).toBeDefined()
          expect(src.label, `${mod}[${i}] 缺 label`).toBeDefined()
          expect(src.value, `${mod}[${i}] 缺 value`).toBeDefined()
        })
      })
    })
  })

  describe('searchHints', () => {
    it('应为每个模块提供搜索提示', () => {
      Object.keys(fieldMap).forEach(mod => {
        expect(searchHints[mod], `模块 ${mod} 缺搜索提示`).toBeDefined()
        expect(searchHints[mod].length).toBeGreaterThan(0)
      })
    })
  })

  describe('toFields', () => {
    it('应将数组格式转换为对象格式', () => {
      const fields = toFields('beds')
      expect(fields).toHaveLength(3)
      expect(fields[0]).toEqual({
        prop: 'roomNo',
        label: '房间编号',
        type: 'text',
        required: true,
        options: [],
        default: null
      })
    })

    it('不存在的模块应返回空数组', () => {
      expect(toFields('nonexistent')).toEqual([])
    })

    it('select 类型字段应正确携带 options', () => {
      const fields = toFields('beds')
      const statusField = fields.find(f => f.prop === 'status')
      expect(statusField.type).toBe('select')
      expect(statusField.options).toEqual(['空闲', '已入住', '维修中'])
      expect(statusField.default).toBe('空闲')
    })
  })

  describe('roleFormatter', () => {
    it('应正确映射所有角色', () => {
      expect(roleFormatter('ROLE_ADMIN')).toBe('管理员')
      expect(roleFormatter('ROLE_MANAGER')).toBe('健康管家')
      expect(roleFormatter('ROLE_NURSE')).toBe('护士')
      expect(roleFormatter('ROLE_USER')).toBe('入住老人')
    })

    it('未知角色应原样返回', () => {
      expect(roleFormatter('ROLE_UNKNOWN')).toBe('ROLE_UNKNOWN')
      expect(roleFormatter('')).toBe('')
    })
  })

  describe('enabledFormatter', () => {
    it('1 应返回 正常', () => expect(enabledFormatter(1)).toBe('正常'))
    it('0 应返回 禁用', () => expect(enabledFormatter(0)).toBe('禁用'))
  })

  describe('checkedInFormatter', () => {
    it('1 应返回 已入住', () => expect(checkedInFormatter(1)).toBe('已入住'))
    it('0 应返回 未入住', () => expect(checkedInFormatter(0)).toBe('未入住'))
  })
})
