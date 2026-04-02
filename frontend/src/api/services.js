import api from './client'

// ── Auth ──────────────────────────────────────────────────────────────────────
export const authApi = {
  login:    (email, password)  => api.post('/auth/login',    { email, password }),
  register: (data)             => api.post('/auth/register', data),
  me:       ()                 => api.get('/users/me'),
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
export const dashboardApi = {
  summary: () => api.get('/dashboard/summary'),
}

// ── Records ───────────────────────────────────────────────────────────────────
export const recordsApi = {
  list:   (params) => api.get('/records', { params }),
  get:    (id)     => api.get(`/records/${id}`),
  create: (data)   => api.post('/records', data),
  update: (id, data) => api.patch(`/records/${id}`, data),
  delete: (id)     => api.delete(`/records/${id}`),
}

// ── Users ─────────────────────────────────────────────────────────────────────
export const usersApi = {
  list:   ()          => api.get('/users'),
  get:    (id)        => api.get(`/users/${id}`),
  update: (id, data)  => api.patch(`/users/${id}`, data),
  delete: (id)        => api.delete(`/users/${id}`),
}
