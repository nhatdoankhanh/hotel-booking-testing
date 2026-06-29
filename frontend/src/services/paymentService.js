import api from './api'

export const paymentService = {
  getAll: () => api.get('/payments'),
  getById: (id) => api.get(`/payments/${id}`),
  makePayment: (bookingId, paymentMethod = 'online') =>
    api.post('/payments', { booking_id: bookingId, payment_method: paymentMethod }),
}
