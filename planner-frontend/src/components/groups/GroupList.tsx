import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { Group, User } from '../../types';
import { useToast } from '../../hooks/useToast';
import LoadingSpinner from '../common/LoadingSpinner';
import { Modal } from '../common/Modal';
import { GroupForm } from './GroupForm';
import { Plus, Pencil, Trash2, FolderKanban, Users, Search, X } from 'lucide-react';

const GroupList: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [showUsersModal, setShowUsersModal] = useState(false);
    const [groupUsers, setGroupUsers] = useState<User[]>([]);
    const [selectedGroupForUsers, setSelectedGroupForUsers] = useState<Group | null>(null);

    const { showSuccess, showError } = useToast();
    const queryClient = useQueryClient();

    const { data: groups, isLoading } = useQuery<Group[]>({
        queryKey: ['groups'],
        queryFn: groupService.getAll,
    });

    const { data: allUsers } = useQuery<User[]>({
        queryKey: ['users'],
        queryFn: userService.getAll,
    });

    const createMutation = useMutation({
        mutationFn: groupService.create,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['groups'] });
            showSuccess('Группа успешно создана');
            closeModal();
        },
        onError: (error: Error) => {
            showError(error.message || 'Ошибка при создании группы');
        },
    });

    const updateMutation = useMutation({
        mutationFn: ({ id, data }: { id: number; data: { name: string } }) =>
            groupService.update(id, data),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['groups'] });
            showSuccess('Группа успешно обновлена');
            closeModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: groupService.delete,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['groups'] });
            showSuccess('Группа успешно удалена');
        },
    });

    const openCreateModal = () => {
        setSelectedGroup(null);
        setIsModalOpen(true);
    };

    const openEditModal = (group: Group) => {
        setSelectedGroup(group);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedGroup(null);
    };

    const handleSubmit = (data: { name: string }) => {
        if (selectedGroup) {
            updateMutation.mutate({ id: selectedGroup.id, data });
        } else {
            createMutation.mutate(data);
        }
    };

    const showGroupUsers = (group: Group) => {
        const users = allUsers?.filter(user => user.groupId === group.id) || [];
        setGroupUsers(users);
        setSelectedGroupForUsers(group);
        setShowUsersModal(true);
    };

    const filteredGroups = groups?.filter(group =>
        group.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (isLoading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen pt-6 px-4">
            <div className="container mx-auto">
                {/* Header */}
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4 -mt-12">
                    <div>
                        <h1 className="text-4xl font-bold text-slate-600">Группы</h1>
                        <p className="text-slate-600 mt-3 text-xl">Управление группами пользователей</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-slate-600 hover:bg-slate-700 text-white px-6 py-3 rounded-xl font-semibold text-xl transition-all duration-300 flex items-center gap-2 shadow-md"
                    >
                        <Plus className="w-6 h-6" />
                        Добавить группу
                    </button>
                </div>

                {/* Фильтры */}
                <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-xl p-5 mb-16">
                    <div className="flex items-center gap-2 mb-4">
                        <h3 className="text-slate-600 text-xl font-semibold">Фильтры</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 items-start">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-600" />
                            <input
                                type="text"
                                placeholder="Поиск по названию группы..."
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

                {/* Grid карточек */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredGroups?.map((group) => {
                        const usersCount = allUsers?.filter(u => u.groupId === group.id).length || 0;
                        return (
                            <motion.div
                                key={group.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                whileHover={{ scale: 1.02, y: -5 }}
                                className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-6 cursor-pointer group"
                            >
                                <div className="flex items-center justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <div>
                                            <h3 className="font-semibold text-xl text-slate-600">
                                                {group.name}
                                            </h3>
                                        </div>
                                    </div>
                                    <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                        <button
                                            onClick={() => openEditModal(group)}
                                            className="p-2 bg-yellow-500/20 text-slate-600 rounded-lg hover:bg-yellow-500/30 transition"
                                        >
                                            <Pencil className="w-4 h-4" />
                                        </button>
                                        <button
                                            onClick={() => deleteMutation.mutate(group.id)}
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
                                        onClick={() => showGroupUsers(group)}
                                        className="text-sm text-slate-600 hover:text-slate-800 transition flex items-center gap-1"
                                    >
                                        <Users className="w-4 h-4" />
                                        Просмотреть участников
                                    </button>
                                </div>
                            </motion.div>
                        );
                    })}
                </div>

                {filteredGroups?.length === 0 && (
                    <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">📁</div>
                        <h3 className="text-xl font-semibold text-slate-600 mb-2">Группы не найдены</h3>
                        <p className="text-slate-500">Создайте первую группу</p>
                    </div>
                )}

                {/* Модальное окно создания/редактирования */}
                <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedGroup ? 'Редактирование группы' : 'Добавление группы'}>
                    <GroupForm
                        initialData={selectedGroup || undefined}
                        onSubmit={handleSubmit}
                        onCancel={closeModal}
                        isLoading={createMutation.isPending || updateMutation.isPending}
                    />
                </Modal>

                {/* Модальное окно участников группы */}
              <Modal isOpen={showUsersModal} onClose={() => setShowUsersModal(false)} title={`Участники группы: ${selectedGroupForUsers?.name}`} size="lg">
                  <div className="space-y-3">
                      {groupUsers.length === 0 ? (
                          <p className="text-slate-500 text-center py-8">В этой группе нет пользователей</p>
                      ) : (
                          groupUsers.map(user => (
                              <div key={user.id} className="flex justify-between items-center p-3 bg-white/80 rounded-xl border border-slate-300">
                                  <div>
                                      <p className="font-medium text-slate-700">{user.name}</p>
                                      <p className="text-sm text-slate-500">{user.roleName}</p>
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

export { GroupList };