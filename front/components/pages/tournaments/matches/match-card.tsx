import PlayerInfo from "./player-info";
import { Match } from "@/types";
import Sets from "./sets/sets";


const MatchCard = ({ match }: { match: Match }) => {

  return (
    <div className={`border rounded-sm shadow-sm ${match.completed ? "border-success" : ""}`}>
      <div>
        <div className="flex flex-col items-center justify-between">
          <PlayerInfo player={match.player1} match={match} className="border-b rounded-t-sm" />
          <Sets match={match} />
          <PlayerInfo player={match.player2} match={match} className="border-t rounded-b-sm" />
        </div>
      </div>
    </div>
  );
};

export default MatchCard;