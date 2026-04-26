// src/components/items/ItemForm.tsx
import React, { useEffect, useState } from 'react';
import { Item, ItemCreateDto, User, Tag } from '../../types';

interface ItemFormProps {
    initialData?: Item;
    users?: User[];
    tags?: Tag[];
    onSubmit: (data: ItemCreateDto) => void;
    onCancel: () => void;
    isLoading: boolean;
}

const ItemForm: React.FC<ItemFormProps> = ({
    initialData,
    users = [],
    tags = [],
    onSubmit,
    onCancel,
    isLoading,
}) => {
    const [formData, setFormData] = useState<ItemCreateDto>({
        name: '',
        description: '',
        completed: false,
        userIds: [],
        tagIds: [],
    });

    useEffect(() => {
        if (initialData) {
            setFormData({
                name: initialData.name,
                description: initialData.description || '',
                completed: initialData.completed,
                userIds: (initialData.users || []).map(u => u.id),
                tagIds: (initialData.tags || []).map(t => t.id),
            });
        }
    }, [initialData]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    const toggleUser = (userId: number) => {
        setFormData(prev => ({
            ...prev,
            userIds: prev.userIds?.includes(userId)
                ? prev.userIds.filter(id => id !== userId)
                : [...(prev.userIds || []), userId],
        }));
    };

    const toggleTag = (tagId: number) => {
        setFormData(prev => ({
            ...prev,
            tagIds: prev.tagIds?.includes(tagId)
                ? prev.tagIds.filter(id => id !== tagId)
                : [...(prev.tagIds || []), tagId],
        }));
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <label className="block text-sm font-medium text-slate-600 mb-1">Название *</label>
                <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={e => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-white/50 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500"
                    placeholder="Введите название"
                />
            </div>

            <div>
                <label className="block text-sm font-medium text-slate-600 mb-1">Описание</label>
                <textarea
                    rows={3}
                    value={formData.description}
                    onChange={e => setFormData({ ...formData, description: e.target.value })}
                    className="w-full px-3 py-2 bg-white/50 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500"
                    placeholder="Описание задачи"
                />
            </div>

            <div className="flex items-center gap-2">
                <input
                    type="checkbox"
                    id="completed"
                    checked={formData.completed}
                    onChange={e => setFormData({ ...formData, completed: e.target.checked })}
                    className="accent-slate-600"
                />
                <label htmlFor="completed" className="text-sm text-slate-600">Задача выполнена</label>
            </div>

            {users.length > 0 && (
                <div>
                    <label className="block text-sm font-medium text-slate-600 mb-2">Назначить пользователей</label>
                    <div className="flex flex-wrap gap-2 max-h-32 overflow-y-auto p-2 border border-slate-300 rounded-lg bg-white/50">
                        {users.map(user => (
                            <button
                                key={user.id}
                                type="button"
                                onClick={() => toggleUser(user.id)}
                                className={`px-2 py-1 rounded-full text-xs transition ${
                                    formData.userIds?.includes(user.id)
                                        ? 'bg-slate-600 text-white'
                                        : 'bg-slate-200 text-slate-700 hover:bg-slate-300'
                                }`}
                            >
                                {user.name}
                            </button>
                        ))}
                    </div>
                </div>
            )}

            {tags.length > 0 && (
                <div>
                    <label className="block text-sm font-medium text-slate-600 mb-2">Теги</label>
                    <div className="flex flex-wrap gap-2 max-h-40 overflow-y-auto p-2 border border-slate-300 rounded-lg bg-white/50">
                        {tags.map(tag => (
                            <button
                                key={tag.id}
                                type="button"
                                onClick={() => toggleTag(tag.id)}
                                className={`px-2 py-1 rounded-full text-xs transition ${
                                    formData.tagIds?.includes(tag.id)
                                        ? 'bg-slate-600 text-white'
                                        : 'bg-slate-200 text-slate-700 hover:bg-slate-300'
                                }`}
                            >
                                #{tag.name}
                            </button>
                        ))}
                    </div>
                </div>
            )}

            <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={onCancel} className="px-4 py-2 bg-red-500/20 text-red-600 rounded-lg hover:bg-red-500/30">Отмена</button>
                <button type="submit" disabled={isLoading} className="px-4 py-2 bg-slate-600 text-white rounded-lg hover:bg-slate-700 disabled:opacity-50">
                    {isLoading ? 'Сохранение...' : 'Сохранить'}
                </button>
            </div>
        </form>
    );
};

export { ItemForm };