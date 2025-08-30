import React, { useEffect, useState } from "react";
import {
  getUserTransactions,
  addTransaction,
  deleteTransaction,
} from "../services/transactionService";
import { getAllCategories } from "../services/categoryService";
import { getGoals } from "../services/goalService";
import "../styles/TransactionStyle.css"; // ✅ External CSS

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [goals, setGoals] = useState([]);

  const [form, setForm] = useState({
    amount: "",
    description: "",
    date: "",
    type: "INCOME",
    categoryId: "",
    goalId: "",
    goalAmount: "",
  });

  useEffect(() => {
    fetchTransactions();
    fetchCategories();
    fetchGoals();
  }, []);

  const fetchTransactions = async () => {
    try {
      const res = await getUserTransactions();
      setTransactions(res.data);
    } catch (err) {
      console.error("Failed to fetch transactions", err);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await getAllCategories();
      setCategories(res.data);
    } catch (err) {
      console.error("Failed to fetch categories", err);
    }
  };

  const fetchGoals = async () => {
    try {
      const res = await getGoals();
      setGoals(res.data);
    } catch (err) {
      console.error("Failed to fetch goals", err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      ...form,
      amount: parseFloat(form.amount),
      goalId: form.goalId || null,
      goalAmount: form.goalId ? parseFloat(form.goalAmount || 0) : null,
    };

    try {
      await addTransaction(payload);
      fetchTransactions();
      setForm({
        amount: "",
        description: "",
        date: "",
        type: "INCOME",
        categoryId: "",
        goalId: "",
        goalAmount: "",
      });
    } catch (err) {
      console.error("Transaction submit failed:", err);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteTransaction(id);
      fetchTransactions();
    } catch (err) {
      console.error("Failed to delete transaction", err);
    }
  };

  return (
    <div className="transactions-container">
      <h2 className="section-title">Add New Transaction</h2>
      <form className="transaction-form" onSubmit={handleSubmit}>
        <input
          type="number"
          placeholder="Amount"
          value={form.amount}
          onChange={(e) => setForm({ ...form, amount: e.target.value })}
          required
        />

        <input
          type="text"
          placeholder="Description"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />

        <input
          type="date"
          value={form.date}
          onChange={(e) => setForm({ ...form, date: e.target.value })}
          required
        />

        <select
          value={form.type}
          onChange={(e) => setForm({ ...form, type: e.target.value })}
          required
        >
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>

        <select
          value={form.categoryId}
          onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
          required
        >
          <option value="">Select Category</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>

        <select
          value={form.goalId}
          onChange={(e) =>
            setForm({ ...form, goalId: e.target.value, goalAmount: "" })
          }
        >
          <option value="">No Goal</option>
          {goals.map((goal) => (
            <option key={goal.id} value={goal.id}>
              {goal.title}
            </option>
          ))}
        </select>

        {form.goalId && (
          <input
            type="number"
            placeholder="Amount toward Goal"
            value={form.goalAmount}
            onChange={(e) => setForm({ ...form, goalAmount: e.target.value })}
          />
        )}

        <button type="submit" className="submit-button">
          Add Transaction
        </button>
      </form>

      <h3 className="section-title">Transaction History</h3>
      <div className="table-container">
        <table className="transaction-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Type</th>
              <th>Amount (₹)</th>
              <th>Description</th>
              <th>Category</th>
              <th>Goal</th>
              {/* <th>Action</th> */}
            </tr>
          </thead>
          <tbody>
            {transactions.length === 0 ? (
              <tr>
                <td colSpan="7" className="no-data">
                  No transactions found.
                </td>
              </tr>
            ) : (
              transactions.map((tx) => (
                <tr key={tx.id}>
                  <td>{tx.date}</td>
                  <td>{tx.type}</td>
                  <td>{tx.amount}</td>
                  <td>{tx.description || "-"}</td>
                  <td>{tx.category}</td>
                  <td>{tx.goal ? tx.goal : "-"}</td>
                  {/* <td>
                    <button
                      onClick={() => handleDelete(tx.id)}
                      className="delete-button"
                    >
                      Delete
                    </button>
                  </td> */}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Transactions;
