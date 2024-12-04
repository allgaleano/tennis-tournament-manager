import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { Match } from "@/types";
import { useEffect, useState } from "react";

export const useTournamentMatches = (tournamentId: number) => {
  const [matches, setMatches] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchMatches = async () => {
    try {
      const sessionId = getClientSideCookie("Session-Id");
      if (!sessionId) {
        throw new Error("Necesitas iniciar sesión para ver los partidos del torneo");
      }
      
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/matches`, {
        headers: {
          "Session-Id": sessionId
        }
      });

      if (!response.ok) {
        throw new Error("No se pudieron cargar los partidos del torneo");
      }

      const data = await response.json();
      setMatches(data);
    } catch (error) {
      setError(error instanceof Error ? error.message : "Ha ocurrido un error");
    }
    setLoading(false);
  };

  useEffect(() => {
    if (tournamentId) {
      fetchMatches();
    }
  }, [tournamentId]);

  return { matches, loading, error, refetch: fetchMatches };
};