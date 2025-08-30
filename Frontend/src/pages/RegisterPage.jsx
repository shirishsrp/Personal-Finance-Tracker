import React, { useState } from 'react';
import { registerUser } from '../services/authService';
import { useNavigate } from 'react-router-dom';
import '../index.css';
import { Link } from 'react-router-dom';

const RegisterPage = () => {
    const [form, setForm] = useState({ username: '', email: '', password: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = e => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async e => {
        e.preventDefault();
        setError('');

        try {
            await registerUser(form);
            navigate('/login');
        } catch (err) {
            const data = err.response?.data;

            // If it's a field-level error object
            if (data && typeof data === 'object' && !Array.isArray(data)) {
                // Combine all messages into a single string
                const messages = Object.values(data).join('\n');
                setError(messages);
            } else {
                setError(data?.message || 'Registration failed');
            }
        }
    };

    return (
        <div className="container">
            <h2>Create Account</h2>
            <p style={{ textAlign: 'center' }}>Sign up to track your finances</p>
            {error && <div className="error">{error}</div>}
            <form onSubmit={handleSubmit}>
                <input
                    name="username"
                    type="text"
                    placeholder="Enter your name"
                    onChange={handleChange}
                    required
                />
                <input
                    name="email"
                    type="email"
                    placeholder="Enter your email"
                    onChange={handleChange}
                    required
                />
                <input
                    name="password"
                    type="password"
                    placeholder="Enter a strong password"
                    onChange={handleChange}
                    required
                />
                <button type="submit">Sign Up</button>
            </form>
            <div className="small-text">
                Already have an account? <Link to="/login">Sign in</Link>
            </div>
        </div>
    );
};

export default RegisterPage;
