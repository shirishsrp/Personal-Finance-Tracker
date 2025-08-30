import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

export const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState({
    token: localStorage.getItem('token'),
    user: null,
    loading: true,
  });

  useEffect(() => {
    const token = auth.token;
    if (token) {
      try {
        const decoded = jwtDecode(token);
        console.log(decoded)
        setAuth(prev => ({ ...prev, user: decoded,  loading: false }));
      } catch (err) {
        console.error('Invalid token');
        logout();
      }
    }else {
      setAuth((prev) => ({ ...prev, loading: false })); // ✅ no token, but still ready
    }
  }, [auth.token]);

  const login = (token) => {
    localStorage.setItem('token', token);
    const decoded = jwtDecode(token);
    console.log(decoded)
    setAuth({ token, user: decoded , loading: false });
  };

  const logout = () => {
    localStorage.removeItem('token');
    setAuth({ token: null, user: null, loading: false });
    window.location.href = '/login';
  };

  return (
    <AuthContext.Provider value={{ ...auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
