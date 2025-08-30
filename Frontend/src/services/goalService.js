
import axios from 'axios';

const BASE = 'http://localhost:8080/api/goals';
const auth = () => ({
  headers: { Authorization: 'Bearer ' + localStorage.getItem('token') },
});

export const getGoals = () => axios.get(BASE, auth());
export const createGoal = (goalData) => axios.post(`${BASE}/create`, goalData, auth());
export const cancelGoal = (id) => axios.post(`${BASE}/${id}/cancel`, {}, auth());

export const updateGoal = (id, updatedData) => axios.put(`${BASE}/${id}`, updatedData, auth());

