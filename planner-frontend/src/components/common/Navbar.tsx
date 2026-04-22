import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
    LayoutDashboard,
    Users,
    FolderKanban,
    BadgeCheck,
    CheckSquare,
    Tags,
    Menu,
    X
} from 'lucide-react';

const Navbar: React.FC = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setScrolled(window.scrollY > 20);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const navItems = [
        { path: '/', label: 'Дашборд', icon: LayoutDashboard },
        { path: '/users', label: 'Пользователи', icon: Users },
        { path: '/groups', label: 'Группы', icon: FolderKanban },
        { path: '/roles', label: 'Роли', icon: BadgeCheck },
        { path: '/items', label: 'Задачи', icon: CheckSquare },
        { path: '/tags', label: 'Теги', icon: Tags },
    ];

    return (
        <>
            <nav className={`fixed top-0 left-0 right-0 z-50 transition-all duration-500 ${
                scrolled ? 'bg-black/80 backdrop-blur-xl border-b border-purple-500/20' : 'bg-transparent'
            }`}>
                <div className="container mx-auto px-4">
                    <div className="flex justify-between items-center h-16">
                        <Link to="/" className="flex items-center space-x-3 group">
                            <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-pink-500 rounded-xl flex items-center justify-center shadow-lg">
                                <span className="text-white text-xl">📋</span>
                            </div>
                            <span className="text-xl font-bold gradient-text">Planner</span>
                        </Link>

                        {/* Desktop Navigation */}
                        <div className="hidden md:flex items-center space-x-1">
                            {navItems.map((item) => {
                                const Icon = item.icon;
                                return (
                                    <Link
                                        key={item.path}
                                        to={item.path}
                                        className="relative px-4 py-2 rounded-xl text-white/70 hover:text-white hover:bg-white/5 transition-all duration-300 flex items-center gap-2 group"
                                    >
                                        <Icon className="w-4 h-4 group-hover:scale-110 transition-transform" />
                                        <span className="font-medium">{item.label}</span>
                                        <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-purple-500 to-pink-500 group-hover:w-full transition-all duration-300"></span>
                                    </Link>
                                );
                            })}
                        </div>

                        {/* Mobile Menu Button */}
                        <button
                            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                            className="md:hidden p-2 rounded-lg bg-white/10"
                        >
                            {isMobileMenuOpen ? <X className="w-5 h-5 text-white" /> : <Menu className="w-5 h-5 text-white" />}
                        </button>
                    </div>
                </div>
            </nav>

            {/* Mobile Navigation */}
            {isMobileMenuOpen && (
                <div className="fixed top-16 left-0 right-0 z-40 bg-black/95 backdrop-blur-xl border-b border-purple-500/20 md:hidden">
                    <div className="container mx-auto px-4 py-4 space-y-2">
                        {navItems.map((item) => {
                            const Icon = item.icon;
                            return (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    onClick={() => setIsMobileMenuOpen(false)}
                                    className="flex items-center gap-3 px-4 py-3 rounded-xl text-white/70 hover:text-white hover:bg-white/5 transition-all"
                                >
                                    <Icon className="w-5 h-5" />
                                    <span className="font-medium">{item.label}</span>
                                </Link>
                            );
                        })}
                    </div>
                </div>
            )}
        </>
    );
};

export default Navbar;