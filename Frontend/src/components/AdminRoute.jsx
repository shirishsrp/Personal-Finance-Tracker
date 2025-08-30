import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const AdminRoute = ({ children }) => {
  const { token, user, loading } = useContext(AuthContext);
  const role = user?.authorities?.[0];

  if (loading) {
    return <p>Loading...</p>; // or a spinner
  }

  if (!token) {
    return <Navigate to="/login" />;  // not logged in
  }

  if (role !== 'ROLE_ADMIN') {
    return <Navigate to="/unauthorized" />;  // logged in, but not admin
  }

  return children;
};

export default AdminRoute;
