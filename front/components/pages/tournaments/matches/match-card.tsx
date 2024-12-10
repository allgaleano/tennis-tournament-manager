import PlayerInfo from "@/components/pages/tournaments/matches/player-info";
import { Match } from "@/types";
import Sets from "@/components/pages/tournaments/matches/sets/sets";
import ModalFormWrapper from "./sets/modal-form-wrapper";
import MatchResultsForm from "./sets/match-results-form";

interface MatchCardProps {
  match: Match;
  tournamentId: number;
  isAdmin: boolean;
  previousRoundsCompleted: boolean;
}
const MatchCard = ({ 
  match,
  tournamentId,
  isAdmin,
  previousRoundsCompleted,
}: MatchCardProps) => {
  const showModalForm = isAdmin && previousRoundsCompleted && !match.completed && match.player1 && match.player2;
  return (
    <div className={`border rounded-sm shadow-sm ${match.completed ? "border-success" : ""}`}>
      <div>
        <div className="flex min-w-60 flex-col items-center justify-between">
          <PlayerInfo player={match.player1} match={match} className="border-b rounded-t-sm" />
          {match.completed ? (
            <Sets match={match} />
          ) : (
            (showModalForm) ? (
              <ModalFormWrapper
                match={match}
                tournamentId={tournamentId}
                variant="sheet"
                className="h-20 grid place-items-center"
              >
                <MatchResultsForm match={match} tournamentId={tournamentId} />
              </ModalFormWrapper>
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