import React, { useEffect, useState } from "react";
import { getUserStats, getTransactionSummary, getTopSpenders, getTopExpenseCategories } from "../services/adminService";
import "../styles/AdminDashboard.css";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';



const AdminDashboard = () => {
    const [userStats, setUserStats] = useState(null);
    const [txnStats, setTxnStats] = useState(null);
    const [topSpenders, setTopSpenders] = useState([]);
    const [topCategories, setTopCategories] = useState([]);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const [userRes, txnRes, spenderRes, categoryRes] = await Promise.all([
                    getUserStats(),
                    getTransactionSummary(),
                    getTopSpenders(),
                    getTopExpenseCategories()
                ]);
                setUserStats(userRes.data);
                setTxnStats(txnRes.data);
                setTopSpenders(spenderRes.data);
                setTopCategories(categoryRes.data);
            } catch (err) {
                console.error("Error fetching admin dashboard data", err);
            }
        };

        fetchStats();
    }, []);

    return (
        <div className="admin-dashboard-container">
            <h2 className="admin-dashboard-title">Admin Dashboard</h2>
            <div className="dashboard-card-wrapper">

                <div className="dashboard-card">
                    <h3>👥 User Statistics</h3>
                    {userStats ? (
                        <ul>
                            <li><strong>Total Users:</strong> {userStats.totalUsers}</li>
                            <li><strong>Admins:</strong> {userStats.totalAdmins}</li>
                            <li><strong>Standard Users:</strong> {userStats.totalStandardUsers}</li>
                        </ul>
                    ) : (
                        <p className="loading-text">Loading...</p>
                    )}
                </div>

                <div className="dashboard-card">
                    <h3>📊 Transaction Summary</h3>
                    {txnStats ? (
                        <ul>
                            <li><strong>Total Transactions:</strong> {txnStats.totalTransactions}</li>
                            <li><strong>Total Income:</strong> ₹{txnStats.totalIncome}</li>
                            <li><strong>Total Expenses:</strong> ₹{txnStats.totalExpense}</li>
                            <li><strong>Avg Transactions/User:</strong> {txnStats.avgTransactionsPerUser.toFixed(2)}</li>
                        </ul>
                    ) : (
                        <p className="loading-text">Loading...</p>
                    )}
                </div>

                <div className="dashboard-card">
                    <h3>🏆 Top 5 Spenders</h3>
                    {topSpenders.length > 0 ? (
                        <table className="top-spenders-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>User Email</th>
                                    <th>Total Spent (₹)</th>
                                </tr>
                            </thead>
                            <tbody>
                                {topSpenders.map((spender, index) => (
                                    <tr key={spender.email}>
                                        <td>{index + 1}</td>
                                        <td>{spender.email}</td>
                                        <td>{spender.totalSpent}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No spending data available</p>
                    )}
                </div>


                <div className="dashboard-card">
                    <h3>📌 Top Expense Categories</h3>
                    {topCategories.length === 0 ? (
                        <p>Loading...</p>
                    ) : (
                        <ResponsiveContainer width="100%" height={300}>
                            <BarChart
                                data={topCategories}
                                margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                            >
                                <XAxis dataKey="categoryName" />
                                <YAxis />
                                <Tooltip />
                                <Bar dataKey="totalSpent" fill="#8884d8" />
                            </BarChart>
                        </ResponsiveContainer>
                    )}
                </div>


            </div>
        </div>
    );
};

export default AdminDashboard;
