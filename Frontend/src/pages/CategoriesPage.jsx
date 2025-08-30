import React, { useEffect, useState } from 'react';
import {
    getAllCategories,
    createCategory,
    updateCategory,
    deleteCategory
} from '../services/categoryService';
import '../styles/CategoriesPage.css';

const AdminCategoryPage = () => {
    const [categories, setCategories] = useState([]);
    const [form, setForm] = useState({ name: '', type: 'EXPENSE' });
    const [editingId, setEditingId] = useState(null);
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchCategories();
    }, []);

    useEffect(() => {
        if (message) {
            const timer = setTimeout(() => setMessage(''), 3000); // clears message after 3 seconds
            return () => clearTimeout(timer); // cleanup on unmount/change
        }
    }, [message]);


    const fetchCategories = async () => {
        try {
            const res = await getAllCategories();
            setCategories(res.data);
        } catch (err) {
            console.error('Error fetching categories:', err);
        }
    };

    const handleChange = e => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async e => {
        e.preventDefault();
        try {
            if (editingId) {
                await updateCategory(editingId, form);
                setMessage('Category updated successfully');
            } else {
                await createCategory(form);
                setMessage('Category added successfully');
            }
            setForm({ name: '', type: 'EXPENSE' });
            setEditingId(null);
            fetchCategories();
        } catch (err) {
            console.error(err);
            setMessage('Something went wrong');
        }
    };

    const handleEdit = cat => {
        setForm({ name: cat.name, type: cat.type });
        setEditingId(cat.id);
        setMessage('');
    };

    const handleDelete = async id => {
        try {
            await deleteCategory(id);
            fetchCategories();
        } catch (err) {
            console.error(err);
            setMessage('Failed to delete');
        }
    };

    return (
        <div className="admin-category-page">
            <h2>Manage Categories</h2>

            {message && <div className="info-msg">{message}</div>}

            <form onSubmit={handleSubmit} className="category-form">
                <input
                    type="text"
                    name="name"
                    placeholder="Category name (e.g. Groceries)"
                    value={form.name}
                    onChange={handleChange}
                    required
                />

                <select name="type" value={form.type} onChange={handleChange}>
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                </select>

                <button type="submit">
                    {editingId ? 'Update Category' : 'Add Category'}
                </button>
            </form>

            <div className="category-table-wrapper">
                <h3>Existing Categories</h3>
                <table className="category-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {categories.map(cat => (
                            <tr key={cat.id}>
                                <td>{cat.name}</td>
                                <td>{cat.type}</td>
                                <td>
                                    <button className="edit-btn" onClick={() => handleEdit(cat)}>Edit</button>
                                    <button className="delete-btn" onClick={() => handleDelete(cat.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                        {categories.length === 0 && (
                            <tr><td colSpan="3">No categories found.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminCategoryPage;
