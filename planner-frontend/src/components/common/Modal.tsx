import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface ModalProps {
    isOpen: boolean;
    onClose: () => void;
    title: string;
    children: React.ReactNode;
    size?: 'sm' | 'md' | 'lg' | 'xl';
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children, size = 'md' }) => {
    useEffect(() => {
        if (isOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, [isOpen]);

    const sizes = {
        sm: 'max-w-md',
        md: 'max-w-lg',
        lg: 'max-w-2xl',
        xl: 'max-w-4xl',
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50">
            {/* Подложка */}
            <div className="fixed inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose} />

            {/*  модальное окно */}
            <div className={`fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 ${sizes[size]} w-full bg-sky-100 border border-slate-500 rounded-2xl shadow-xl max-h-[90vh] overflow-hidden`}>
                <div className="flex justify-between items-center p-6 ">
                    <h2 className="text-xl font-bold text-slate-700">{title}</h2>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-slate-200 rounded-full transition-all duration-200"
                    >
                        <X className="w-5 h-5 text-slate-600 hover:text-slate-800" />
                    </button>
                </div>
                <div className="p-6 overflow-y-auto max-h-[calc(90vh-80px)]">
                    {children}
                </div>
            </div>
        </div>
    );
};

export { Modal };