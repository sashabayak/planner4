import React, { useEffect, useState } from 'react';
import { Role, RoleCreateDto } from '../../types';

interface RoleFormProps {
    initialData?: Role;
    onSubmit: (data: RoleCreateDto) => void;
    onCancel: () => void;
    isLoading: boolean;
}

const RoleForm: React.FC<RoleFormProps> = ({
    initialData,
    onSubmit,
    onCancel,
    isLoading,
}) => {
    const [formData, setFormData] = useState<RoleCreateDto>({
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
        <form onSubmit={handleSubmit} className="space-y-4 bg-sky-100 p-6 rounded-xl border border-slate-500 shadow-sm">
            <div>
                <label htmlFor="name" className="block text-sm font-medium text-slate-600 mb-1">
                    Название роли *
                </label>
                <input
                    id="name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-white/50 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500 transition-all"
                    placeholder="Администратор"
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

export { RoleForm };