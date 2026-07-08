import { describe, it, expect, vi, beforeEach } from 'vitest'

// 使用 vi.hoisted 确保 mock 变量在 vi.mock 之前可用
const { mockGet, mockPost, mockPut, mockDelete } = vi.hoisted(() => ({
  mockGet: vi.fn().mockResolvedValue({}),
  mockPost: vi.fn().mockResolvedValue({}),
  mockPut: vi.fn().mockResolvedValue({}),
  mockDelete: vi.fn().mockResolvedValue({}),
}))

vi.mock('../utils/request', () => ({
  default: {
    get: mockGet,
    post: mockPost,
    put: mockPut,
    delete: mockDelete,
  }
}))

import {
  pageApi,
  listApi,
  detailApi,
  createApi,
  updateApi,
  deleteApi,
  outingReturnApi,
  nursingConfirmApi,
  exportApi
} from '../api/crud.js'

describe('api/crud.js CRUD API 工厂', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('pageApi', () => {
    it('应调用 GET /{resource}/page 并传入 params', () => {
      pageApi('customers', { current: 1, size: 10 })
      expect(mockGet).toHaveBeenCalledWith('/customers/page', {
        params: { current: 1, size: 10 }
      })
    })

    it('不同资源应生成不同路径', () => {
      pageApi('beds', { current: 1 })
      expect(mockGet).toHaveBeenCalledWith('/beds/page', { params: { current: 1 } })

      pageApi('nursing-records', { current: 2, size: 20 })
      expect(mockGet).toHaveBeenCalledWith('/nursing-records/page', { params: { current: 2, size: 20 } })
    })
  })

  describe('listApi', () => {
    it('应调用 GET /{resource}', () => {
      listApi('customers')
      expect(mockGet).toHaveBeenCalledWith('/customers')
    })
  })

  describe('detailApi', () => {
    it('应调用 GET /{resource}/{id}', () => {
      detailApi('customers', 42)
      expect(mockGet).toHaveBeenCalledWith('/customers/42')
    })
  })

  describe('createApi', () => {
    it('应调用 POST /{resource} 并传入 data', () => {
      const data = { name: '张三', age: 68 }
      createApi('customers', data)
      expect(mockPost).toHaveBeenCalledWith('/customers', data)
    })
  })

  describe('updateApi', () => {
    it('应调用 PUT /{resource}/{id} 并传入 data', () => {
      const data = { name: '李四' }
      updateApi('customers', 5, data)
      expect(mockPut).toHaveBeenCalledWith('/customers/5', data)
    })
  })

  describe('deleteApi', () => {
    it('应调用 DELETE /{resource}/{id}', () => {
      deleteApi('customers', 3)
      expect(mockDelete).toHaveBeenCalledWith('/customers/3')
    })
  })

  describe('outingReturnApi', () => {
    it('应调用 POST /outings/{id}/return', () => {
      outingReturnApi(7)
      expect(mockPost).toHaveBeenCalledWith('/outings/7/return')
    })
  })

  describe('nursingConfirmApi', () => {
    it('应调用 POST /nursing-records/{id}/confirm 并传入 result 参数', () => {
      nursingConfirmApi(10, '已完成')
      expect(mockPost).toHaveBeenCalledWith('/nursing-records/10/confirm', null, {
        params: { result: '已完成' }
      })
    })

    it('result 为异常时应正确传递', () => {
      nursingConfirmApi(11, '异常')
      expect(mockPost).toHaveBeenCalledWith('/nursing-records/11/confirm', null, {
        params: { result: '异常' }
      })
    })
  })

  describe('exportApi', () => {
    it('应调用 GET /{resource}/export 并设置 responseType 为 blob', () => {
      exportApi('customers', { keyword: '张' })
      expect(mockGet).toHaveBeenCalledWith('/customers/export', {
        params: { keyword: '张' },
        responseType: 'blob'
      })
    })
  })

  describe('通用行为', () => {
    it('所有 API 函数应返回 Promise', () => {
      const results = [
        pageApi('x', {}),
        listApi('x'),
        detailApi('x', 1),
        createApi('x', {}),
        updateApi('x', 1, {}),
        deleteApi('x', 1),
        outingReturnApi(1),
        nursingConfirmApi(1, '正常'),
        exportApi('x', {}),
      ]
      results.forEach(r => {
        expect(r).toBeInstanceOf(Promise)
      })
    })
  })
})
