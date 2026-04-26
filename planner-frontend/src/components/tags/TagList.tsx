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
        <div className="min-h-screen pt-6 px-4">
            <div className="container mx-auto">
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4 -mt-12">
                    <div>
                        <h1 className="text-4xl font-bold text-slate-600">Теги</h1>
                        <p className="text-slate-600 mt-3 text-xl">Управление тегами для задач</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-slate-600 hover:bg-slate-700 text-white px-6 py-3 rounded-xl font-semibold text-xl transition-all duration-300 flex items-center gap-2 shadow-md"
                    >
                        <Plus className="w-6 h-6" />
                        Добавить тег
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
                                placeholder="Поиск по названию тега..."
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
                    {filteredTags?.map((tag) => (
                        <motion.div
                            key={tag.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            whileHover={{ scale: 1.02, y: -5 }}
                            className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-6 cursor-pointer group"
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-3">
                                    <div className="w-12 h-12 rounded-xl flex items-center justify-center bg-slate-300">
                                        <TagIcon className="w-6 h-6 text-slate-600" />
                                    </div>
                                    <div>
                                        <h3 className="font-semibold text-xl text-slate-600">
                                            #{tag.name}
                                        </h3>
                                    </div>
                                </div>
                                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                    <button onClick={() => openEditModal(tag)} className="p-2 bg-yellow-500/20 text-slate-600 rounded-lg hover:bg-yellow-500/30 transition">
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button onClick={() => deleteMutation.mutate(tag.id)} className="p-2 bg-red-500/20 text-slate-600 rounded-lg hover:bg-red-500/30 transition">
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </motion.div>
                    ))}
                </div>

                {filteredTags?.length === 0 && (
                    <div className="bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">🏷️</div>
                        <h3 className="text-xl font-semibold text-slate-600 mb-2">Теги не найдены</h3>
                        <p className="text-slate-500">Создайте первый тег</p>
                    </div>
                )}

               <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedTag ? 'Редактирование тега' : 'Добавление тега'}>
                   <form onSubmit={handleSubmit} className="space-y-4">
                       <div>
                           <label htmlFor="tagName" className="block text-sm font-medium text-slate-600 mb-1">
                               Название тега *
                           </label>
                           <input
                               id="tagName"
                               type="text"
                               required
                               value={formName}
                               onChange={(e) => setFormName(e.target.value)}
                               className="w-full px-3 py-2 bg-white/50 border border-slate-300 rounded-lg text-slate-700 focus:outline-none focus:border-slate-500 transition-all"
                               placeholder="важный"
                           />
                       </div>
                       <div className="flex justify-end gap-3 pt-4">
                           <button
                               type="button"
                               onClick={closeModal}
                               className="px-4 py-2 bg-red-500/20 text-red-600 rounded-lg hover:bg-red-500/30 transition"
                           >
                               Отмена
                           </button>
                           <button
                               type="submit"
                               disabled={createMutation.isPending || updateMutation.isPending}
                               className="px-4 py-2 bg-slate-600 hover:bg-slate-700 text-white rounded-lg transition disabled:opacity-50"
                           >
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