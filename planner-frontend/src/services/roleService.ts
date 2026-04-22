import api from './api';
import { Role, RoleCreateDto } from '../types';

export const roleService = {
    getAll: async (): Promise<Role[]> => {
        const response = await api.get('/roles');
        return response.data;
    },

    getById: async (id: number): Promise<Role> => {
        const response = await api.get(`/roles/${id}`);
        return response.data;
    },

    create: async (data: RoleCreateDto): Promise<Role> => {
        const response = await api.post('/roles', data);
        return response.data;
    },

    update: async (id: number, data: RoleCreateDto): Promise<Role> => {
        const response = await api.put(`/roles/${id}`, data);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/roles/${id}`);
    },
};