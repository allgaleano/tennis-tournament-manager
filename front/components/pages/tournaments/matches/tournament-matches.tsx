"use client";

import { useTournamentMatches } from '@/hooks/useTournamentMatches';
import { Match } from '@/types';
import MatchCard from './match-card';
import { BarLoader } from 'react-spinners';

interface TournamentMatchesProps {
  tournamentId: number;
  isAdmin: boolean;
}

const TournamentMatches = ({ tournamentId, isAdmin }: TournamentMatchesProps) => {
  const { matches, loading, error } = useTournamentMatches(tournamentId);
  
  if (loading) return (
    <div className="grid place-items-center w-full h-[600px]">
      <BarLoader className="text-primary"/>
    </div>
  );

  if (error) return <div className="text-destructive">{error}</div>;

  const getRoundDisplayName = (round: string) => {
    const roundNames = {
      'ROUND_16': 'Octavos de final',
      'QUARTER_FINALS': 'Cuartos de final',
      'SEMIFINAL': 'Semifinal',
      'FINAL': 'Final'
    };
    return roundNames[round as keyof typeof roundNames] || round;
  };

  const roundOrder = ['ROUND_16', 'QUARTER_FINALS', 'SEMIFINAL', 'FINAL'];

  const matchesByRound = matches.reduce((acc, match) => {
    if (!acc[match.round]) {
      acc[match.round] = [];
    }
    acc[match.round].push(match);
    return acc;
  }, {} as Record<string, Match[]>);

  const areAllMatchesCompletedInRound = (round: string) => {
    return matchesByRound[round]?.every(match => match.completed) ?? true;
  };

  const arePreviousRoundsCompleted = (currentRound: string) => {
    const currentRoundIndex = roundOrder.indexOf(currentRound);
    if (currentRoundIndex <= 0) return true;

    return roundOrder
      .slice(0, currentRoundIndex)
      .every(round => areAllMatchesCompletedInRound(round));
  };

  const sortedRounds = Object.keys(matchesByRound).sort(
    (a, b) => roundOrder.indexOf(a) - roundOrder.indexOf(b)
  );

  return (
    <div className="space-y-4 mt-6">
      <div className="flex gap-4">
        {sortedRounds.map((round) => (
          <div key={round} className="w-full flex space-y-4 flex-col items-center border p-4 rounded-sm shadow-sm">
            <h2 className="font-semibold">
              {getRoundDisplayName(round)}
            </h2>
            <div className="flex flex-col gap-4 h-full justify-center">
              {matchesByRound[round].map((match) => (
                <MatchCard 
                  key={match.id} 
                  match={match} 
                  tournamentId={tournamentId} 
                  isAdmin={isAdmin}
                  previousRoundsCompleted={arePreviousRoundsCompleted(round)}
                />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TournamentMatches;