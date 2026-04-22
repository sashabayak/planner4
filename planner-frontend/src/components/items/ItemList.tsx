import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { itemService } from '../../services/itemService';
import { tagService } from '../../services/tagService';
import { userService } from '../../services/userService';
import { Item, Tag, User } from '../../types';
import { useToast } from '../../hooks/useToast';
import LoadingSpinner from '../common/LoadingSpinner';
import { Modal } from '../common/Modal';
import { ItemForm } from './ItemForm';
import { Plus, Pencil, Trash2, CheckSquare, Calendar, Users, Tag as TagIcon, Search, X, CheckCircle, Circle } from 'lucide-react';

const ItemList: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedItem, setSelectedItem] = useState<Item | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState('all');
    const [showUsersModal, setShowUsersModal] = useState(false);
    const [itemUsers, setItemUsers] = useState<User[]>([]);
    const [selectedItemForUsers, setSelectedItemForUsers] = useState<Item | null>(null);
    const [showTagsModal, setShowTagsModal] = useState(false);
    const [itemTags, setItemTags] = useState<Tag[]>([]);
    const [selectedItemForTags, setSelectedItemForTags] = useState<Item | null>(null);
    const [allTags, setAllTags] = useState<Tag[]>([]);
    const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);

    const { showSuccess, showError } = useToast();
    const queryClient = useQueryClient();

    const { data: items, isLoading } = useQuery<Item[]>({
        queryKey: ['items'],
        queryFn: itemService.getAll,
    });

    const { data: allUsers } = useQuery<User[]>({
        queryKey: ['users'],
        queryFn: userService.getAll,
    });

    const { data: tags } = useQuery<Tag[]>({
        queryKey: ['tags'],
        queryFn: tagService.getAll,
    });

    const createMutation = useMutation({
        mutationFn: itemService.create,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['items'] });
            showSuccess('Задача успешно создана');
            closeModal();
        },
        onError: (error: Error) => {
            showError(error.message || 'Ошибка при создании задачи');
        },
    });

    const updateMutation = useMutation({
        mutationFn: ({ id, data }: { id: number; data: any }) =>
            itemService.update(id, data),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['items'] });
            showSuccess('Задача успешно обновлена');
            closeModal();
        },
    });

    const deleteMutation = useMutation({
        mutationFn: itemService.delete,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['items'] });
            showSuccess('Задача успешно удалена');
        },
    });

    const openCreateModal = () => {
        setSelectedItem(null);
        setIsModalOpen(true);
    };

    const openEditModal = (item: Item) => {
        setSelectedItem(item);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedItem(null);
    };

    const handleSubmit = (data: any) => {
        if (selectedItem) {
            updateMutation.mutate({ id: selectedItem.id, data });
        } else {
            createMutation.mutate(data);
        }
    };

    const toggleComplete = (item: Item) => {
        updateMutation.mutate({
            id: item.id,
            data: { ...item, completed: !item.completed }
        });
    };

    const showItemUsers = (item: Item) => {
        // В реальном API нужно получать пользователей, связанных с задачей
        // Для демонстрации используем фильтрацию по items (если бы в User был массив items)
        // Пока заглушка
        setItemUsers([]);
        setSelectedItemForUsers(item);
        setShowUsersModal(true);
    };

    const showItemTags = async (item: Item) => {
        setSelectedItemForTags(item);
        setItemTags(item.tags || []);
        setSelectedTagIds((item.tags || []).map(t => t.id));
        setAllTags(tags || []);
        setShowTagsModal(true);
    };

const addTagToItem = (tagId: number) => {
    console.log("addTagToItem вызван с tagId:", tagId);
    console.log("allTags:", allTags);
    console.log("itemTags до добавления:", itemTags);

    const tag = allTags.find(t => t.id === tagId);
    console.log("Найденный тег:", tag);

    if (tag && !itemTags.some(t => t.id === tagId)) {
        const newItemTags = [...itemTags, tag];
        const newSelectedTagIds = [...selectedTagIds, tagId];

        console.log("Новый itemTags:", newItemTags);
        console.log("Новый selectedTagIds:", newSelectedTagIds);

        setItemTags(newItemTags);
        setSelectedTagIds(newSelectedTagIds);
    } else {
        console.log("Тег не добавлен: либо не найден, либо уже существует");
    }
};

const removeTagFromItem = (tagId: number) => {
    setItemTags(itemTags.filter(t => t.id !== tagId));
    setSelectedTagIds(selectedTagIds.filter(id => id !== tagId));
};

const saveTags = async () => {
    if (!selectedItemForTags) return;

    console.log("=== saveTags вызван ===");
    console.log("selectedItemForTags.id:", selectedItemForTags.id);
    console.log("selectedTagIds:", selectedTagIds);
    console.log("itemTags:", itemTags);

    console.log("currentTagIds (из itemTags):", currentTagIds);

    console.log("toAdd:", toAdd);
    console.log("toRemove:", toRemove);
    console.log("Сохраняем теги для задачи:", selectedItemForTags.id);
    console.log("Выбранные ID тегов:", selectedTagIds);

    const currentTagIds = itemTags.map(t => t.id);

    const toAdd = selectedTagIds.filter(id => !currentTagIds.includes(id));

    const toRemove = currentTagIds.filter(id => !selectedTagIds.includes(id));

    console.log("Добавить:", toAdd);
    console.log("Удалить:", toRemove);

    try {
        for (const tagId of toAdd) {
            console.log(`Добавляем тег ${tagId} к задаче ${selectedItemForTags.id}`);
            await itemService.addTagToItem(selectedItemForTags.id, tagId);
        }

        for (const tagId of toRemove) {
            console.log(`Удаляем тег ${tagId} у задачи ${selectedItemForTags.id}`);
            await itemService.removeTagFromItem(selectedItemForTags.id, tagId);
        }

        showSuccess('Теги обновлены');
        setShowTagsModal(false);
        queryClient.invalidateQueries({ queryKey: ['items'] });
    } catch (error) {
        console.error("Ошибка при сохранении тегов:", error);
        showError('Ошибка при сохранении тегов');
    }
};

    const filteredItems = items?.filter(item => {
        const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (item.description && item.description.toLowerCase().includes(searchTerm.toLowerCase()));
        const matchesStatus = statusFilter === 'all' ||
            (statusFilter === 'completed' && item.completed) ||
            (statusFilter === 'active' && !item.completed);
        return matchesSearch && matchesStatus;
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

    if (isLoading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen pt-20 px-4">
            <div className="container mx-auto">
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4">
                    <div>
                        <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-indigo-400 bg-clip-text text-transparent">
                            Задачи
                        </h1>
                        <p className="text-purple-300/70 mt-1">Управление задачами</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white px-5 py-2.5 rounded-xl font-semibold transition-all duration-300 flex items-center gap-2 shadow-lg shadow-purple-500/25"
                    >
                        <Plus className="w-5 h-5" />
                        Добавить задачу
                    </button>
                </div>

                {/* Filters */}
                <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-xl p-6 mb-6">
                    <div className="flex items-center gap-2 mb-4">
                        <Search className="w-5 h-5 text-purple-400" />
                        <h3 className="text-white font-semibold">Фильтры</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-purple-400" />
                            <input
                                type="text"
                                placeholder="Поиск по названию или описанию..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full pl-9 pr-4 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white placeholder-white/30 focus:outline-none focus:border-purple-500 transition-all"
                            />
                        </div>
                        <select
                            value={statusFilter}
                            onChange={(e) => setStatusFilter(e.target.value)}
                            className="px-4 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500 transition-all"
                        >
                            <option value="all">Все задачи</option>
                            <option value="active">Активные</option>
                            <option value="completed">Выполненные</option>
                        </select>
                        {(searchTerm || statusFilter !== 'all') && (
                            <button
                                onClick={() => {
                                    setSearchTerm('');
                                    setStatusFilter('all');
                                }}
                                className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition flex items-center justify-center gap-2"
                            >
                                <X className="w-4 h-4" />
                                Сбросить
                            </button>
                        )}
                    </div>
                </div>

                {/* Items Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredItems?.map((item) => (
                        <motion.div
                            key={item.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            whileHover={{ scale: 1.02, y: -5 }}
                            className={`bg-black/40 backdrop-blur-sm border rounded-2xl p-6 cursor-pointer group ${
                                item.completed ? 'border-green-500/30' : 'border-purple-500/30'
                            }`}
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-3">
                                    <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                                        item.completed ? 'bg-green-500/20' : 'bg-gradient-to-br from-purple-500/20 to-pink-500/20'
                                    }`}>
                                        {item.completed ? (
                                            <CheckCircle className="w-6 h-6 text-green-400" />
                                        ) : (
                                            <CheckSquare className="w-6 h-6 text-purple-400" />
                                        )}
                                    </div>
                                    <div>
                                        <h3 className={`font-semibold text-lg ${item.completed ? 'text-green-300 line-through' : 'text-white'}`}>
                                            {item.name}
                                        </h3>
                                        <p className="text-xs text-white/40">ID: {item.id}</p>
                                    </div>
                                </div>
                                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                    <button
                                        onClick={() => toggleComplete(item)}
                                        className="p-2 bg-green-500/20 text-green-300 rounded-lg hover:bg-green-500/30 transition"
                                        title={item.completed ? "Отметить как невыполненную" : "Отметить как выполненную"}
                                    >
                                        {item.completed ? <Circle className="w-4 h-4" /> : <CheckCircle className="w-4 h-4" />}
                                    </button>
                                    <button
                                        onClick={() => openEditModal(item)}
                                        className="p-2 bg-yellow-500/20 text-yellow-300 rounded-lg hover:bg-yellow-500/30 transition"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => deleteMutation.mutate(item.id)}
                                        className="p-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>

                            {item.description && (
                                <p className="text-white/60 text-sm mb-3 line-clamp-2">
                                    {item.description}
                                </p>
                            )}

                            <div className="space-y-2">
                                <div className="flex items-center gap-2 text-white/60">
                                    <Calendar className="w-4 h-4 text-purple-400" />
                                    <span className="text-sm">Создана: {formatDate(item.createdAt)}</span>
                                </div>
                            </div>

                            <div className="mt-4 pt-3 border-t border-white/10 flex flex-wrap gap-2">
                                <button
                                    onClick={() => showItemUsers(item)}
                                    className="text-sm text-purple-400 hover:text-purple-300 transition flex items-center gap-1"
                                >
                                    <Users className="w-4 h-4" />
                                    Пользователи
                                </button>
                                <button
                                    onClick={() => showItemTags(item)}
                                    className="text-sm text-purple-400 hover:text-purple-300 transition flex items-center gap-1"
                                >
                                    <TagIcon className="w-4 h-4" />
                                    Теги {item.tags && item.tags.length > 0 && `(${item.tags.length})`}
                                </button>
                            </div>

                            {/* Tags preview */}
                            {item.tags && item.tags.length > 0 && (
                                <div className="mt-2 flex flex-wrap gap-1">
                                    {item.tags.map(tag => (
                                        <span key={tag.id} className="text-xs px-2 py-0.5 bg-purple-500/20 text-purple-300 rounded-full">
                                            #{tag.name}
                                        </span>
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    ))}
                </div>

                {filteredItems?.length === 0 && (
                    <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">✅</div>
                        <h3 className="text-xl font-semibold text-white mb-2">Задачи не найдены</h3>
                        <p className="text-white/40">Создайте первую задачу</p>
                    </div>
                )}

                {/* Modal for Create/Edit */}
                <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedItem ? 'Редактирование задачи' : 'Добавление задачи'}>
                    <ItemForm
                        initialData={selectedItem || undefined}
                        onSubmit={handleSubmit}
                        onCancel={closeModal}
                        isLoading={createMutation.isPending || updateMutation.isPending}
                    />
                </Modal>

                {/* Modal for Tags management */}
                <Modal isOpen={showTagsModal} onClose={() => setShowTagsModal(false)} title={`Управление тегами: ${selectedItemForTags?.name}`} size="lg">
                    <div className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Добавить тег</label>
                            <div className="flex gap-2">
                                <select
                                    value=""
                                    onChange={(e) => {
                                        if (e.target.value) {
                                            addTagToItem(Number(e.target.value));
                                            e.target.value = '';
                                        }
                                    }}
                                    className="flex-1 px-3 py-2 bg-white/5 border border-purple-500/30 rounded-lg text-white focus:outline-none focus:border-purple-500"
                                >
                                    <option value="">Выберите тег</option>
                                    {allTags.filter(t => !itemTags.some(it => it.id === t.id)).map(tag => (
                                        <option key={tag.id} value={tag.id}>{tag.name}</option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Текущие теги</label>
                            <div className="flex flex-wrap gap-2">
                                {itemTags.length === 0 ? (
                                    <p className="text-white/40">Нет тегов</p>
                                ) : (
                                    itemTags.map(tag => (
                                        <div key={tag.id} className="flex items-center gap-1 px-2 py-1 bg-purple-500/20 rounded-full">
                                            <span className="text-purple-300 text-sm">#{tag.name}</span>
                                            <button
                                                onClick={() => removeTagFromItem(tag.id)}
                                                className="text-red-400 hover:text-red-300"
                                            >
                                                <X className="w-3 h-3" />
                                            </button>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>

                        <div className="flex justify-end gap-3 pt-4">
                            <button onClick={() => setShowTagsModal(false)} className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg">Отмена</button>
                            <button onClick={saveTags} className="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg">Сохранить</button>
                        </div>
                    </div>
                </Modal>

                {/* Modal for Users */}
                <Modal isOpen={showUsersModal} onClose={() => setShowUsersModal(false)} title={`Пользователи, связанные с задачей: ${selectedItemForUsers?.name}`} size="lg">
                    <div className="space-y-3">
                        {itemUsers.length === 0 ? (
                            <p className="text-white/40 text-center py-8">Нет пользователей, связанных с этой задачей</p>
                        ) : (
                            itemUsers.map(user => (
                                <div key={user.id} className="flex items-center justify-between p-3 bg-white/5 rounded-xl">
                                    <div>
                                        <p className="font-medium text-white">{user.name}</p>
                                        <p className="text-sm text-white/40">{user.roleName} • {user.groupName}</p>
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

export { ItemList };