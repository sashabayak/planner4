import axios from 'axios';
import toast from 'react-hot-toast';

const api = axios.create({
    baseURL: 'http://localhost:8081/api',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const message = error.response?.data?.message || error.response?.data || error.message;

        if (error.response?.status === 404) {
            toast.error('Ресурс не найден');
        } else if (error.response?.status === 500) {
            toast.error('Внутренняя ошибка сервера');
        } else if (message) {
            toast.error(message);
        }

        return Promise.reject(error);
    }
);

export default api;