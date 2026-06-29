import api from './api'

export const hotelService = {
  getAll: (city) => api.get('/hotels', { params: city ? { city } : {} }),
  getById: (id) => api.get(`/hotels/${id}`),
  create: (data) => api.post('/hotels', data),
  update: (id, data) => api.put(`/hotels/${id}`, data),
  delete: (id) => api.delete(`/hotels/${id}`),
}
