import React, { useEffect, useState } from 'react';
import { Item, ItemCreateDto } from '../../types';

interface ItemFormProps {
    initialData?: Item;
    onSubmit: (data: ItemCreateDto) => void;
    onCancel: () => void;
    isLoading: boolean;
}

const ItemForm: React.FC<ItemFormProps> = ({
    initialData,
    onSubmit,
    onCancel,
    isLoading,
}) => {
    const [formData, setFormData] = useState<ItemCreateDto>({
        name: '',
        description: '',
        completed: false,
    });

    useEffect(() => {
        if (initialData) {
            setFormData({
                name: initialData.name,
                description: initialData.description || '',
                completed: initialData.completed,
            });
        }
    }, [initialData]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4 bg-sky-100 p-6 rounded-xl border border-slate-500 shadow-sm">
            <div>
                <label htmlFor="name" className="block text-sm font-medium text-slate-600 mb-1">
                    Название задачи *
                </label>
                <input
                    id="name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-white/80 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500 transition-all"
                    placeholder="Разработать дизайн"
                />
            </div>

            <div>
                <label htmlFor="description" className="block text-sm font-medium text-slate-600 mb-1">
                    Описание
                </label>
                <textarea
                    id="description"
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    rows={3}
                    className="w-full px-3 py-2 bg-white/80 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500 transition-all"
                    placeholder="Подробное описание задачи..."
                />
            </div>

            <div className="flex justify-end gap-3 pt-4">
                <button
                    type="button"
                    onClick={onCancel}
                    className="px-4 py-2 bg-red-500/20 text-red-600 rounded-lg hover:bg-red-500/30 transition"
                >
                    Отмена
                </button>
                <button
                    type="submit"
                    disabled={isLoading}
                    className="px-4 py-2 bg-slate-600 hover:bg-slate-700 text-white rounded-lg transition disabled:opacity-50"
                >
                    {isLoading ? 'Сохранение...' : 'Сохранить'}
                </button>
            </div>
        </form>
    );
};

export { ItemForm };