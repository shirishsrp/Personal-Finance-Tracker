import axios from 'axios';

const API_BASE = 'http://localhost:8080/categories';

const auth = () => ({ headers: { Authorization: 'Bearer ' + localStorage.getItem('token') } });


export const getAllCategories = () => axios.get(API_BASE, auth());
//export const getTransactionSummary = () => axios.get(`${BASE}/summary`, auth());

export const createCategory = (data) => axios.post(API_BASE, data, auth());
export const updateCategory = (id, data) => axios.put(`${API_BASE}/${id}`, data, auth());
export const deleteCategory = (id) => axios.delete(`${API_BASE}/${id}`, auth());