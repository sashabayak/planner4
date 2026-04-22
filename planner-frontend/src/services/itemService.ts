import api from './api';
import { Item, ItemCreateDto, ItemUpdateDto } from '../types';

export const itemService = {
    getAll: async (): Promise<Item[]> => {
        const response = await api.get('/items');
        return response.data;
    },

    getById: async (id: number): Promise<Item> => {
        const response = await api.get(`/items/${id}`);
        return response.data;
    },

    create: async (data: ItemCreateDto): Promise<Item> => {
        const response = await api.post('/items', data);
        return response.data;
    },

    update: async (id: number, data: ItemUpdateDto): Promise<Item> => {
        const response = await api.put(`/items/${id}`, data);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/items/${id}`);
    },
 addTagToItem: async (itemId: number, tagId: number): Promise<void> => {
        await api.post(`/items/${itemId}/tags/${tagId}`);
    },

    removeTagFromItem: async (itemId: number, tagId: number): Promise<void> => {
        await api.delete(`/items/${itemId}/tags/${tagId}`);
    },

    getTagsByItemId: async (itemId: number): Promise<Tag[]> => {
        const response = await api.get(`/items/${itemId}/tags`);
        return response.data;
    },
};