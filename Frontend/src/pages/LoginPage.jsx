import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext.jsx';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../services/authService';
import '../index.css';
import { Link } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const LoginPage = () => {
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const [form, setForm] = useState({ email: '', password: '' });
    const [error, setError] = useState('');

    const handleChange = e => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async e => {
        e.preventDefault();
        setError('');

        try {
            const res = await loginUser(form);
            const token = res.data.token;
            if (token) {
                login(token);  // save token in context
                const decoded = jwtDecode(token);
                const role = decoded?.authorities?.[0];
                console.log(role);
                if (role === 'ROLE_ADMIN') {
                    console.log("User is an admin");
                    navigate('/admin');
                } else {
                    console.log("User is a regular user");
                    navigate('/dashboard');
                }
            } else {
                setError('Login failed: No token received');
            }
        } catch (err) {
            console.log();
            
            setError(err.response?.data?.message || 'Login failed');
        }
    };

    return (
        <div className="container">
            <h2>Welcome Back</h2>
            <p style={{ textAlign: 'center' }}>Sign in to your finance tracker</p>
            {error && <div className="error">{error}</div>}
            <form onSubmit={handleSubmit}>
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
                    placeholder="Enter your password"
                    onChange={handleChange}
                    required
                />
                <button type="submit">Sign In</button>
            </form>
            <div className="small-text">
                Don't have an account? <Link to="/register">Sign up</Link>
            </div>
        </div>
    );
};

export default LoginPage;
