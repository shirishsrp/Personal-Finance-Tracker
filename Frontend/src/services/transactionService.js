import axios from 'axios';
const BASE = 'http://localhost:8080/api/transactions';
const auth = () => ({ headers: { Authorization: 'Bearer ' + localStorage.getItem('token') } });

//ret
export const getTransactionSummary = () => axios.get(`${BASE}/summary`, auth());
export const getSpendingByCategory = () => axios.get(`${BASE}/spending-by-category`, auth());
export const getRecentTransactions = () => axios.get(`${BASE}/history`, auth());




export const getUserTransactions = () => axios.get(`${BASE}/history`, auth());
export const addTransaction = (data) => axios.post(`${BASE}/add`, data, auth());
export const deleteTransaction = (id) => axios.delete(`${BASE}/${id}`, auth());
// (Optional) export const updateTransaction = (id, data) => axios.put(`${BASE}/${id}`, data, auth());