import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { roleService } from '../../services/roleService';
import { userService } from '../../services/userService';
import { Role, User } from '../../types';
import { useToast } from '../../hooks/useToast';
import LoadingSpinner from '../common/LoadingSpinner';
import { Modal } from '../common/Modal';
import { RoleForm } from './RoleForm';
import { Plus, Pencil, Trash2, BadgeCheck, Users, Search, X } from 'lucide-react';

const RoleList: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedRole, setSelectedRole] = useState<Role | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [showUsersModal, setShowUsersModal] = useState(false);
    const [roleUsers, setRoleUsers] = useState<User[]>([]);
    const [selectedRoleForUsers, setSelectedRoleForUsers] = useState<Role | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const itemsPerPage = 9;

    const { showSuccess, showError } = useToast();
    const queryClient = useQueryClient();

    const { data: roles, isLoading } = useQuery<Role[]>({
        queryKey: ['roles'],
        queryFn: roleService.getAll,
    });

    const { data: allUsers } = useQuery<User[]>({
        queryKey: ['users'],
        queryFn: userService.getAll,
    });

    const createMutation = useMutation({
        mutationFn: roleService.create,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['roles'] });
            showSuccess('Роль успешно создана');
            closeModal();
        },
        onError: (error: Error) => {
            showError(error.message || 'Ошибка при создании роли');
        },
    });

    const updateMutation = useMutation({
        mutationFn: ({ id, data }: { id: number; data: { name: string } }) =>
            roleService.update(id, data),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['roles'] });
            showSuccess('Роль успешно обновлена');
            closeModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: roleService.delete,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['roles'] });
            showSuccess('Роль успешно удалена');
        },
    });

    const openCreateModal = () => {
        setSelectedRole(null);
        setIsModalOpen(true);
    };

    const openEditModal = (role: Role) => {
        setSelectedRole(role);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedRole(null);
    };

    const handleSubmit = (data: { name: string }) => {
        if (selectedRole) {
            updateMutation.mutate({ id: selectedRole.id, data });
        } else {
            createMutation.mutate(data);
        }
    };

    const showRoleUsers = (role: Role) => {
        const users = allUsers?.filter(user => user.roleId === role.id) || [];
        setRoleUsers(users);
        setSelectedRoleForUsers(role);
        setShowUsersModal(true);
    };

    const filteredRoles = roles?.filter(role =>
        role.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const totalPages = Math.ceil((filteredRoles?.length || 0) / itemsPerPage);
    const paginatedRoles = filteredRoles?.slice(currentPage * itemsPerPage, (currentPage + 1) * itemsPerPage);

    useEffect(() => {
        setCurrentPage(0);
    }, [searchTerm]);

    if (isLoading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen pt-6 px-4">
            <div className="container mx-auto">
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4 -mt-12">
                    <div>
                        <h1 className="text-4xl font-bold text-slate-600">Роли</h1>
                        <p className="text-slate-600 mt-3 text-xl">Управление ролями пользователей</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-slate-600 hover:bg-slate-700 text-white px-6 py-3 rounded-xl font-semibold text-xl transition-all duration-300 flex items-center gap-2 shadow-md"
                    >
                        <Plus className="w-6 h-6" />
                        Добавить роль
                    </button>
                </div>

                <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-xl p-5 mb-16">
                    <div className="flex items-center gap-2 mb-4">
                        <h3 className="text-slate-600 text-xl font-semibold">Фильтры</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 items-start">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-600" />
                            <input
                                type="text"
                                placeholder="Поиск по названию роли..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full pl-9 pr-4 py-2 text-lg bg-white/5 border border-slate-300 rounded-lg text-slate-600 placeholder-slate-600 focus:outline-none focus:border-slate-500 transition-all"
                            />
                        </div>
                        {searchTerm && (
                            <button
                                onClick={() => setSearchTerm('')}
                                className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition flex items-center justify-center gap-2"
                            >
                                <X className="w-4 h-4" />
                                Сбросить
                            </button>
                        )}
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {paginatedRoles?.map((role) => {
                        const usersCount = allUsers?.filter(u => u.roleId === role.id).length || 0;
                        return (
                            <motion.div
                                key={role.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                whileHover={{ scale: 1.02, y: -5 }}
                                className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-6 cursor-pointer group"
                            >
                                <div className="flex items-center justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <div>
                                            <h3 className="font-semibold text-xl text-slate-600">
                                                {role.name}
                                            </h3>
                                        </div>
                                    </div>
                                    <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                        <button
                                            onClick={() => openEditModal(role)}
                                            className="p-2 bg-yellow-500/20 text-slate-600 rounded-lg hover:bg-yellow-500/30 transition"
                                        >
                                            <Pencil className="w-4 h-4" />
                                        </button>
                                        <button
                                            onClick={() => deleteMutation.mutate(role.id)}
                                            className="p-2 bg-red-500/20 text-slate-600 rounded-lg hover:bg-red-500/30 transition"
                                        >
                                            <Trash2 className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <div className="flex items-center gap-2 text-slate-600">
                                        <Users className="w-4 h-4" />
                                        <span className="text-sm">Пользователей: <span className="font-semibold">{usersCount}</span></span>
                                    </div>
                                </div>

                                <div className="mt-4 pt-3 border-t border-slate-300">
                                    <button
                                        onClick={() => showRoleUsers(role)}
                                        className="text-sm text-slate-600 hover:text-slate-800 transition flex items-center gap-1"
                                    >
                                        <Users className="w-4 h-4" />
                                        Просмотреть пользователей
                                    </button>
                                </div>
                            </motion.div>
                        );
                    })}
                </div>

                {filteredRoles?.length === 0 && (
                    <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">🏷️</div>
                        <h3 className="text-xl font-semibold text-slate-600 mb-2">Роли не найдены</h3>
                        <p className="text-slate-500">Создайте первую роль</p>
                    </div>
                )}

                {totalPages > 1 && (
                    <div className="flex justify-center items-center gap-4 mt-8">
                        <button
                            onClick={() => setCurrentPage(p => p - 1)}
                            disabled={currentPage === 0}
                            className="px-4 py-2 bg-white/5 border border-slate-300 rounded-lg text-slate-600 disabled:opacity-30 hover:bg-slate-100"
                        >
                            ← Предыдущая
                        </button>
                        <span className="text-slate-600">
                            Страница {currentPage + 1} из {totalPages}
                        </span>
                        <button
                            onClick={() => setCurrentPage(p => p + 1)}
                            disabled={currentPage + 1 >= totalPages}
                            className="px-4 py-2 bg-white/5 border border-slate-300 rounded-lg text-slate-600 disabled:opacity-30 hover:bg-slate-100"
                        >
                            Следующая →
                        </button>
                    </div>
                )}

                <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedRole ? 'Редактирование роли' : 'Добавление роли'}>
                    <RoleForm
                        initialData={selectedRole || undefined}
                        onSubmit={handleSubmit}
                        onCancel={closeModal}
                        isLoading={createMutation.isPending || updateMutation.isPending}
                    />
                </Modal>

               <Modal isOpen={showUsersModal} onClose={() => setShowUsersModal(false)} title={`Пользователи с ролью: ${selectedRoleForUsers?.name}`} size="lg">
                   <div className="space-y-3">
                       {roleUsers.length === 0 ? (
                           <p className="text-slate-500 text-center py-8">Нет пользователей с этой ролью</p>
                       ) : (
                           roleUsers.map(user => (
                               <div key={user.id} className="flex justify-between items-center p-3 bg-white/80 rounded-xl border border-slate-300">
                                   <div>
                                       <p className="font-medium text-slate-700">{user.name}</p>
                                       <p className="text-sm text-slate-500">{user.groupName}</p>
                                   </div>
                               </div>
                           ))
                       )}
                   </div>
               </Modal>
            </div>
        </div>
    );
};

export { RoleList };