import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { userService } from '../services/userService';
import { groupService } from '../services/groupService';
import { roleService } from '../services/roleService';
import { itemService } from '../services/itemService';
import { tagService } from '../services/tagService';
import { Users, FolderKanban, BadgeCheck, CheckSquare, Tags, TrendingUp } from 'lucide-react';

const StatCard: React.FC<{ label: string; value: string | number; icon: React.ElementType; delay: number; trend?: string }> =
    ({ label, value, icon: Icon, delay, trend }) => {
        return (
            <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay, duration: 0.5 }}
                whileHover={{ scale: 1.02, y: -5 }}
                className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6 cursor-pointer group"
            >
                <div className="flex items-center justify-between">
                    <div>
                        <p className="text-white/50 text-sm">{label}</p>
                        <p className="text-3xl font-bold gradient-text mt-2">{value}</p>
                        {trend && (
                            <p className="text-xs text-green-400 mt-1 flex items-center gap-1">
                                <TrendingUp className="w-3 h-3" />
                                {trend}
                            </p>
                        )}
                    </div>
                    <div className="w-12 h-12 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
                        <Icon className="w-6 h-6 text-purple-400" />
                    </div>
                </div>
            </motion.div>
        );
    };

const Dashboard: React.FC = () => {
    const { data: users } = useQuery({ queryKey: ['users'], queryFn: userService.getAll });
    const { data: groups } = useQuery({ queryKey: ['groups'], queryFn: groupService.getAll });
    const { data: roles } = useQuery({ queryKey: ['roles'], queryFn: roleService.getAll });
    const { data: items } = useQuery({ queryKey: ['items'], queryFn: itemService.getAll });
    const { data: tags } = useQuery({ queryKey: ['tags'], queryFn: tagService.getAll });

    const stats = [
        { label: 'Пользователей', value: users?.length || 0, icon: Users, delay: 0.1, trend: '+8%' },
        { label: 'Групп', value: groups?.length || 0, icon: FolderKanban, delay: 0.2, trend: '+2' },
        { label: 'Ролей', value: roles?.length || 0, icon: BadgeCheck, delay: 0.3, trend: '+1' },
        { label: 'Задач', value: items?.length || 0, icon: CheckSquare, delay: 0.4, trend: '+15%' },
        { label: 'Тегов', value: tags?.length || 0, icon: Tags, delay: 0.5, trend: '+5' },
    ];

    const completedItems = items?.filter(i => i.completed).length || 0;
    const activeItems = (items?.length || 0) - completedItems;

    const recentItems = [...(items || [])]
        .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
        .slice(0, 5);

    const formatDate = (dateString: string) => {
        try {
            return new Date(dateString).toLocaleDateString('ru-RU', {
                day: 'numeric',
                month: 'short'
            });
        } catch {
            return dateString;
        }
    };

    return (
        <div className="min-h-screen pt-20 px-4">
            <div className="container mx-auto">
                <motion.div
                    initial={{ opacity: 0, y: 30 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6 }}
                    className="text-center mb-12"
                >
                    <motion.div
                        animate={{ scale: [1, 1.05, 1] }}
                        transition={{ duration: 2, repeat: Infinity }}
                        className="inline-block"
                    >
                        <div className="w-20 h-20 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-2xl flex items-center justify-center mx-auto border border-purple-500/30">
                            <CheckSquare className="w-10 h-10 text-purple-400" />
                        </div>
                    </motion.div>
                    <h1 className="text-4xl font-bold gradient-text mt-4">Панель управления</h1>
                    <p className="text-white/50 mt-2">Planner - управление пользователями и задачами</p>
                </motion.div>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6 mb-8">
                    {stats.map((stat) => (
                        <StatCard key={stat.label} {...stat} />
                    ))}
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Статус задач */}
                    <motion.div
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.6 }}
                        className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6"
                    >
                        <h2 className="text-xl font-semibold text-white mb-4 flex items-center gap-2">
                            <CheckSquare className="w-5 h-5 text-purple-400" />
                            Статус задач
                        </h2>
                        <div className="space-y-3">
                            <div className="flex justify-between items-center p-3 bg-white/5 rounded-xl">
                                <span className="text-white/80">Всего задач</span>
                                <span className="text-purple-400 font-bold text-xl">{items?.length || 0}</span>
                            </div>
                            <div className="flex justify-between items-center p-3 bg-white/5 rounded-xl">
                                <span className="text-white/80">Активные</span>
                                <span className="text-yellow-400 font-bold text-xl">{activeItems}</span>
                            </div>
                            <div className="flex justify-between items-center p-3 bg-white/5 rounded-xl">
                                <span className="text-white/80">Выполненные</span>
                                <span className="text-green-400 font-bold text-xl">{completedItems}</span>
                            </div>
                        </div>
                    </motion.div>

                    {/* Последние задачи */}
                    <motion.div
                        initial={{ opacity: 0, x: 20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.7 }}
                        className="bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6"
                    >
                        <h2 className="text-xl font-semibold text-white mb-4 flex items-center gap-2">
                            <CheckSquare className="w-5 h-5 text-purple-400" />
                            Последние задачи
                        </h2>
                        <div className="space-y-3">
                            {recentItems.length === 0 ? (
                                <p className="text-white/40 text-center py-4">Нет задач</p>
                            ) : (
                                recentItems.map(item => (
                                    <div key={item.id} className="flex justify-between items-center p-3 bg-white/5 rounded-xl">
                                        <div>
                                            <p className={`font-medium ${item.completed ? 'text-green-300 line-through' : 'text-white'}`}>
                                                {item.name}
                                            </p>
                                            <p className="text-xs text-white/40">{formatDate(item.createdAt)}</p>
                                        </div>
                                        <span className={`text-xs px-2 py-1 rounded-full ${item.completed ? 'bg-green-500/20 text-green-300' : 'bg-yellow-500/20 text-yellow-300'}`}>
                                            {item.completed ? 'Выполнена' : 'Активна'}
                                        </span>
                                    </div>
                                ))
                            )}
                        </div>
                    </motion.div>
                </div>

                {/* Краткая информация по группам */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.8 }}
                    className="mt-6 bg-black/40 backdrop-blur-sm border border-purple-500/30 rounded-2xl p-6"
                >
                    <h2 className="text-xl font-semibold text-white mb-4 flex items-center gap-2">
                        <FolderKanban className="w-5 h-5 text-purple-400" />
                        Группы и роли
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <p className="text-white/60 text-sm mb-2">Группы</p>
                            <div className="flex flex-wrap gap-2">
                                {groups?.map(group => (
                                    <span key={group.id} className="px-3 py-1 bg-purple-500/20 text-purple-300 rounded-full text-sm">
                                        {group.name}
                                    </span>
                                ))}
                            </div>
                        </div>
                        <div>
                            <p className="text-white/60 text-sm mb-2">Роли</p>
                            <div className="flex flex-wrap gap-2">
                                {roles?.map(role => (
                                    <span key={role.id} className="px-3 py-1 bg-pink-500/20 text-pink-300 rounded-full text-sm">
                                        {role.name}
                                    </span>
                                ))}
                            </div>
                        </div>
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default Dashboard;