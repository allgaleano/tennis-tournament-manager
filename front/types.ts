export interface ApiResponse {
  title: string;
  description: string;
  errorCode: string;
}

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

export interface Match {
  id: number;
  player1: Player | null;
  player2: Player | null;
  round: string;
  winner: Player | null;
  completed: boolean;
  player1SetsWon: number | null;
  player2SetsWon: number | null;
  sets: Set[];
}

export interface Set {
  setNumber: number;
  player1Games: number;
  player2Games: number;
  tiebreak: boolean;
  player1TiebreakGames: number | null;
  player2TiebreakGames: number | null;
}

export interface Player {
  id: number;
  name: string;
  surname: string;
  email: string;
  username: string;
}

export type PlayerTournamentStats = {
  player: Player;
  matchesPlayed: number;
  matchesWon: number;
  matchesLost: number;
  setsWon: number;
  setsLost: number;
  gamesWon: number;
  gamesLost: number;
  tiebreakGamesWon: number;
  tiebreakGamesLost: number;
  points: number;
}

export type PlayerGlobalStats = {
  player: Player;
  rankingPoints: number;
  rankingPosition: number | null;
  tournamentsPlayed: number;
  tournamentsWon: number;
  totalMatchesPlayed: number;
  totalMatchesWon: number;
  totalMatchesLost: number;
  totalSetsWon: number;
  totalSetsLost: number;
  totalGamesWon: number;
  totalGamesLost: number;
  totalTiebreakGamesWon: number;
  totalTiebreakGamesLost: number;
}

export type PlayerGlobalStatsDisplayList = {
  _embedded: {
    playerStatsDTOList: PlayerGlobalStats[];
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}