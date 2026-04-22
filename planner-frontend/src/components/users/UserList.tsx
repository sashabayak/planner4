import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { userService } from '../../services/userService';
import { groupService } from '../../services/groupService';
import { roleService } from '../../services/roleService';
import { User, UserCreateDto, Group, Role, Item } from '../../types';import { useToast } from '../../hooks/useToast';
import LoadingSpinner from '../common/LoadingSpinner';
import { Modal } from '../common/Modal';
import { UserForm } from './UserForm';
import { itemService } from '../../services/itemService';
import {
    Plus,
    Pencil,
    Trash2,
    Users,
    Calendar,
    Briefcase,
    BadgeCheck,
    CheckSquare,
    Search,
    X
} from 'lucide-react';

const UserList: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [groupFilter, setGroupFilter] = useState('all');
    const [roleFilter, setRoleFilter] = useState('all');
    const [showUserItemsModal, setShowUserItemsModal] = useState(false);
    const [selectedUserForItems, setSelectedUserForItems] = useState<User | null>(null);
    const [userItems, setUserItems] = useState<Item[]>([]);
    const [allItems, setAllItems] = useState<Item[]>([]);
    const [selectedItemIds, setSelectedItemIds] = useState<number[]>([]);
    const [formData, setFormData] = useState<UserCreateDto>({
        name: '',
        birthDate: '',
        roleId: 0,
        groupId: 0,
    });
    const [editingId, setEditingId] = useState<number | null>(null);

    const { showSuccess, showError } = useToast();
    const queryClient = useQueryClient();

    const { data: users, isLoading: usersLoading } = useQuery<User[]>({
        queryKey: ['users'],
        queryFn: userService.getAll,
    });

    const { data: groups } = useQuery<Group[]>({
        queryKey: ['groups'],
        queryFn: groupService.getAll,
    });

    const { data: roles } = useQuery<Role[]>({
        queryKey: ['roles'],
        queryFn: roleService.getAll,
    });

    const { data: items } = useQuery<Item[]>({
        queryKey: ['items'],
        queryFn: itemService.getAll,
    });

    const createMutation = useMutation({
        mutationFn: userService.create,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['users'] });
            showSuccess('Пользователь успешно создан');
            closeModal();
        },
        onError: (error: Error) => {
            showError(error.message || 'Ошибка при создании пользователя');
        },
    });

    const updateMutation = useMutation({
        mutationFn: ({ id, data }: { id: number; data: UserCreateDto }) =>
            userService.update(id, data),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['users'] });
            showSuccess('Пользователь успешно обновлен');
            closeModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: userService.delete,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['users'] });
            showSuccess('Пользователь успешно удален');
        },
    });

    const openCreateModal = () => {
        setEditingId(null);
        setFormData({ name: '', birthDate: '', roleId: 0, groupId: 0 });
        setIsModalOpen(true);
    };

    const openEditModal = (user: User) => {
        setEditingId(user.id);
        setFormData({
            name: user.name,
            birthDate: user.birthDate.split('T')[0],
            roleId: user.roleId,
            groupId: user.groupId,
        });
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingId(null);
        setSelectedUser(null);
    };

const showUserItems = async (user: User) => {
    console.log("=== showUserItems НАЧАЛО ===");
    console.log("user.id:", user.id);

    try {
        const freshUser = await userService.getById(user.id);
        console.log("freshUser:", freshUser);
        console.log("freshUser.items:", freshUser.items);

        setSelectedUserForItems(freshUser);
        setUserItems(freshUser.items || []);
        setSelectedItemIds((freshUser.items || []).map(i => i.id));
        setAllItems(items || []);
        setShowUserItemsModal(true);
    } catch (error) {
        console.error("Ошибка при загрузке пользователя:", error);
        // Fallback - используем то, что есть
        setSelectedUserForItems(user);
        setUserItems(user.items || []);
        setSelectedItemIds((user.items || []).map(i => i.id));
        setAllItems(items || []);
        setShowUserItemsModal(true);
    }
};

const saveUserItems = async () => {
    if (!selectedUserForItems) return;

    console.log("=== saveUserItems ПРИНУДИТЕЛЬНОЕ ДОБАВЛЕНИЕ ===");
    console.log("selectedItemIds:", selectedItemIds);

    try {
        for (const itemId of selectedItemIds) {
            console.log(`ПРИНУДИТЕЛЬНО добавляем задачу ${itemId} пользователю ${selectedUserForItems.id}`);
            await userService.addItemToUser(selectedUserForItems.id, itemId);
        }

        showSuccess('Задачи пользователя обновлены');
        setShowUserItemsModal(false);
        queryClient.invalidateQueries({ queryKey: ['users'] });
        queryClient.invalidateQueries({ queryKey: ['items'] });
    } catch (error) {
        console.error("Ошибка:", error);
        showError('Ошибка при сохранении');
    }
};

const addItemToUser = (itemId: number) => {
    console.log("addItemToUser вызван с itemId:", itemId);
    const item = allItems.find(i => i.id === itemId);
    console.log("Найденный item:", item);

    if (item && !userItems.some(i => i.id === itemId)) {
        console.log("Добавляем задачу в локальный список");
        setUserItems([...userItems, item]);
        setSelectedItemIds([...selectedItemIds, itemId]);
    } else {
        console.log("Задача не добавлена (уже есть или не найдена)");
    }
};

const removeItemFromUser = (itemId: number) => {
    console.log("removeItemFromUser вызван с itemId:", itemId);
    setUserItems(userItems.filter(i => i.id !== itemId));
    setSelectedItemIds(selectedItemIds.filter(id => id !== itemId));
};

    const handleSubmit = (data: UserCreateDto) => {
        if (editingId) {
            updateMutation.mutate({ id: editingId, data });
        } else {
            createMutation.mutate(data);
        }
    };

    const filteredUsers = users?.filter(user => {
        const matchesSearch = user.name.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesGroup = groupFilter === 'all' || user.groupId.toString() === groupFilter;
        const matchesRole = roleFilter === 'all' || user.roleId.toString() === roleFilter;
        return matchesSearch && matchesGroup && matchesRole;
    });

    const formatDate = (dateString: string) => {
        try {
            return new Date(dateString).toLocaleDateString('ru-RU', {
                day: 'numeric',
                month: 'long',
                year: 'numeric'
            });
        } catch {
            return dateString;
        }
    };

    if (usersLoading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen pt-20 px-4">
            <div className="container mx-auto">
                {/* Header */}
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4">
                    <div>
                        <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-indigo-400 bg-clip-text text-transparent">
                            Пользователи
                        </h1>
                        <p className="text-purple-300/70 mt-1">Управление пользователями системы</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white px-5 py-2.5 rounded-xl font-semibold transition-all duration-300 flex items-center gap-2 shadow-lg shadow-purple-500/25"
                    >
                        <Plus className="w-5 h-5" />
                        Добавить пользователя
                    </button>
                </div>

                {/* Filters */}
                <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-xl p-6 mb-6">
                    <div className="flex items-center gap-2 mb-4">
                        <Search className="w-5 h-5 text-purple-400" />
                        <h3 className="text-white font-semibold">Фильтры</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-purple-400" />
                            <input
                                type="text"
                                placeholder="Поиск по имени..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full pl-9 pr-4 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white placeholder-white/30 focus:outline-none focus:border-purple-500 transition-all"
                            />
                        </div>
                        <select
                            value={groupFilter}
                            onChange={(e) => setGroupFilter(e.target.value)}
                            className="px-4 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                        >
                            <option value="all">Все группы</option>
                            {groups?.map((group) => (
                                <option key={group.id} value={group.id.toString()}>
                                    {group.name}
                                </option>
                            ))}
                        </select>
                        <select
                            value={roleFilter}
                            onChange={(e) => setRoleFilter(e.target.value)}
                            className="px-4 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                        >
                            <option value="all">Все роли</option>
                            {roles?.map((role) => (
                                <option key={role.id} value={role.id.toString()}>
                                    {role.name}
                                </option>
                            ))}
                        </select>
                        {(searchTerm || groupFilter !== 'all' || roleFilter !== 'all') && (
                            <button
                                onClick={() => {
                                    setSearchTerm('');
                                    setGroupFilter('all');
                                    setRoleFilter('all');
                                }}
                                className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition flex items-center justify-center gap-2"
                            >
                                <X className="w-4 h-4" />
                                Сбросить
                            </button>
                        )}
                    </div>
                </div>

                {/* Users Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredUsers?.map((user) => (
                        <motion.div
                            key={user.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            whileHover={{ scale: 1.02, y: -5 }}
                            className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6 cursor-pointer group"
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-3">
                                    <div className="w-12 h-12 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-xl flex items-center justify-center">
                                        <Users className="w-6 h-6 text-purple-400" />
                                    </div>
                                    <div>
                                        <h3 className="font-semibold text-white text-lg">
                                            {user.name}
                                        </h3>
                                        <p className="text-xs text-white/40">ID: {user.id}</p>
                                    </div>
                                </div>
                                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                    <button
                                        onClick={() => openEditModal(user)}
                                        className="p-2 bg-yellow-500/20 text-yellow-300 rounded-lg hover:bg-yellow-500/30 transition"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => deleteMutation.mutate(user.id)}
                                        className="p-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <div className="flex items-center gap-2 text-white/60">
                                    <Calendar className="w-4 h-4 text-purple-400" />
                                    <span className="text-sm">Дата рождения: {formatDate(user.birthDate)}</span>
                                </div>
                                <div className="flex items-center gap-2 text-white/60">
                                    <Briefcase className="w-4 h-4 text-purple-400" />
                                    <span className="text-sm">Группа: <span className="text-purple-300">{user.groupName}</span></span>
                                </div>
                                <div className="flex items-center gap-2 text-white/60">
                                    <BadgeCheck className="w-4 h-4 text-purple-400" />
                                    <span className="text-sm">Роль: <span className="text-purple-300">{user.roleName}</span></span>
                                </div>
                            </div>

                                <div className="mt-4 pt-3 border-t border-white/10">
                                    <p className="text-sm text-white/40 flex items-center gap-1">
                                        <CheckSquare className="w-4 h-4" />
                                        Задач: <span className="text-purple-400 font-semibold">{user.items?.length || 0}</span>
                                    </p>
                                    <button
                                        onClick={() => showUserItems(user)}
                                        className="text-sm text-purple-400 hover:text-purple-300 transition flex items-center gap-1 mt-2"
                                    >
                                        <CheckSquare className="w-4 h-4" />
                                        Управление задачами
                                    </button>
                                </div>
                        </motion.div>
                    ))}
                </div>

                {filteredUsers?.length === 0 && (
                    <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">👥</div>
                        <h3 className="text-xl font-semibold text-white mb-2">Пользователи не найдены</h3>
                        <p className="text-white/40">Попробуйте изменить параметры поиска</p>
                    </div>
                )}

                {/* Modal */}
                <Modal isOpen={isModalOpen} onClose={closeModal} title={editingId ? 'Редактирование пользователя' : 'Добавление пользователя'}>
                    <UserForm
                        initialData={editingId ? users?.find(u => u.id === editingId) : undefined}
                        groups={groups || []}
                        roles={roles || []}
                        onSubmit={handleSubmit}
                        onCancel={closeModal}
                        isLoading={createMutation.isPending || updateMutation.isPending}
                    />
                </Modal>
                 {/* Modal for User Items management */}
                                <Modal isOpen={showUserItemsModal} onClose={() => setShowUserItemsModal(false)} title={`Задачи пользователя: ${selectedUserForItems?.name}`} size="lg">
                                    <div className="space-y-4">
                                        <div>
                                            <label className="block text-sm font-medium text-gray-300 mb-2">Добавить задачу</label>
                                            <select
                                                value=""
                                                onChange={(e) => {
                                                    if (e.target.value) {
                                                        addItemToUser(Number(e.target.value));
                                                        e.target.value = '';
                                                    }
                                                }}
                                                className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500"
                                            >
                                                <option value="">Выберите задачу</option>
                                                {allItems.filter(i => !userItems.some(ui => ui.id === i.id)).map(item => (
                                                    <option key={item.id} value={item.id}>{item.name}</option>
                                                ))}
                                            </select>
                                        </div>

                                        <div>
                                            <label className="block text-sm font-medium text-gray-300 mb-2">Текущие задачи</label>
                                            <div className="space-y-2">
                                                {userItems.length === 0 ? (
                                                    <p className="text-white/40 text-center py-4">Нет задач</p>
                                                ) : (
                                                    userItems.map(item => (
                                                        <div key={item.id} className="flex justify-between items-center p-2 bg-white/5 rounded-lg">
                                                            <span className="text-white">{item.name}</span>
                                                            <button onClick={() => removeItemFromUser(item.id)} className="text-red-400 hover:text-red-300">✕</button>
                                                        </div>
                                                    ))
                                                )}
                                            </div>
                                        </div>

                                        <div className="flex justify-end gap-3 pt-4">
                                            <button onClick={() => setShowUserItemsModal(false)} className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg">Отмена</button>
                                            <button onClick={saveUserItems} className="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg">Сохранить</button>
                                        </div>
                                    </div>
                                </Modal>
            </div>
        </div>
    );
};

export { UserList };