import { Set } from "@/types";
import ScoreBox from "./score-box";

interface PlayerScoreRowProps {
  sets: (Set | null)[];
  isPlayer1: boolean;
  setsWon: number | null;
  isMatchWinner: boolean;
  className?: string;
}

const PlayerScoreRow = ({ 
  sets, 
  isPlayer1, 
  setsWon, 
  isMatchWinner, 
  className = "" 
}: PlayerScoreRowProps) => (
  <div className={`flex justify-between ${className}`}>
    {sets.map((set, index) => (
      <ScoreBox
        key={`p${isPlayer1 ? "1" : "2"}-${index}`}
        set={set}
        isPlayer1={isPlayer1}
        isLast={false}
      />
    ))}
    <ScoreBox
      set={null}
      isPlayer1={isPlayer1}
      isLast={true}
      score={setsWon}
    />
  </div>
);

export default PlayerScoreRow;