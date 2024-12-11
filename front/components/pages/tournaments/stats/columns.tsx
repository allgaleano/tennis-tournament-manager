"use client";

import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { cn } from "@/lib/utils";
import { Player, PlayerTournamentStats } from "@/types";
import { ColumnDef } from "@tanstack/react-table";

export const columns: ColumnDef<PlayerTournamentStats>[] = [
  {
    header: "Pos.",
    cell: function Cell({ row }) {
      const position = row.index + 1;
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
      const position = row.index + 1;
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
      const points = row.original.points;
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
    header: "Partidos ganados",
    accessorFn: (row) => row.matchesWon,
    id: "matchesWon",
  }, 
  {
    header: "Partidos perdidos",
    accessorFn: (row) => row.matchesLost,
    id: "matchesLost",
  }, 
  {
    header: "Sets ganados",
    accessorFn: (row) => row.setsWon,
    id: "setsWon",
  },
  {
    header: "Sets perdidos",
    accessorFn: (row) => row.setsLost,
    id: "setsLost",
  },
  {
    header: "Juegos ganados",
    accessorFn: (row) => row.gamesWon,
    id: "gamesWon",
  },
  {
    header: "Juegos perdidos",
    accessorFn: (row) => row.gamesLost,
    id: "gamesLost",
  },
  {
    header: "Tiebreak ganados",
    accessorFn: (row) => row.tiebreakGamesWon,
    id: "tiebreakGamesWon",
  },
  {
    header: "Tiebreak perdidos",
    accessorFn: (row) => row.tiebreakGamesLost,
    id: "tiebreakGamesLost",
  }
]