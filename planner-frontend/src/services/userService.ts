import api from './api';
import { User, UserCreateDto, UserUpdateDto } from '../types';

export const userService = {
    getAll: async (): Promise<User[]> => {
        const response = await api.get('/users');
        return response.data;
    },

    getById: async (id: number): Promise<User> => {
        const response = await api.get(`/users/${id}`);
            console.log("GET /users/${id} response:", response.data);
        return response.data;
    },

    getByGroupId: async (groupId: number): Promise<User[]> => {
        const response = await api.get(`/users/group/${groupId}`);
        return response.data;
    },

    create: async (data: UserCreateDto): Promise<User> => {
        const response = await api.post('/users', data);
        return response.data;
    },

    update: async (id: number, data: UserUpdateDto): Promise<User> => {
        const response = await api.put(`/users/${id}`, data);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/users/${id}`);
    },

    addItemToUser: async (userId: number, itemId: number): Promise<void> => {
        console.log(`userService.addItemToUser: POST /users/${userId}/items/${itemId}`);
        const response = await api.post(`/users/${userId}/items/${itemId}`);
        console.log("Ответ:", response.status);
        return response.data;
    },

    removeItemFromUser: async (userId: number, itemId: number): Promise<void> => {
        console.log(`userService.removeItemFromUser: DELETE /users/${userId}/items/${itemId}`);
        const response = await api.delete(`/users/${userId}/items/${itemId}`);
        console.log("Ответ:", response.status);
        return response.data;
    },
};