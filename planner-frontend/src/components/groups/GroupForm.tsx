import React, { useEffect, useState } from 'react';
import { Group, GroupCreateDto } from '../../types';

interface GroupFormProps {
    initialData?: Group;
    onSubmit: (data: GroupCreateDto) => void;
    onCancel: () => void;
    isLoading: boolean;
}

const GroupForm: React.FC<GroupFormProps> = ({
    initialData,
    onSubmit,
    onCancel,
    isLoading,
}) => {
    const [formData, setFormData] = useState<GroupCreateDto>({
        name: '',
    });

    useEffect(() => {
        if (initialData) {
            setFormData({ name: initialData.name });
        }
    }, [initialData]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <label htmlFor="name" className="block text-sm font-medium text-gray-300 mb-1">
                    Название группы *
                </label>
                <input
                    id="name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                    placeholder="Администраторы"
                />
            </div>

            <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={onCancel} className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition">
                    Отмена
                </button>
                <button type="submit" disabled={isLoading} className="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg hover:opacity-90 transition disabled:opacity-50">
                    {isLoading ? 'Сохранение...' : 'Сохранить'}
                </button>
            </div>
        </form>
    );
};

export { GroupForm };