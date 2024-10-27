export interface UserData {
  id: number;
  name: string;
  surname: string;
  phoneNumber: string;
  createdAt: string;
  username: string;
  email: string;
  role: string;
}

export type User = {
  id: number;
  name: string;
  phoneNumber: string;
  createdAt: string;
  username: string;
  email: string;
  enabledAccount: boolean;
  role: string;
}

export interface UserDisplayList {
  _embedded: {
    userDisplayDTOList: User[];
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
} 