import React, { useEffect, useState } from 'react';
import { getTransactionSummary, getSpendingByCategory, getRecentTransactions } from '../services/transactionService';
import { getBudgetsWithActuals } from '../services/budgetService';
import { getGoals } from '../services/goalService';
import Navbar from '../components/Navbar';
import '../index.css';
import '../styles/DashboardStyle.css';
import CategoryPieChart from '../components/CategoryPieChart';
import { Link } from "react-router-dom";


const DashboardPage = () => {
  const [summary, setSummary] = useState(null);
  const [categorySpending, setCategorySpending] = useState([]);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [budgets, setBudgets] = useState([]);
  const [goals, setGoals] = useState([]);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [summaryRes, categoryRes, recentRes, budgetRes, goalRes] = await Promise.all([
        getTransactionSummary(),
        getSpendingByCategory(),
        getRecentTransactions(),
        getBudgetsWithActuals(),
        getGoals()
      ]);

      console.log("Budget Data:", budgetRes.data); // Debugging line to check budget data
      console.log("Goal Data:", goalRes.data); // Debugging line to check goal data
      console.log("budget with actuals Data:", budgetRes.data); // Debugging line to check budget data
      setSummary(summaryRes.data);
      setCategorySpending(categoryRes.data);
      setRecentTransactions(recentRes.data.slice(0, 5)); // Last 5 txns
      setBudgets(budgetRes.data);
      console.log(budgetRes.data);

      setGoals(goalRes.data);
    } catch (err) {
      console.error("Error loading dashboard:", err);
    }
  };
  const formatMonth = (ymString) => {
    const [year, month] = ymString.split("-");
    const date = new Date(year, month - 1); // JS months are 0-indexed
    return date.toLocaleString("en-US", { month: "short", year: "2-digit" }); // e.g., Aug 25
  };

  return (
    <>
      <div className="dashboard-wrapper">
        <div className="dashboard-container">
          <h2 style={{ textAlign: 'center' }}>Dashboard</h2>

          {/* Your existing sections go here without any major change */}

          {summary && (
            <div className="card-container">
              <div className="card" style={{ backgroundColor: '#d4edda' }}>
                <h4>Income</h4>
                <p>₹{summary.totalIncome}</p>
              </div>
              <div className="card" style={{ backgroundColor: '#f8d7da' }}>
                <h4>Expense</h4>
                <p>₹{summary.totalExpense}</p>
              </div>
              <div className="card" style={{ backgroundColor: '#fff3cd' }}>
                <h4>Balance</h4>
                <p>₹{summary.balance}</p>
              </div>
            </div>
          )}

          {/* Chart remains full width */}
          {categorySpending.length > 0 && (
            <div>
              <h3>📊 Spending by Category</h3>
              <CategoryPieChart data={categorySpending} />
            </div>
          )}

          {/* Wrap bottom 3 sections into a 3-column layout */}
          <div className="bottom-sections">
            <div>
              <h3>📉 Budget Usage</h3>
              {budgets.map((b, idx) => {
                const percent = (parseFloat(b.actualSpent) / parseFloat(b.limit)) * 100;
                const barColor = percent > 100 ? 'budget-fill-red' : 'budget-fill-green';
                return (
                  <div key={idx} style={{ marginBottom: '12px' }}>
                    <div>
                      <strong>{b.category}</strong> — ₹{b.actualSpent} / ₹{b.limit} ({formatMonth(b.month)})
                      {percent > 100 && <span style={{ color: 'red' }}> ⚠ Over Budget</span>}
                    </div>
                    <div className="budget-progress-bar">
                      <div
                        className={`budget-fill ${barColor}`}
                        style={{ width: `${Math.min(percent, 100)}%` }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>

            <div>
              <h3>🎯 Goal Progress</h3>
              {goals.map((g, idx) => {
                const percent = (parseFloat(g.currentAmount) / parseFloat(g.targetAmount)) * 100;
                return (
                  <div key={idx} style={{ marginBottom: '12px' }}>
                    <div>
                      <strong>{g.title}</strong> — ₹{g.currentAmount} / ₹{g.targetAmount} ({percent.toFixed(1)}%)
                    </div>
                    <div className="budget-progress-bar">
                      <div
                        className="budget-fill budget-fill-green"
                        style={{ width: `${Math.min(percent, 100)}%` }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>

            <div>
              <h3>🧾 Recent Transactions</h3>
              <table className="transactions-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Amount (₹)</th>
                    <th>Type</th>
                    <th>Category</th>
                  </tr>
                </thead>
                <tbody>
                  {recentTransactions.map((txn, idx) => (
                    <tr key={idx}>
                      <td>{txn.date}</td>
                      <td>{txn.amount}</td>
                      <td>{txn.type}</td>
                      <td>{txn.category}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div style={{ textAlign: "right", marginTop: "10px" }}>
              <Link to="/transactions" className="view-all-button">
                View All Transactions →
              </Link>
            </div>


          </div>
        </div>
      </div>
    </>
  );

};

export default DashboardPage;
