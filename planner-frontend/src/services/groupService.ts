import api from './api';
import { Group, GroupCreateDto } from '../types';

export const groupService = {
    getAll: async (): Promise<Group[]> => {
        const response = await api.get('/groups');
        return response.data;
    },

    getById: async (id: number): Promise<Group> => {
        const response = await api.get(`/groups/${id}`);
        return response.data;
    },

    create: async (data: GroupCreateDto): Promise<Group> => {
        const response = await api.post('/groups', data);
        return response.data;
    },

    update: async (id: number, data: GroupCreateDto): Promise<Group> => {
        const response = await api.put(`/groups/${id}`, data);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/groups/${id}`);
    },
};