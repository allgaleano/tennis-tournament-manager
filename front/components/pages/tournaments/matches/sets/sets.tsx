import { Match, Set } from "@/types";
import PlayerScoreRow from "./player-score-row";

interface SetsProps {
  match: Match;
}

const Sets = ({ match }: SetsProps) => {
  // Fill remaining sets with null to always show 5 sets
  const filledSets: (Set | null)[] = [...match.sets, ...Array(5 - match.sets.length).fill(null)];
  
  return (
    <div>
      <PlayerScoreRow
        sets={filledSets}
        isPlayer1={true}
        setsWon={match.player1SetsWon}
        isMatchWinner={match.winner?.id === match.player1?.id}
      />
      <PlayerScoreRow
        sets={filledSets}
        isPlayer1={false}
        setsWon={match.player2SetsWon}
        isMatchWinner={match.winner?.id === match.player2?.id}
        className="border-t"
      />
    </div>
  );
};

export default Sets;