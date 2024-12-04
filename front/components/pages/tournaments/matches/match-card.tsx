import PlayerInfo from "./player-info";
import { Match } from "@/types";


const MatchCard = ({ match }: { match: Match }) => {
  
  return (
    <div className="w-52 border rounded-sm shadow-sm">
      <div>
        <div className="flex justify-between items-center">
          {match.completed && (
            <span className="text-sm text-green-600">Completado</span>
          )}
        </div>
        <div className="flex flex-col gap-1 items-center justify-between">
            <PlayerInfo player={match.player1} match={match} className="border-b"/>
          <div className="text-center text-sm text-muted-foreground">vs</div>
            <PlayerInfo player={match.player2} match={match} className="border-t"/>
        </div>
      </div>
    </div>
  );
};

export default MatchCard;