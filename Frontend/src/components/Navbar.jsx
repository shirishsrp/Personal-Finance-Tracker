import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext.jsx';
import '../styles/NavbarStyle.css';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const role = user?.authorities?.[0];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <div className="navbar-brand">
          💰 <Link to={role === 'ROLE_ADMIN' ? '/admin' : '/dashboard'} className="navbar-link">Finance Tracker</Link>
        </div>

        {role === 'ROLE_ADMIN' && (
          <div className="navbar-tabs">
            <Link to="/admin/categories" className="navbar-tab">Manage Categories</Link>
            {/* Add more admin tabs here */}
          </div>
        )}

        {role === 'ROLE_USER' && (
          <div className="navbar-tabs">
            <Link to="/transactions" className="navbar-tab">Manage Transactions</Link>
            <Link to="/goals" className="navbar-tab">Manage Goals</Link>
            <Link to="/budgets" className="navbar-tab">Manage Budgets</Link>

            {/* Add more user tabs here */}
          </div>
        )}
      </div>

      <div className="navbar-right">
        <span className="navbar-username">{user?.sub || user?.username}</span>
        <button onClick={handleLogout} className="navbar-logout">Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;
