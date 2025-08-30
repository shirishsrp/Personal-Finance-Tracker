import axios from 'axios';
const BASE = 'http://localhost:8080/api/budgets';

const auth = () => ({
  headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
});
// export const getBudgetsWithActuals = () =>
//   axios.get(`${BASE}/user-with-actuals`, {
//     headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
//   });

export const getBudgetsWithActuals = () =>
  axios.get(`${BASE}/user-with-actuals`, auth());


export const createOrUpdateBudget = (data) =>
  axios.post(`${BASE}`, data, auth());