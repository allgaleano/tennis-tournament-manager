"use client";

import { useTournamentMatches } from '@/hooks/useTournamentMatches';
import { Match } from '@/types';
import MatchCard from './match-card';

interface TournamentBracketProps {
  tournamentId: number;
}

const TournamentMatches = ({ tournamentId }: TournamentBracketProps) => {
  const { matches, loading, error } = useTournamentMatches(tournamentId);
  
  if (loading) return <div>Cargando...</div>;
  if (error) return <div className="text-destructive">{error}</div>;

  const getRoundDisplayName = (round: string) => {
    const roundNames = {
      'ROUND_16': 'Dieciseisavos de final',
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
                <MatchCard key={match.id} match={match} />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TournamentMatches;