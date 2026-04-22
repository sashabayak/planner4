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
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <label htmlFor="name" className="block text-sm font-medium text-gray-300 mb-1">
                    Имя *
                </label>
                <input
                    id="name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                    placeholder="Иван Иванов"
                />
            </div>

            <div>
                <label htmlFor="birthDate" className="block text-sm font-medium text-gray-300 mb-1">
                    Дата рождения *
                </label>
                <input
                    id="birthDate"
                    type="date"
                    required
                    value={formData.birthDate}
                    onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                    className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                />
            </div>

            <div>
                <label htmlFor="roleId" className="block text-sm font-medium text-gray-300 mb-1">
                    Роль *
                </label>
                <select
                    id="roleId"
                    required
                    value={formData.roleId}
                    onChange={(e) => setFormData({ ...formData, roleId: Number(e.target.value) })}
                    className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                >
                    <option value={0}>Выберите роль</option>
                    {roles.map((role) => (
                        <option key={role.id} value={role.id}>
                            {role.name}
                        </option>
                    ))}
                </select>
            </div>

            <div>
                <label htmlFor="groupId" className="block text-sm font-medium text-gray-300 mb-1">
                    Группа *
                </label>
                <select
                    id="groupId"
                    required
                    value={formData.groupId}
                    onChange={(e) => setFormData({ ...formData, groupId: Number(e.target.value) })}
                    className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                >
                    <option value={0}>Выберите группу</option>
                    {groups.map((group) => (
                        <option key={group.id} value={group.id}>
                            {group.name}
                        </option>
                    ))}
                </select>
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

export { UserForm };