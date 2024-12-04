import PlayerInfo from "./player-info";
import { Match } from "@/types";


const MatchCard = ({ match }: { match: Match }) => {
  
  return (
    <div className={`w-52 border rounded-sm shadow-sm ${match.completed ? "border-success" : ""}`}>
      <div>
        <div className="flex flex-col gap-1 items-center justify-between">
            <PlayerInfo player={match.player1} match={match} className="border-b rounded-t-sm"/>
          <div className="text-center text-sm text-muted-foreground">vs</div>
            <PlayerInfo player={match.player2} match={match} className="border-t rounded-b-sm"/>
        </div>
      </div>
    </div>
  );
};

export default MatchCard;