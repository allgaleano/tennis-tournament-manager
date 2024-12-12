"use client";

import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { cn } from "@/lib/utils";
import { Player, PlayerGlobalStats } from "@/types";
import { ColumnDef } from "@tanstack/react-table";

export const columns: ColumnDef<PlayerGlobalStats>[] = [
  {
    header: "Pos.",
    cell: function Cell({ row }) {
      const position = row.original.rankingPosition;
      return (
        <span
          className={cn(
            "grid place-items-center w-7 h-7 rounded-[20%] font-semibold",
            position === 1 && "bg-yellow-400 text-yellow-900",
            position === 2 && "bg-gray-200 text-gray-900",
            position === 3 && "bg-amber-700 text-amber-100",
          )}
        >
          {position}º
        </span>
      ); 
    },
  },
  {
    header: "Nombre",
    accessorFn: (row) => row.player,
    cell: function Cell({ row }) {
      const player: Player =  row.original.player;
      const position = row.original.rankingPosition;
      return (
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
              <span>{position === 1 && <span>👑</span>} {player.name} {player.surname}</span>
            </TooltipTrigger>
            <TooltipContent>
              <div className="flex flex-col gap-2">
                <p className="font-semibold">{player.username}</p>
                <p>{player.email}</p>
              </div>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      )
    }
  },
  {
    header: "Puntos",
    cell: function Cell({ row }) {
      const points = row.original.rankingPoints;
      const position = row.index + 1;
      return (
        <span
          className={cn(
            position === 1 && "text-yellow-600 font-semibold",
            position === 2 && "text-gray-600 font-semibold",
            position === 3 && "text-amber-700 font-semibold",
          )}
        >
          {points}
        </span>
      )
    }
  },
  {
    header: "Torneos Jugados",
    accessorFn: (row) => row.tournamentsPlayed,
    id: "tournamentsPlayed",
  },
  {
    header: "Torneos G.",
    cell: function Cell({ row }) {
      const tournamentsWon = row.original.tournamentsWon;
      return (
        <span className={cn(
          tournamentsWon > 0 && "text-success font-semibold",
          tournamentsWon === 0 && "text-gray-500"
        )}>{tournamentsWon}</span>
      );
    }
  },
  {
    header: "Partidos Jugados",
    accessorFn: (row) => row.totalMatchesPlayed,
    id: "matchesPlayed",
  },
  {
    header: "Partidos G.",
    cell: function Cell({ row }) {
      const matchesWon = row.original.totalMatchesWon;
      return (
        <span className={cn(
          matchesWon > 0 && "text-success font-semibold",
          matchesWon === 0 && "text-gray-500"
        )}>{matchesWon}</span>
      );
    }
  },
  {
    header: "Partidos P.",
    cell: function Cell({ row }) {
      const matchesLost = row.original.totalMatchesLost;
      return (
        <span className={cn(
          matchesLost > 0 && "text-destructive font-semibold",
          matchesLost === 0 && "text-gray-500"
        )}>{matchesLost}</span>
      );
    }
  },
  {
    header: "Sets G.",
    cell: function Cell({ row }) {
      const setsWon = row.original.totalSetsWon;
      return (
        <span className={cn(
          setsWon > 0 && "text-success font-semibold",
          setsWon === 0 && "text-gray-500"
        )}>{setsWon}</span>
      );
    }
  },
  {
    header: "Sets P.",
    cell: function Cell({ row }) {
      const setsLost = row.original.totalSetsLost;
      return (
        <span className={cn(
          setsLost > 0 && "text-destructive font-semibold",
          setsLost === 0 && "text-gray-500"
        )}>{setsLost}</span>
      );
    }
  },
  {
    header: "Juegos G.",
    cell: function Cell({ row }) {
      const gamesWon = row.original.totalGamesWon;
      return (
        <span className={cn(
          gamesWon > 0 && "text-success font-semibold",
          gamesWon === 0 && "text-gray-500"
        )}>{gamesWon}</span>
      );
    }
  },
  {
    header: "Juegos P.",
    cell: function Cell({ row }) {
      const gamesLost = row.original.totalGamesLost;
      return (
        <span className={cn(
          gamesLost > 0 && "text-destructive font-semibold",
          gamesLost === 0 && "text-gray-500"
        )}>{gamesLost}</span>
      );
    }
  },
  {
    header: "Tiebreaks G.",
    cell: function Cell({ row }) {
      const tiebreakGamesWon = row.original.totalTiebreakGamesWon;
      return (
        <span className={cn(
          tiebreakGamesWon > 0 && "text-success font-semibold",
          tiebreakGamesWon === 0 && "text-gray-500"
        )}>{tiebreakGamesWon}</span>
      );
    }
  },
  {
    header: "Tiebreaks P.",
    cell: function Cell({ row }) {
      const tiebreakGamesLost = row.original.totalTiebreakGamesLost;
      return (
        <span className={cn(
          tiebreakGamesLost > 0 && "text-destructive font-semibold",
          tiebreakGamesLost === 0 && "text-gray-500"
        )}>{tiebreakGamesLost}</span>
      );
    }
  }
]