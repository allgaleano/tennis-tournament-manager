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
  confirmedAccount: boolean;
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

export type Tournament = {
  id: number;
  name: string;
  registrationDeadline: string;
  maxPlayers: number;
  status: string;
  selectedPlayersCount: number;
}

export type Enrollment = {
  id: number;
  player: {
    id: number;
    name: string;
    surname: string;
    email: string;
    username: string;
  };
  status: string;
};

export interface EnrollmentDisplayList {
  _embedded: {
    tournamentEnrollmentDTOList: Enrollment[];
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}