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
        <div className="min-h-screen pt-20 px-4">
            <div className="container mx-auto">
                {/* Header */}
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4">
                    <div>
                        <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-indigo-400 bg-clip-text text-transparent">
                            Группы
                        </h1>
                        <p className="text-purple-300/70 mt-1">Управление группами пользователей</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white px-5 py-2.5 rounded-xl font-semibold transition-all duration-300 flex items-center gap-2 shadow-lg shadow-purple-500/25"
                    >
                        <Plus className="w-5 h-5" />
                        Добавить группу
                    </button>
                </div>

                {/* Search */}
                <div className="mb-6">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-purple-400" />
                        <input
                            type="text"
                            placeholder="Поиск по названию группы..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-3 bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-xl text-white placeholder-white/30 focus:outline-none focus:border-purple-500 transition-all"
                        />
                    </div>
                </div>

                {/* Groups Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredGroups?.map((group) => {
                        const usersCount = allUsers?.filter(u => u.groupId === group.id).length || 0;
                        return (
                            <motion.div
                                key={group.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                whileHover={{ scale: 1.02, y: -5 }}
                                className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6 cursor-pointer group"
                            >
                                <div className="flex items-center justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <div className="w-12 h-12 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-xl flex items-center justify-center">
                                            <FolderKanban className="w-6 h-6 text-purple-400" />
                                        </div>
                                        <div>
                                            <h3 className="font-semibold text-white text-lg">
                                                {group.name}
                                            </h3>
                                            <p className="text-xs text-white/40">ID: {group.id}</p>
                                        </div>
                                    </div>
                                    <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                        <button
                                            onClick={() => openEditModal(group)}
                                            className="p-2 bg-yellow-500/20 text-yellow-300 rounded-lg hover:bg-yellow-500/30 transition"
                                        >
                                            <Pencil className="w-4 h-4" />
                                        </button>
                                        <button
                                            onClick={() => deleteMutation.mutate(group.id)}
                                            className="p-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition"
                                        >
                                            <Trash2 className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <div className="flex items-center gap-2 text-white/60">
                                        <Users className="w-4 h-4 text-purple-400" />
                                        <span className="text-sm">Пользователей: <span className="text-purple-300 font-semibold">{usersCount}</span></span>
                                    </div>
                                </div>

                                <div className="mt-4 pt-3 border-t border-white/10">
                                    <button
                                        onClick={() => showGroupUsers(group)}
                                        className="text-sm text-purple-400 hover:text-purple-300 transition flex items-center gap-1"
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
                    <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">📁</div>
                        <h3 className="text-xl font-semibold text-white mb-2">Группы не найдены</h3>
                        <p className="text-white/40">Создайте первую группу</p>
                    </div>
                )}

                {/* Modal for Create/Edit */}
                <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedGroup ? 'Редактирование группы' : 'Добавление группы'}>
                    <GroupForm
                        initialData={selectedGroup || undefined}
                        onSubmit={handleSubmit}
                        onCancel={closeModal}
                        isLoading={createMutation.isPending || updateMutation.isPending}
                    />
                </Modal>

                {/* Modal for Users in Group */}
                <Modal isOpen={showUsersModal} onClose={() => setShowUsersModal(false)} title={`Участники группы: ${selectedGroupForUsers?.name}`} size="lg">
                    <div className="space-y-3">
                        {groupUsers.length === 0 ? (
                            <p className="text-white/40 text-center py-8">В этой группе нет пользователей</p>
                        ) : (
                            groupUsers.map(user => (
                                <div key={user.id} className="flex items-center justify-between p-3 bg-white/5 rounded-xl">
                                    <div>
                                        <p className="font-medium text-white">{user.name}</p>
                                        <p className="text-sm text-white/40">{user.roleName}</p>
                                    </div>
                                    <span className="text-xs text-purple-400">ID: {user.id}</span>
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