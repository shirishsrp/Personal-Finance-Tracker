import axios from 'axios';

const BASE = 'http://localhost:8080/api/admin';
const auth = () => ({ headers: { Authorization: 'Bearer ' + localStorage.getItem('token') } });

export const getUserStats = () => axios.get(`${BASE}/user-stats`, auth());
export const getTransactionSummary = () => axios.get(`${BASE}/transaction-summary`, auth());

export const getTopSpenders = () =>
  axios.get(`${BASE}/top-spenders`, auth());

export const getTopExpenseCategories = (limit = 5) =>
  axios.get(`${BASE}/top-expense-categories?limit=${limit}`, auth());


