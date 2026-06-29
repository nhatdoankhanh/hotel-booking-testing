import api from './api'

export const roomService = {
  getAll: (hotelId) => api.get('/rooms', { params: hotelId ? { hotel_id: hotelId } : {} }),
  getAvailable: (hotelId) => api.get('/rooms/available', { params: { hotel_id: hotelId } }),
  getById: (id) => api.get(`/rooms/${id}`),
  create: (data) => api.post('/rooms', data),
  update: (id, data) => api.put(`/rooms/${id}`, data),
  delete: (id) => api.delete(`/rooms/${id}`),
}
