import React, { useEffect, useState } from "react";
import {
    getGoals,
    createGoal,
    cancelGoal,
    updateGoal,
} from "../services/goalService";
import "../styles/GoalsStyle.css";

const Goals = () => {
    const [goals, setGoals] = useState([]);
    const [form, setForm] = useState({
        title: "",
        goalDescription: "",
        targetAmount: "",
        deadline: "",
    });

    const [editGoalId, setEditGoalId] = useState(null);
    const [editForm, setEditForm] = useState({
        title: "",
        goalDescription: "",
        targetAmount: "",
        deadline: "",
    });

    const fetchGoals = async () => {
        try {
            const res = await getGoals();
            setGoals(res.data);
        } catch (err) {
            console.error("Error fetching goals", err);
        }
    };

    useEffect(() => {
        fetchGoals();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            await createGoal({
                ...form,
                targetAmount: parseFloat(form.targetAmount),
            });
            fetchGoals();
            setForm({
                title: "",
                goalDescription: "",
                targetAmount: "",
                deadline: "",
            });
        } catch (err) {
            console.error("Error creating goal", err);
        }
    };

    const handleCancel = async (id) => {
        try {
            await cancelGoal(id);
            fetchGoals();
        } catch (err) {
            console.error("Error cancelling goal", err);
        }
    };

    const startEdit = (goal) => {
        setEditGoalId(goal.id);
        setEditForm({
            title: goal.title,
            goalDescription: goal.goalDescription,
            targetAmount: goal.targetAmount,
            deadline: goal.deadline,
        });
    };

    const cancelEdit = () => {
        setEditGoalId(null);
        setEditForm({
            title: "",
            targetAmount: "",
            deadline: "",
        });
    };

    const handleSave = async (id) => {
        try {
            await updateGoal(id, {
                ...editForm,
                targetAmount: parseFloat(editForm.targetAmount),
            });
            setEditGoalId(null);
            fetchGoals();
        } catch (err) {
            if (err.response?.status === 400 && typeof err.response.data === "object") {
                const errorMap = err.response.data;
                const messageList = Object.entries(errorMap)
                    .map(([field, msg]) => `${field}: ${msg}`)
                    .join("\n");

                alert("Validation failed:\n" + messageList);
            } else {
                alert("Something went wrong. Please try again.");
            }
        }
    };

    return (
        <div className="goals-container">
            <h2 className="section-title">Set a New Financial Goal</h2>
            <form className="goal-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Title"
                    value={form.title}
                    onChange={(e) => setForm({ ...form, title: e.target.value })}
                    required
                />
                <input
                    type="text"
                    placeholder="Description"
                    value={form.goalDescription}
                    onChange={(e) => setForm({ ...form, goalDescription: e.target.value })}
                    required
                />
                <input
                    type="number"
                    placeholder="Target Amount"
                    value={form.targetAmount}
                    onChange={(e) => setForm({ ...form, targetAmount: e.target.value })}
                    required
                />
                <input
                    type="date"
                    placeholder="Enter Deadline"
                    value={form.deadline}
                    onChange={(e) => setForm({ ...form, deadline: e.target.value })}
                    required
                />
                <button type="submit" className="submit-button">Create Goal</button>
            </form>

            <h3 className="section-title">Active Goals</h3>
            <div className="table-container">
                <table className="goal-table">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Description</th>
                            <th>Target (₹)</th>
                            <th>Saved (₹)</th>
                            <th>Progress</th>
                            <th>Deadline</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {goals.length === 0 ? (
                            <tr><td colSpan="7" className="no-data">No goals found</td></tr>
                        ) : (
                            goals.map((goal) => (
                                <tr key={goal.id}>
                                    {editGoalId === goal.id ? (
                                        <>
                                            <td>
                                                <input
                                                    value={editForm.title}
                                                    onChange={(e) =>
                                                        setEditForm({ ...editForm, title: e.target.value })
                                                    }
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="text"
                                                    value={editForm.goalDescription}
                                                    onChange={(e) =>
                                                        setEditForm({ ...editForm, goalDescription: e.target.value })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    type="number"
                                                    value={editForm.targetAmount}
                                                    onChange={(e) =>
                                                        setEditForm({
                                                            ...editForm,
                                                            targetAmount: e.target.value,
                                                        })
                                                    }
                                                />
                                            </td>
                                            <td>{goal.currentAmount}</td>
                                            <td>
                                                <div className="progress-container">
                                                    <div
                                                        className="progress-bar"
                                                        style={{ width: `${goal.progressPercentage}%` }}
                                                    />
                                                    <span className="progress-text">
                                                        {goal.progressPercentage.toFixed(0)}%
                                                    </span>
                                                </div>
                                            </td>
                                            <td>
                                                <input
                                                    type="date"
                                                    value={editForm.deadline}
                                                    onChange={(e) =>
                                                        setEditForm({
                                                            ...editForm,
                                                            deadline: e.target.value,
                                                        })
                                                    }
                                                />
                                            </td>
                                            <td>{goal.status}</td>
                                            <td>
                                                <button onClick={() => handleSave(goal.id)} className="save-button">Save</button>
                                                <button onClick={cancelEdit} className="cancel-button">Cancel</button>
                                            </td>
                                        </>
                                    ) : (
                                        <>
                                            <td>{goal.title}</td>
                                            <td>{goal.goalDescription}</td>
                                            <td>{goal.targetAmount}</td>
                                            <td>{goal.currentAmount}</td>
                                            <td>
                                                <div className="progress-container">
                                                    <div
                                                        className="progress-bar"
                                                        style={{ width: `${goal.progressPercentage}%` }}
                                                    />
                                                    <span className="progress-text">
                                                        {goal.progressPercentage.toFixed(0)}%
                                                    </span>
                                                </div>
                                            </td>
                                            <td>{goal.deadline}</td>
                                            <td>{goal.status}</td>
                                            <td>
                                                {goal.status === "ACTIVE" ? (
                                                    <>
                                                        <button onClick={() => startEdit(goal)} className="edit-button">Edit</button>
                                                        <button onClick={() => handleCancel(goal.id)} className="cancel-button">Cancel</button>
                                                    </>
                                                ) : (
                                                    "-"
                                                )}
                                            </td>
                                        </>
                                    )}
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Goals;
