import PlayerInfo from "@/components/pages/tournaments/matches/player-info";
import { Match } from "@/types";
import Sets from "@/components/pages/tournaments/matches/sets/sets";
import MatchResultsDialog from "./sets/match-result-sheet";

interface MatchCardProps {
  match: Match;
  tournamentId: number;
}
const MatchCard = ({ 
  match,
  tournamentId
}: MatchCardProps) => {

  return (
    <div className={`border rounded-sm shadow-sm ${match.completed ? "border-success" : ""}`}>
      <div>
        <div className="flex min-w-60 flex-col items-center justify-between">
          <PlayerInfo player={match.player1} match={match} className="border-b rounded-t-sm" />
          {match.completed ? (
            <Sets match={match} />
          ) : (
            (match.player1 && match.player2) ? (
              <MatchResultsDialog match={match} tournamentId={tournamentId}/>
            ) : (
              <div className="flex justify-center items-center h-20 text-muted-foreground">vs</div>
            )
          )}
          <PlayerInfo player={match.player2} match={match} className="border-t rounded-b-sm" />
        </div>
      </div>
    </div>
  );
};

export default MatchCard;