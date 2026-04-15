import api from './api'

export const authService = {
  register: (data) => api.post('/auth/register', data),

  sendVerification: (email) =>
    api.post('/auth/send-verification', { email }),

  verifyEmail: (email, code) =>
    api.post('/auth/verify-email', { email, code }),

  login: (email, password) =>
    api.post('/auth/login', { email, password }),

  refreshToken: (refreshToken) =>
    api.post('/auth/refresh', null, { params: { refreshToken } }),
}

export const userService = {
  getAll: (params) => api.get('/users', { params }),
  getMe: () => api.get('/users/me'),
  getById: (id) => api.get(`/users/${id}`),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
  getDirectReports: (managerId) => api.get(`/users/${managerId}/reports`),
  getOrgChart: () => api.get('/users/org-chart'),
}

export const roleService = {
  getAll: () => api.get('/roles'),
  getById: (id) => api.get(`/roles/${id}`),
}

export const taskService = {
  create: (data) => api.post('/tasks', data),
  getAll: (params) => api.get('/tasks', { params }),
  getMyTasks: (params) => api.get('/tasks/my-tasks', { params }),
  getAssignedByMe: (params) => api.get('/tasks/assigned-by-me', { params }),
  getKanban: () => api.get('/tasks/kanban'),
  getById: (id) => api.get(`/tasks/${id}`),
  getSubTasks: (id) => api.get(`/tasks/${id}/subtasks`),
  update: (id, data) => api.put(`/tasks/${id}`, data),
  delete: (id) => api.delete(`/tasks/${id}`),
}
