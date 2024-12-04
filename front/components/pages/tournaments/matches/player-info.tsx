import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Match, Player } from "@/types";

interface PlayerInfoProps {
  player: Player | null;
  match: Match;
  className?: string;
}

const PlayerInfo = ({ player, match, className }: PlayerInfoProps) => {
  const isPlayerWinner = (playerId: number | undefined) => {
    if (!playerId) return false;
    return match.winner?.id === playerId;
  };

  if (!player) {
    return (
      <div className={`text-muted-foreground w-full flex justify-center py-1 ${className}`}>-</div>
    )
  }
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger className={`${isPlayerWinner(player?.id) ? 'text-success bg-success/10 border-success font-semibold' : ''} w-full flex justify-center py-1 ${className}`}>
          {player.name} {player.surname}
        </TooltipTrigger>
        <TooltipContent>
          <div className="space-y-1">
            <p className="font-semibold">{player.username}</p>
            <p>{player.email}</p>
          </div>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};

export default PlayerInfo;