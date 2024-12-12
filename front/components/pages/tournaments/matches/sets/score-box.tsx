import { Set } from "@/types";
import GameScore from "./game-score";

interface ScoreBoxProps {
  set: Set | null;
  isPlayer1: boolean;
  isLast?: boolean;
  score?: number | null;
  isMatchWinner?: boolean;
}

const ScoreBox = ({ 
  set, 
  isPlayer1, 
  isLast = false, 
  score = null ,
  isMatchWinner
}: ScoreBoxProps) => {
  const isWinner: boolean = set 
    ? (isPlayer1 ? set.player1Games > set.player2Games : set.player2Games > set.player1Games) 
    : false;

  return (
    <div 
      className={`
        flex w-10 h-10 p-2 items-center justify-center
        ${!isLast ? "border-r" : ""} 
        ${isWinner ||isMatchWinner ? "font-bold" : ""}
      `}
    >
      {score !== null && <span className="text-base">{score}</span>}
      {set && (
        <GameScore
          games={isPlayer1 ? set.player1Games : set.player2Games}
          tiebreakGames={isPlayer1 ? set.player1TiebreakGames : set.player2TiebreakGames}
          isWinner={isWinner}
        />
      )}
    </div>
  );
};

export default ScoreBox;