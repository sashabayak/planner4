import React, { useState, useEffect } from 'react';
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
    const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
    const [selectedTagId, setSelectedTagId] = useState<number | null>(null);
    const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const itemsPerPage = 9;
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
        setSelectedItemForUsers(item);
        setItemUsers(item.users || []);
        setShowUsersModal(true);
    };

    const showItemTags = async (item: Item) => {
        setSelectedItemForTags(item);
        setItemTags(item.tags || []);
        setAllTags(tags || []);
        setShowTagsModal(true);
    };

   const addTagToItem = async (tagId: number) => {
       if (!selectedItemForTags) return;
       const tag = allTags.find(t => t.id === tagId);
       if (!tag) return;
       try {
           await itemService.addTagToItem(selectedItemForTags.id, tagId);
           // Обновляем локальное состояние
           setItemTags([...itemTags, tag]);
           setSelectedTagIds([...selectedTagIds, tagId]);
           showSuccess(`Тег "${tag.name}" добавлен`);
           // Обновляем список задач
           queryClient.invalidateQueries({ queryKey: ['items'] });
       } catch (error) {
           console.error(error);
           showError('Ошибка при добавлении тега');
       }
   };

   const removeTagFromItem = async (tagId: number) => {
       if (!selectedItemForTags) return;
       const tag = itemTags.find(t => t.id === tagId);
       if (!tag) return;
       try {
           await itemService.removeTagFromItem(selectedItemForTags.id, tagId);
           setItemTags(itemTags.filter(t => t.id !== tagId));
           setSelectedTagIds(selectedTagIds.filter(id => id !== tagId));
           showSuccess(`Тег "${tag.name}" удалён`);
           queryClient.invalidateQueries({ queryKey: ['items'] });
       } catch (error) {
           console.error(error);
           showError('Ошибка при удалении тега');
       }
   };

   const addUserToItem = async (userId: number) => {
       if (!selectedItemForUsers) return;
       const user = allUsers.find(u => u.id === userId);
       if (!user) return;
       try {
           await userService.addItemToUser(userId, selectedItemForUsers.id);
           setItemUsers([...itemUsers, user]);
           setSelectedUserIds([...selectedUserIds, userId]);
           showSuccess(`Пользователь "${user.name}" добавлен`);
           queryClient.invalidateQueries({ queryKey: ['items'] });
       } catch (error) {
           console.error(error);
           showError('Ошибка при добавлении пользователя');
       }
   };

   const removeUserFromItem = async (userId: number) => {
       if (!selectedItemForUsers) return;
       const user = itemUsers.find(u => u.id === userId);
       if (!user) return;
       try {
           await userService.removeItemFromUser(userId, selectedItemForUsers.id);
           setItemUsers(itemUsers.filter(u => u.id !== userId));
           setSelectedUserIds(selectedUserIds.filter(id => id !== userId));
           showSuccess(`Пользователь "${user.name}" удалён`);
           queryClient.invalidateQueries({ queryKey: ['items'] });
       } catch (error) {
           console.error(error);
           showError('Ошибка при удалении пользователя');
       }
   };

    const filteredItems = items?.filter(item => {
        const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (item.description && item.description.toLowerCase().includes(searchTerm.toLowerCase()));
        const matchesStatus = statusFilter === 'all' ||
            (statusFilter === 'completed' && item.completed) ||
            (statusFilter === 'active' && !item.completed);
        const matchesTag = selectedTagId ? item.tags?.some(tag => tag.id === selectedTagId) : true;
        const matchesUser = selectedUserId ? item.users?.some(user => user.id === selectedUserId) : true;
        return matchesSearch && matchesStatus && matchesTag && matchesUser;
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

    const totalPages = Math.ceil((filteredItems?.length || 0) / itemsPerPage);
    const paginatedItems = filteredItems?.slice(currentPage * itemsPerPage, (currentPage + 1) * itemsPerPage);

    useEffect(() => {
        setCurrentPage(0);
    }, [searchTerm, statusFilter, selectedTagId, selectedUserId]);

    if (isLoading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen pt-6 px-4">
            <div className="container mx-auto">
                <div className="flex justify-between items-center mb-8 flex-wrap gap-4 -mt-12">
                    <div>
                        <h1 className="text-4xl font-bold text-slate-600">
                            Задачи
                        </h1>
                        <p className="text-slate-600 mt-3 text-xl mb-2 ">Управление задачами</p>
                    </div>
                    <button
                        onClick={openCreateModal}
                        className="bg-slate-600 text-white px-6 py-3 rounded-xl font-semibold text-xl transition-all duration-300 flex items-center gap-2 shadow-lg shadow-slate-500/25"
                    >
                        <Plus className="w-6 h-6" />
                        Добавить задачу
                    </button>
                </div>

{/* Filters */}
<div className=" bg-sky-100 backdrop-blur-sm  border border-slate-500 rounded-xl p-5 mb-12">
    <div className="flex items-center gap-2 mb-4">
        <h3 className="text-slate-600 text-xl font-semibold">Фильтры</h3>
    </div>
    <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-start">
        {/* Поиск по названию */}
        <div className="relative ">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-600" />
            <input
                type="text "
                placeholder="Название или описание..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className=" pl-9 pr-4 py-2 text-lg bg-white/5 border border-slate-300 rounded-lg text-slate-600 placeholder-slate-600 focus:outline-none focus:border-slate-500 transition-all"
            />
        </div>

        {/* Фильтр по статусу - список кнопок (ЗАМЕНА select) */}
        <div>
            <div className="flex  items-center gap-4">
            <label className="block text-lg font-medium text-slate-600 mb-0">Статус:</label>
                <button
                    type="button"
                    onClick={() => setStatusFilter('active')}
                    className={`px-3 py-2 rounded-lg text-s font-medium transition-all duration-200 ${
                        statusFilter === 'active'
                            ? 'bg-slate-600 text-white border-2 border-slate-600'
                            : 'bg-transparent text-slate-600 border-2 border-slate-400 hover:bg-slate-600'
                    }`}
                >
                    Активные
                </button>
                <button
                    type="button"
                    onClick={() => setStatusFilter('completed')}
                    className={`px-3 py-2 rounded-lg text-s font-medium transition-all duration-200 ${
                        statusFilter === 'completed'
                            ? 'bg-slate-600 text-white border-2 border-slate-600'
                            : 'bg-transparent text-slate-600 border-2 border-slate-400 hover:bg-slate-600'
                    }`}
                >
                    Выполненные
                </button>
            </div>
                {/* Кнопка сброса – отдельная строка, на всю ширину */}
                {(searchTerm || statusFilter !== 'all') && (
                    <div className="mt-4">
                        <button
                            onClick={() => {
                                setSearchTerm('');
                                setStatusFilter('all');
                            }}
                            className="w-full px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition flex items-center justify-center gap-2 mt-4"
                        >
                            <X className="w-4 h-4" />
                            Сбросить
                        </button>
                    </div>
                )}
        </div>
            {/* Колонка 3: Тег */}
            <div className = "flex items-center gap-2">
            <label className="block text-lg font-medium text-slate-600 mb-0">Тег:</label>
                <select  value={selectedTagId ?? ''}
                                onChange={(e) => setSelectedTagId(e.target.value ? Number(e.target.value) : null)}
                                className="w-full px-3 py-2 bg-white/5 border border-slate-300 rounded-lg text-slate-600 text-lg focus:outline-none focus:border-slate-500">
                    <option value="">Все теги</option>
                    {tags?.map(tag => <option key={tag.id} value={tag.id}>#{tag.name}</option>)}
                </select>
            </div>
            {/* Колонка 4: Пользователь */}
            <div className = "flex items-center gap-2">
                <label className="block text-lg font-medium text-slate-600 mb-1">Пользователь:</label>
                <select
                    value={selectedUserId ?? ''}
                    onChange={(e) => setSelectedUserId(e.target.value ? Number(e.target.value) : null)}
                    className="w-full px-3 py-2 bg-white/5 border border-slate-300 rounded-lg text-slate-600 text-lg focus:outline-none focus:border-slate-500"
                >
                    <option value="">Все пользователи</option>
                    {allUsers?.map(user => (
                        <option key={user.id} value={user.id}>{user.name}</option>
                    ))}
                </select>
            </div>
            </div>
    </div>


                {/* Items Grid */}
                <div className=" grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {paginatedItems?.map((item) => (
                        <motion.div
                            key={item.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            whileHover={{ scale: 1.02, y: -5 }}
                            className={` bg-sky-100 backdrop-blur-sm border border-slate-500 rounded-2xl p-6 cursor-pointer group ${
                                item.completed ? 'border-green-500/30' : 'border-slate-300'
                            }`}
                        >

                            <div className="flex justify-between items-start">
                               {/* Левая часть: иконка и название */}
                               <div className="flex items-center gap-3">
                               <button
                                 onClick={() => toggleComplete(item)}
                                 className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                                    item.completed ? 'bg-green-500/20' : 'bg-slate-300'
                                   }`}>
                                    {item.completed ? (
                                   <CheckCircle className="w-6 h-6 text-green-400" />
                                    ) : (
                                     <CheckSquare className="w-6 h-6 text-slate-600" />
                                    )}
                               </button>

                                   <div className = "flex flex-col">
                                   <div>
                                       <h3 className={`font-semibold text-xl ${item.completed ? 'text-green-300 line-through' : 'text-slate-600'}`}>
                                           {item.name}
                                       </h3>
                                   </div>
                                   <div className="mt-1">
                                    {item.description && (
                                        <p className="text-slate-600 text-lg mb-0 line-clamp-2">
                                            {item.description}
                                        </p>
                                    )}

                                   </div>
                                   </div>
                               </div>

                               {/* Правая часть: колонка с тегами (сверху) и иконками (снизу) */}
                               <div className="flex flex-col items-end gap-2">
                               <div className="flex flex-wrap gap-2">
                                </div>
                                   {/* Теги – в правом верхнем углу */}
                                   {item.tags && item.tags.length > 0 && (
                                       <div className="pt-3 flex flex-wrap gap-1 justify-end">
                                           {item.tags.map(tag => (
                                               <span key={tag.id} className="text-s px-2 py-0.5 bg-slate-500/20 text-slate-600 rounded-full whitespace-nowrap">
                                                   #{tag.name}
                                               </span>
                                           ))}
                                       </div>
                                   )}
                                   {/* Иконки действий */}
                                   <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                       <button
                                           onClick={() => openEditModal(item)}
                                           className="p-2 bg-yellow-500/20 text-slate-600 rounded-lg hover:bg-yellow-500/30 transition"
                                       >
                                           <Pencil className="w-4 h-4" />
                                       </button>
                                       <button
                                           onClick={() => deleteMutation.mutate(item.id)}
                                           className="p-2 bg-red-500/20 text-slate-600 rounded-lg hover:bg-red-500/30 transition"
                                       >
                                           <Trash2 className="w-4 h-4" />
                                       </button>
                                   </div>
                                   <button
                                     onClick={() => showItemTags(item)}
                                     className="text-sm text-slate-600 hover:text-slate-600 transition flex items-center gap-1"
                                     >
                                   <TagIcon className="w-4 h-4" />
                                    Теги {item.tags && item.tags.length > 0 && `(${item.tags.length})`}
                                 </button>
                               </div>
                           </div>

                            <div className="mt-4 pt-3 border-t border-white/10 flex flex-wrap gap-2">
                                <button
                                    onClick={() => showItemUsers(item)}
                                    className="text-sm text-slate-600 hover:text-slate-600 transition flex items-center gap-1"
                                >
                                    <Users className="w-4 h-4" />
                                    Пользователи
                                </button>
                                </div>
                            {/* Users preview  */}
                            {item.users && item.users.length > 0 && (
                                <div className="mt-2 flex flex-wrap gap-3">
                                    {item.users.map(user => (
                                       <div key={user.id} className="flex flex-col items-center text-center">
                                           <div className="w-8 h-8 rounded-full bg-slate-300 flex items-center justify-center text-sm font-bold text-slate-600 shadow-sm">
                                               {user.name.charAt(0).toUpperCase()}
                                           </div>
                                           <span className="text-[15px] text-slate-600 mt-1  max-w-[80px] text-center">
                                               {user.name}
                                           </span>
                                           <span className="text-[14px] text-slate-600">{user.roleName}</span>
                                       </div>
                                    ))}
                                </div>
                            )}

                        </motion.div>
                    ))}
                </div>

                {filteredItems?.length === 0 && (
                    <div className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-12 text-center">
                        <div className="text-6xl mb-4">ок</div>
                        <h3 className="text-xl font-semibold text-white mb-2">Задачи не найдены</h3>
                        <p className="text-white/40">Создайте первую задачу</p>
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

                {/* Modal for Create/Edit */}
                <Modal isOpen={isModalOpen} onClose={closeModal} title={selectedItem ? 'Редактирование задачи' : 'Добавление задачи'}>
                    <ItemForm
                        initialData={selectedItem || undefined}
                        users={allUsers || []}
                        tags={tags || []}
                        onSubmit={handleSubmit}
                        onCancel={closeModal}
                        isLoading={createMutation.isPending || updateMutation.isPending}
                    />
                </Modal>

<Modal isOpen={showTagsModal} onClose={() => setShowTagsModal(false)} title={`Управление тегами: ${selectedItemForTags?.name}`} size="lg">
    <div className="space-y-4">
        {/* Блок добавления тега */}
        <div>
            <label className="block text-lg font-medium text-slate-600 mb-2">Добавить тег</label>
            <div className="space-y-2 max-h-40 overflow-y-auto border border-slate-300 rounded-lg p-2 bg-white/50">
                {allTags.filter(t => !itemTags.some(it => it.id === t.id)).length === 0 ? (
                    <p className="text-slate-500 text-sm text-center py-4">Нет доступных тегов</p>
                ) : (
                    allTags.filter(t => !itemTags.some(it => it.id === t.id)).map(tag => (
                        <button
                            key={tag.id}
                            onClick={() => addTagToItem(tag.id)}
                            className="w-full text-left px-3 py-2 bg-slate-600 hover:bg-slate-500 text-white rounded-lg transition flex justify-between items-center group"
                        >
                            <span>#{tag.name}</span>
                            <span className="text-slate-300 opacity-0 group-hover:opacity-100">+ Добавить</span>
                        </button>
                    ))
                )}
            </div>
        </div>

{/* Блок текущих тегов */}
<div>
    <label className="block text-lg font-medium text-slate-600 mb-2">Текущие теги</label>
    <div className="flex flex-wrap gap-1.5 border border-slate-300 rounded-lg p-2 bg-white/50 min-h-[40px]">
        {itemTags.length === 0 ? (
            <p className="text-slate-500 text-xs w-full text-center py-1">Нет тегов</p>
        ) : (
            itemTags.map(tag => (
                <div key={tag.id} className="flex items-center gap-0.5 px-1 py-0.5 border border-slate-400 rounded-md bg-white/30">
                    <span className="text-xs text-slate-600">#{tag.name}</span>
                    <button
                        onClick={() => removeTagFromItem(tag.id)}
                        className="text-red-400 hover:text-red-600 transition"
                    >
                        <X className="w-3 h-3" />
                    </button>
                </div>
            ))
        )}
    </div>
</div>

        {/* Кнопка закрытия */}
        <div className="flex justify-end gap-3 pt-4">
            <button
                onClick={() => setShowTagsModal(false)}
                className="px-4 py-2 bg-slate-600 hover:bg-slate-700 text-white rounded-lg transition"
            >
                Закрыть
            </button>
        </div>
    </div>
</Modal>

{/* Modal for Users */}
<Modal isOpen={showUsersModal} onClose={() => setShowUsersModal(false)} title={`Пользователи, связанные с задачей: ${selectedItemForUsers?.name}`} size="lg">
    <div className="space-y-4">
        {/* Блок добавления пользователя */}
        <div>
            <label className="block text-lg font-medium text-slate-600 mb-2">Добавить пользователя</label>
            <div className="space-y-2 max-h-40 overflow-y-auto border border-slate-300 rounded-lg p-2 bg-white/50">
                {allUsers?.filter(u => !itemUsers.some(iu => iu.id === u.id)).length === 0 ? (
                    <p className="text-slate-500 text-center py-4">Нет доступных пользователей</p>
                ) : (
                    allUsers?.filter(u => !itemUsers.some(iu => iu.id === u.id)).map(user => (
                        <button
                            key={user.id}
                            onClick={() => addUserToItem(user.id)}
                            className="w-full text-left px-3 py-2 bg-slate-600 hover:bg-slate-500 text-white rounded-lg transition flex justify-between items-center group"
                        >
                            <span>{user.name}</span>
                            <span className="text-xs text-slate-300">{user.roleName}</span>
                            <span className="text-xs text-slate-300">{user.groupName}</span>
                            <span className="text-slate-300 opacity-0 group-hover:opacity-100">+ Добавить</span>
                        </button>
                    ))
                )}
            </div>
        </div>

        {/* Блок текущих пользователей */}
        <div>
            <label className="block text-lg font-medium text-slate-600 mb-2">Текущие пользователи</label>
            <div className="space-y-2 max-h-60 overflow-y-auto border border-slate-300 rounded-lg p-2 bg-white/50">
                {itemUsers.length === 0 ? (
                    <p className="text-slate-500 text-center py-4">Нет пользователей</p>
                ) : (
                    itemUsers.map(user => (
                        <div key={user.id} className="flex justify-between items-center p-3 bg-white/80 rounded-lg shadow-sm">
                            <div>
                                <span className="font-medium text-slate-700">{user.name}</span>
                                <p className="text-sm text-slate-500">{user.roleName} • {user.groupName}</p>
                            </div>
                            <button
                                onClick={() => removeUserFromItem(user.id)}
                                className="text-red-500 hover:text-red-700 transition"
                            >
                                <X className="w-4 h-4" />
                            </button>
                        </div>
                    ))
                )}
            </div>
        </div>

        {/* Кнопка закрытия */}
        <div className="flex justify-end gap-3 pt-4">
            <button
                onClick={() => setShowUsersModal(false)}
                className="px-4 py-2 bg-slate-600 hover:bg-slate-700 text-white rounded-lg transition"
            >
                Закрыть
            </button>
        </div>
    </div>
</Modal>
            </div>
        </div>
    );
};

export { ItemList };