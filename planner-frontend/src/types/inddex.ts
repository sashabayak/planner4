export interface Group {
    id: number;
    name: string;
}

export interface Role {
    id: number;
    name: string;
}

export interface Tag {
    id: number;
    name: string;
}

export interface Item {
    id: number;
    name: string;
    description: string;
    completed: boolean;
    createdAt: string;
    tags?: Tag[];
}

export interface User {
    id: number;
    name: string;
    birthDate: string;
    roleId: number;
    roleName: string;
    groupId: number;
    groupName: string;
    items?: Item[];
}

export interface UserCreateDto {
    name: string;
    birthDate: string;
    roleId: number;
    groupId: number;
}

export interface UserUpdateDto {
    name?: string;
    birthDate?: string;
    roleId?: number;
    groupId?: number;
}

export interface GroupCreateDto {
    name: string;
}

export interface RoleCreateDto {
    name: string;
}

export interface ItemCreateDto {
    name: string;
    description?: string;
    completed?: boolean;
    userIds?: number[];
    tagIds?: number[];
}

export interface ItemUpdateDto {
    name?: string;
    description?: string;
    completed?: boolean;
}