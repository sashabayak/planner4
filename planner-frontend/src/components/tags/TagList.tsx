import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { tagService } from '../../services/tagService';
import { Tag } from '../../types';
import { useToast } from '../../hooks/useToast';
import LoadingSpinner from '../common/LoadingSpinner';
import { Modal } from '../common/Modal';
import { Plus, Pencil, Trash2, Tag as TagIcon, Search, X } from 'lucide-react';

const TagList: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedTag, setSelectedTag] = useState<Tag | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [formName, setFormName] = useState('');

    const { showSuccess, showError } = useToast();
    const queryClient = useQueryClient();

    const { data: tags, isLoading } = useQuery<Tag[]>({
        queryKey: ['tags'],
        queryFn: tagService.getAll,
    });

    const createMutation = useMutation({
        mutationFn: tagService.create,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['tags'] });
            await queryClient.invalidateQueries({ queryKey: ['items'] });
            showSuccess('Тег успешно создан');
            closeModal();
        },
        onError: (error: Error) => {
            showError(error.message || 'Ошибка при создании тега');
        },
    });

    const updateMutation = useMutation({
        mutationFn: ({ id, data }: { id: number; data: { name: string } }) =>
            tagService.update(id, data),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['tags'] });
            await queryClient.invalidateQueries({ queryKey: ['items'] });
            showSuccess('Тег успешно обновлен');
            closeModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: tagService.delete,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['tags'] });
            await queryClient.invalidateQueries({ queryKey: ['items'] });
            showSuccess('Тег успешно удален');
        },
    });

    const openCreateModal = () => {
        setSelectedTag(null);
        setFormName('');
        setIsModalOpen(true);
    };

    const openEditModal = (tag: Tag) => {
        setSelectedTag(tag);
        setFormName(tag.name);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedTag(null);
        setFormName('');
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (selectedTag) {
            updateMutation.mutate({ id: selectedTag.id, data: { name: formName } });
        } else {
            createMutation.mutate({ name: formName });
        }
    };

    const filteredTags = tags?.filter(tag =>
        tag.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (isLoading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen pt-20 px-4">
            <div className="container mx-auto">
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4">
                    <div>
                        <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-indigo-400 bg-clip-text text-transparent">
                            Теги
                        </h1>
                        <p className="text-purple-300/70 mt-1">Управление тегами для задач</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white px-5 py-2.5 rounded-xl font-semibold transition-all duration-300 flex items-center gap-2 shadow-lg shadow-purple-500/25"
                    >
                        <Plus className="w-5 h-5" />
                        Добавить тег
                    </button>
                </div>

                <div className="mb-6">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-purple-400" />
                        <input
                            type="text"
                            placeholder="Поиск по названию тега..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-3 bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-xl text-white placeholder-white/30 focus:outline-none focus:border-purple-500 transition-all"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredTags?.map((tag) => (
                        <motion.div
                            key={tag.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            whileHover={{ scale: 1.02, y: -5 }}
                            className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6 cursor-pointer group"
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-3">
                                    <div className="w-12 h-12 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-xl flex items-center justify-center">
                                        <TagIcon className="w-6 h-6 text-purple-400" />
                                    </div>
                                    <div>
                                        <h3 className="font-semibold text-white text-lg">
                                            #{tag.name}
                                        </h3>
                                        <p className="text-xs text-white/40">ID: {tag.id}</p>
                                    </div>
                                </div>
                                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                    <button
                                        onClick={() => openEditModal(tag)}
                                        className="p-2 bg-yellow-500/20 text-yellow-300 rounded-lg hover:bg-yellow-500/30 transition"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => deleteMutation.mutate(tag.id)}
                                        className="p-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </motion.div>
                    ))}
                </div>

                {filteredTags?.length === 0 && (
                    <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">🏷️</div>
                        <h3 className="text-xl font-semibold text-white mb-2">Теги не найдены</h3>
                        <p className="text-white/40">Создайте первый тег</p>
                    </div>
                )}

                <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedTag ? 'Редактирование тега' : 'Добавление тега'}>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label htmlFor="tagName" className="block text-sm font-medium text-gray-300 mb-1">
                                Название тега *
                            </label>
                            <input
                                id="tagName"
                                type="text"
                                required
                                value={formName}
                                onChange={(e) => setFormName(e.target.value)}
                                className="w-full px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                                placeholder="важный"
                            />
                        </div>
                        <div className="flex justify-end gap-3 pt-4">
                            <button type="button" onClick={closeModal} className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg">Отмена</button>
                            <button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg">
                                {createMutation.isPending || updateMutation.isPending ? 'Сохранение...' : 'Сохранить'}
                            </button>
                        </div>
                    </form>
                </Modal>
            </div>
        </div>
    );
};

export { TagList };