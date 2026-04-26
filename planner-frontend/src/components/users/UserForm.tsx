import React, { useEffect, useState } from 'react';
import { User, UserCreateDto, Group, Role } from '../../types';

interface UserFormProps {
    initialData?: User;
    groups: Group[];
    roles: Role[];
    onSubmit: (data: UserCreateDto) => void;
    onCancel: () => void;
    isLoading: boolean;
}

const UserForm: React.FC<UserFormProps> = ({
    initialData,
    groups,
    roles,
    onSubmit,
    onCancel,
    isLoading,
}) => {
    const [formData, setFormData] = useState<UserCreateDto>({
        name: '',
        birthDate: '',
        roleId: 0,
        groupId: 0,
    });

    useEffect(() => {
        if (initialData) {
            setFormData({
                name: initialData.name,
                birthDate: initialData.birthDate.split('T')[0],
                roleId: initialData.roleId,
                groupId: initialData.groupId,
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
                    Имя *
                </label>
                <input
                    id="name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-white/50 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500 transition-all"
                    placeholder="Иван Иванов"
                />
            </div>

            <div>
                <label htmlFor="birthDate" className="block text-sm font-medium text-slate-600 mb-1">
                    Дата рождения *
                </label>
                <input
                    id="birthDate"
                    type="date"
                    required
                    value={formData.birthDate}
                    onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                    className="w-full px-3 py-2 bg-white/50 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500 transition-all"
                />
            </div>

            <div>
                <label className="block text-sm font-medium text-slate-600 mb-2">
                    Роль *
                </label>
                <div className="grid grid-cols-2 gap-2 max-h-40 overflow-y-auto p-2 bg-white/50 rounded-lg border border-slate-300">
                    <button
                        type="button"
                        onClick={() => setFormData({ ...formData, roleId: 0 })}
                        className={`px-3 py-2 rounded-lg text-sm transition ${
                            formData.roleId === 0
                                ? 'bg-slate-600 text-white'
                                : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                        }`}
                    >
                        Не выбрано
                    </button>
                    {roles.map((role) => (
                        <button
                            key={role.id}
                            type="button"
                            onClick={() => setFormData({ ...formData, roleId: role.id })}
                            className={`px-3 py-2 rounded-lg text-sm transition ${
                                formData.roleId === role.id
                                    ? 'bg-slate-600 text-white'
                                    : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                            }`}
                        >
                            {role.name}
                        </button>
                    ))}
                </div>
            </div>

            <div>
                <label className="block text-sm font-medium text-slate-600 mb-2">
                    Группа *
                </label>
                <div className="grid grid-cols-2 gap-2 max-h-40 overflow-y-auto p-2 bg-white/50 rounded-lg border border-slate-300">
                    <button
                        type="button"
                        onClick={() => setFormData({ ...formData, groupId: 0 })}
                        className={`px-3 py-2 rounded-lg text-sm transition ${
                            formData.groupId === 0
                                ? 'bg-slate-600 text-white'
                                : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                        }`}
                    >
                        Не выбрано
                    </button>
                    {groups.map((group) => (
                        <button
                            key={group.id}
                            type="button"
                            onClick={() => setFormData({ ...formData, groupId: group.id })}
                            className={`px-3 py-2 rounded-lg text-sm transition ${
                                formData.groupId === group.id
                                    ? 'bg-slate-600 text-white'
                                    : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                            }`}
                        >
                            {group.name}
                        </button>
                    ))}
                </div>
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

export { UserForm };