import api from './api'

export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  adminLogin: (data) => api.post('/auth/admin/login', data),
  adminRegister: (data) => api.post('/auth/admin/register', data),
}
