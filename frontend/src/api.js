import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const authService = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (userData) => api.post('/auth/register', userData),
};

export const appointmentService = {
  create: (appointmentData) => api.post('/appointments', appointmentData),
  getPatientAppointments: () => api.get('/appointments/patient'),
  getDoctorAppointments: () => api.get('/appointments/doctor'),
  updateStatus: (id, status, notes) => api.put(`/appointments/${id}/status`, { status, notes }),
  getDoctors: () => api.get('/appointments/doctors'),
};

export const healthRecordService = {
  create: (formData) => {
    return api.post('/health-records', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  getRecords: () => api.get('/health-records'),
  getRecord: (id) => api.get(`/health-records/${id}`),
};

export default api;
