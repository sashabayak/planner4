import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import Navbar from './components/common/Navbar';
import Dashboard from './pages/Dashboard';
import { UsersPage } from './pages/UsersPage';
import { GroupsPage } from './pages/GroupsPage';
import { RolesPage } from './pages/RolesPage';
import { ItemsPage } from './pages/ItemsPage';
import { TagsPage } from './pages/TagsPage';

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            refetchOnWindowFocus: false,
            retry: 1,
            staleTime: 5 * 60 * 1000,
        },
    },
});

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <Router>
                <Toaster
                    position="top-right"
                    toastOptions={{
                        style: {
                            background: '#1a1a2e',
                            color: '#fff',
                            borderRadius: '1rem',
                            padding: '12px 20px',
                            border: '1px solid rgba(139, 92, 246, 0.3)',
                        },
                    }}
                />
                <div className="min-h-screen bg-black">
                    <Navbar />
                    <main className="relative z-10">
                        <Routes>
                            <Route path="/" element={<Dashboard />} />
                            <Route path="/users" element={<UsersPage />} />
                            <Route path="/groups" element={<GroupsPage />} />
                            <Route path="/roles" element={<RolesPage />} />
                            <Route path="/items" element={<ItemsPage />} />
                            <Route path="/tags" element={<TagsPage />} />
                            <Route path="*" element={<Navigate to="/" replace />} />
                        </Routes>
                    </main>
                </div>
            </Router>
        </QueryClientProvider>
    );
}

export default App;