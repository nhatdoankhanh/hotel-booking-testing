import api from './api'

export const bookingService = {
  getAll: () => api.get('/bookings'),
  getMy: () => api.get('/bookings/my'),
  getById: (id) => api.get(`/bookings/${id}`),
  create: (data) => api.post('/bookings', data),
  cancel: (id) => api.put(`/bookings/${id}/cancel`),
  updateStatus: (id, status) => api.put(`/bookings/${id}/status`, { status }),
}
