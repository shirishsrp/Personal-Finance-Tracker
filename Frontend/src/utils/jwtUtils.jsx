import { jwtDecode } from 'jwt-decode';

export const getUserFromToken = (token) => {
    try {
        return jwtDecode(token);
    } catch (err) {
        return null;
    }
};
