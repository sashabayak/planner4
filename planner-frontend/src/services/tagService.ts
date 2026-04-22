import api from './api';
import { Tag } from '../types';

export const tagService = {
    getAll: async (): Promise<Tag[]> => {
        const response = await api.get('/tags');
        return response.data;
    },

    getById: async (id: number): Promise<Tag> => {
        const response = await api.get(`/tags/${id}`);
        return response.data;
    },

    create: async (data: { name: string }): Promise<Tag> => {
        const response = await api.post('/tags', data);
        return response.data;
    },

    update: async (id: number, data: { name: string }): Promise<Tag> => {
        const response = await api.put(`/tags/${id}`, data);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/tags/${id}`);
    },
};