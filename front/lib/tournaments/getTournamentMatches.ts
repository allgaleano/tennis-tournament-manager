import { cookies } from "next/headers";

export const getTournamentMatches = async (tournamentId: string) => {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) {
      throw new Error("Necesitas iniciar sesión para ver los partidos del torneo");
    }
    
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/matches`, {
      headers: {
        "Session-Id": sessionId.value
      },
      next: {
        tags: [`matches-${tournamentId}`]
      }
    });

    if (!response.ok) {
      throw new Error("No se pudieron cargar los partidos del torneo");
    }

    const data = await response.json();
    return { matches: data };
  } catch (error) {
    return { error: "Ha ocurrido un error" };
  }
};