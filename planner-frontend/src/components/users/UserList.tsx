import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { userService } from '../../services/userService';
import { groupService } from '../../services/groupService';
import { roleService } from '../../services/roleService';
import { User, UserCreateDto, Group, Role, Item } from '../../types';
import { useToast } from '../../hooks/useToast';
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
    const [currentPage, setCurrentPage] = useState(0);
    const itemsPerPage = 9;

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
        try {
            const freshUser = await userService.getById(user.id);
            setSelectedUserForItems(freshUser);
            setUserItems(freshUser.items || []);
            setSelectedItemIds((freshUser.items || []).map(i => i.id));
            setAllItems(items || []);
            setShowUserItemsModal(true);
        } catch (error) {
            setSelectedUserForItems(user);
            setUserItems(user.items || []);
            setSelectedItemIds((user.items || []).map(i => i.id));
            setAllItems(items || []);
            setShowUserItemsModal(true);
        }
    };

    const saveUserItems = async () => {
        if (!selectedUserForItems) return;
        try {
            for (const itemId of selectedItemIds) {
                await userService.addItemToUser(selectedUserForItems.id, itemId);
            }
            showSuccess('Задачи пользователя обновлены');
            setShowUserItemsModal(false);
            queryClient.invalidateQueries({ queryKey: ['users'] });
            queryClient.invalidateQueries({ queryKey: ['items'] });
        } catch (error) {
            showError('Ошибка при сохранении');
        }
    };

    const addItemToUser = (itemId: number) => {
        const item = allItems.find(i => i.id === itemId);
        if (item && !userItems.some(i => i.id === itemId)) {
            setUserItems([...userItems, item]);
            setSelectedItemIds([...selectedItemIds, itemId]);
        }
    };

    const removeItemFromUser = (itemId: number) => {
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

    const totalPages = Math.ceil((filteredUsers?.length || 0) / itemsPerPage);
    const paginatedUsers = filteredUsers?.slice(currentPage * itemsPerPage, (currentPage + 1) * itemsPerPage);

    useEffect(() => {
        setCurrentPage(0);
    }, [searchTerm, groupFilter, roleFilter]);

    const formatDate = (dateString: string) => {
        if (!dateString) return 'Дата не указана';
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
        <div className="min-h-screen pt-6 px-4">
            <div className="container mx-auto">
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4 -mt-12">
                    <div>
                        <h1 className="text-4xl font-bold text-slate-600 ">Пользователи</h1>
                        <p className="text-slate-600 mt-3 text-xl">Управление пользователями системы</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-slate-600 hover:bg-slate-700 text-white px-6 py-3 rounded-xl font-semibold text-xl transition-all duration-300 flex items-center gap-2 shadow-md"
                    >
                        <Plus className="w-6 h-6" />
                        Добавить пользователя
                    </button>
                </div>

                {/* Фильтры */}
                <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-xl p-5 mb-16">
                    <div className="flex items-center gap-2 mb-4">
                        <h3 className="text-slate-600 text-xl font-semibold">Фильтры</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-start">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-600" />
                            <input
                                type="text"
                                placeholder="Поиск по имени..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full pl-9 pr-4 py-2 text-lg bg-white/5 border border-slate-300 rounded-lg text-slate-600 placeholder-slate-600 focus:outline-none focus:border-slate-500 transition-all"
                            />
                        </div>
                        <div>
                            <label className="block text-[17px] font-medium text-slate-600 mb-1 -mt-5">Группа</label>
                            <div className="grid grid-cols-2 gap-1 max-h-32 overflow-y-auto p-1 bg-white/5 rounded-lg border border-slate-300">
                                <button
                                    type="button"
                                    onClick={() => setGroupFilter('all')}
                                    className={`px-2 py-1 rounded-lg text-xs transition ${
                                        groupFilter === 'all'
                                            ? 'bg-slate-600 text-white'
                                            : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                                    }`}
                                >
                                    Все
                                </button>
                                {groups?.map((group) => (
                                    <button
                                        key={group.id}
                                        type="button"
                                        onClick={() => setGroupFilter(group.id.toString())}
                                        className={`px-2 py-1 rounded-lg text-xs transition ${
                                            groupFilter === group.id.toString()
                                                ? 'bg-slate-600 text-white'
                                                : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                                        }`}
                                    >
                                        {group.name}
                                    </button>
                                ))}
                            </div>
                        </div>
                        <div>
                            <label className="block text-[17px] font-medium text-slate-600 mb-1 -mt-5">Роль</label>
                            <div className="grid grid-cols-2 gap-1 max-h-32 overflow-y-auto p-1 bg-white/5 rounded-lg border border-slate-300">
                                <button
                                    type="button"
                                    onClick={() => setRoleFilter('all')}
                                    className={`px-2 py-1 rounded-lg text-xs transition ${
                                        roleFilter === 'all'
                                            ? 'bg-slate-600 text-white'
                                            : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                                    }`}
                                >
                                    Все
                                </button>
                                {roles?.map((role) => (
                                    <button
                                        key={role.id}
                                        type="button"
                                        onClick={() => setRoleFilter(role.id.toString())}
                                        className={`px-2 py-1 rounded-lg text-xs transition ${
                                            roleFilter === role.id.toString()
                                                ? 'bg-slate-600 text-white'
                                                : 'bg-white text-slate-600 border border-slate-300 hover:bg-slate-100'
                                        }`}
                                    >
                                        {role.name}
                                    </button>
                                ))}
                            </div>
                        </div>
                        {(searchTerm || groupFilter !== 'all' || roleFilter !== 'all') && (
                            <button
                                onClick={() => {
                                    setSearchTerm('');
                                    setGroupFilter('all');
                                    setRoleFilter('all');
                                }}
                                className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition flex items-center justify-center gap-2 self-end"
                            >
                                <X className="w-4 h-4" />
                                Сбросить
                            </button>
                        )}
                    </div>
                </div>

                {/* Сетка пользователей */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {paginatedUsers?.map((user) => (
                        <motion.div
                            key={user.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            whileHover={{ scale: 1.02, y: -5 }}
                            className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-6 cursor-pointer group"
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-3">

                                    <div>
                                        <h3 className="font-semibold text-xl text-slate-600">
                                            {user.name}
                                        </h3>
                                    </div>
                                </div>
                                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                    <button
                                        onClick={() => openEditModal(user)}
                                        className="p-2 bg-yellow-500/20 text-slate-600 rounded-lg hover:bg-yellow-500/30 transition"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => deleteMutation.mutate(user.id)}
                                        className="p-2 bg-red-500/20 text-slate-600 rounded-lg hover:bg-red-500/30 transition"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <div className="flex items-center gap-2 text-slate-600">
                                    <Briefcase className="w-4 h-4" />
                                    <span className="text-sm">Группа: <span className="font-semibold">{user.groupName}</span></span>
                                </div>
                                <div className="flex items-center gap-2 text-slate-600">
                                    <BadgeCheck className="w-4 h-4" />
                                    <span className="text-sm">Роль: <span className="font-semibold">{user.roleName}</span></span>
                                </div>
                            </div>

                            <div className="mt-4 pt-3 border-t border-slate-300">
                                <p className="text-sm text-slate-600 flex items-center gap-1">
                                    <CheckSquare className="w-4 h-4" />
                                    Задач: <span className="font-semibold">{user.items?.length || 0}</span>
                                </p>
                                <button
                                    onClick={() => showUserItems(user)}
                                    className="text-sm text-slate-600 hover:text-slate-800 transition flex items-center gap-1 mt-2"
                                >
                                    <CheckSquare className="w-4 h-4" />
                                    Управление задачами
                                </button>
                            </div>
                        </motion.div>
                    ))}
                </div>

                {filteredUsers?.length === 0 && (
                    <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">👥</div>
                        <h3 className="text-xl font-semibold text-slate-600 mb-2">Пользователи не найдены</h3>
                        <p className="text-slate-500">Попробуйте изменить параметры поиска</p>
                    </div>
                )}

                {/* Пагинация */}
                {totalPages > 1 && (
                    <div className="flex justify-center items-center gap-4 mt-8">
                        <button
                            onClick={() => setCurrentPage(p => Math.max(0, p - 1))}
                            disabled={currentPage === 0}
                            className="px-4 py-2 bg-white/5 border border-slate-300 rounded-lg text-slate-600 disabled:opacity-30 hover:bg-slate-100 transition"
                        >
                            ← Предыдущая
                        </button>
                        <span className="text-slate-600">
                            Страница {currentPage + 1} из {totalPages}
                        </span>
                        <button
                            onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))}
                            disabled={currentPage + 1 >= totalPages}
                            className="px-4 py-2 bg-white/5 border border-slate-300 rounded-lg text-slate-600 disabled:opacity-30 hover:bg-slate-100 transition"
                        >
                            Следующая →
                        </button>
                    </div>
                )}

                {/* Модальное окно создания/редактирования */}
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

                {/* Модальное окно управления задачами пользователя */}
                <Modal isOpen={showUserItemsModal} onClose={() => setShowUserItemsModal(false)} title={`Задачи пользователя: ${selectedUserForItems?.name}`} size="lg">
                    <div className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Доступные задачи</label>
                            <div className="space-y-2 max-h-40 overflow-y-auto">
                                {allItems.filter(i => !userItems.some(ui => ui.id === i.id)).length === 0 ? (
                                    <p className="text-white/40 text-sm">Нет доступных задач</p>
                                ) : (
                                    allItems.filter(i => !userItems.some(ui => ui.id === i.id)).map(item => (
                                        <button
                                            key={item.id}
                                            onClick={() => addItemToUser(item.id)}
                                            className="w-full text-left px-3 py-2 bg-white/5 hover:bg-purple-500/30 rounded-lg text-white transition flex justify-between items-center group"
                                        >
                                            <span>{item.name}</span>
                                            <span className="text-purple-400 opacity-0 group-hover:opacity-100">+ Добавить</span>
                                        </button>
                                    ))
                                )}
                            </div>
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