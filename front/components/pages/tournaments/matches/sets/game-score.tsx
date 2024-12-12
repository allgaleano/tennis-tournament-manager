interface GameScoreProps {
  games: number;
  tiebreakGames: number | null;
  isWinner: boolean;
}

const GameScore = ({ games, tiebreakGames, isWinner }: GameScoreProps) => {
  if (tiebreakGames !== null) {
    return (
      <span className={isWinner ? "font-bold" : ""}>
        {games}
        <sup className="text-xs">{tiebreakGames}</sup>
      </span>
    );
  }
  return <span className={isWinner ? "font-bold" : ""}>{games}</span>;
};

export default GameScore;