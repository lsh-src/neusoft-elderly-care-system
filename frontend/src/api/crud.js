import request from '../utils/request'

export const pageApi = (resource, params) => request.get(`/${resource}/page`, { params })
export const listApi = (resource) => request.get(`/${resource}`)
export const detailApi = (resource, id) => request.get(`/${resource}/${id}`)
export const createApi = (resource, data) => request.post(`/${resource}`, data)
export const updateApi = (resource, id, data) => request.put(`/${resource}/${id}`, data)
export const deleteApi = (resource, id) => request.delete(`/${resource}/${id}`)
export const outingReturnApi = (id) => request.post(`/outings/${id}/return`)
export const nursingConfirmApi = (id, result) => request.post(`/nursing-records/${id}/confirm`, null, { params: { result } })
export const exportApi = (resource, params) => request.get(`/${resource}/export`, { params, responseType: 'blob' })
