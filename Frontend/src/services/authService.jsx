import axios from 'axios';

const API_BASE = 'http://localhost:8080/auth';

export const loginUser = async (credentials) => {
  return axios.post(`${API_BASE}/signin`, credentials);  
};

export const registerUser = async (data) => {
  return axios.post(`${API_BASE}/signup`, data);  
};
