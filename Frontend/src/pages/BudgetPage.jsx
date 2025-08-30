import React, { useEffect, useState } from "react";
import {
  getBudgetsWithActuals,
  createOrUpdateBudget,
} from "../services/budgetService";
import { getAllCategories } from "../services/categoryService";
import "../styles/BudgetStyle.css";

const Budget = () => {
  const [budgets, setBudgets] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({
    categoryName: "",
    month: "",
    monthlyLimit: "",
  });

  const fetchBudgets = async () => {
    try {
      const res = await getBudgetsWithActuals();
      setBudgets(res.data);
    } catch (err) {
      console.error("Error fetching budgets", err);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await getAllCategories();
      setCategories(res.data);
    } catch (err) {
      console.error("Error fetching categories", err);
    }
  };

  useEffect(() => {
    fetchBudgets();
    fetchCategories();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await createOrUpdateBudget({
        ...form,
        monthlyLimit: parseFloat(form.monthlyLimit),
      });
      setForm({
        categoryName: "",
        month: "",
        monthlyLimit: "",
      });
      fetchBudgets();
    } catch (err) {
      let msg = "Something went wrong";

      if (err?.response?.data) {
        const data = err.response.data;
        if (typeof data === "object") {
          msg = Object.entries(data)
            .map(([field, message]) => `${field}: ${message}`)
            .join("\n");
        } else {
          msg = data.toString();
        }
      }

      alert(msg);
      console.error("Error creating/updating budget", err);
    }
  };

  return (
    <div className="budget-container">
      <h2 className="section-title">Set/Update Monthly Budget</h2>
      <form className="budget-form" onSubmit={handleSubmit}>
        <select
          id="categoryName"
          name="categoryName"
          value={form.categoryName}
          onChange={(e) =>
            setForm({ ...form, categoryName: e.target.value })
          }
          required
        >
          <option value="">Select Category</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.name}>
              {cat.name}
            </option>
          ))}
        </select>

        <input
          id="month"
          name="month"
          type="month"
          value={form.month}
          onChange={(e) => setForm({ ...form, month: e.target.value })}
          required
        />

        <input
          id="monthlyLimit"
          name="monthlyLimit"
          type="number"
          placeholder="Monthly Limit"
          value={form.monthlyLimit}
          onChange={(e) => setForm({ ...form, monthlyLimit: e.target.value })}
          required
        />

        <button type="submit" className="submit-button">Save Budget</button>
      </form>

      <h3 className="section-title">Your Budgets</h3>
      <div className="table-container">
        <table className="budget-table">
          <thead>
            <tr>
              <th>Category</th>
              <th>Month</th> 
              <th>Monthly Limit (₹)</th>
              <th>Actual Spent (₹)</th>
            </tr>
          </thead>
          <tbody>
            {budgets.length === 0 ? (
              <tr>
                <td colSpan="3" className="no-data">No budgets found</td>
              </tr>
            ) : (
              budgets.map((b, i) => (
                <tr key={i}>
                  <td>{b.category}</td>
                  <td>{b.month}</td> 
                  <td>{b.limit}</td>
                  <td>{b.actualSpent}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Budget;
