import { Tournament } from "@/types";
import { cookies } from "next/headers";

export async function getTournament(id: string): Promise<{ tournament?: Tournament; error?: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${id}`, {
      method: "GET",
      headers: {
        "Session-Id" : sessionId.value
      },
    });

    if (!response.ok) {
      return { error: "Error al obtener el torneo" }
    }

    const data = await response.json();
    const tournament: Tournament = {
      id: data.id,
      name: data.name,
      registrationDeadline: data.registrationDeadline,
      maxPlayers: data.maxPlayers,
      status: data.status,
      selectedPlayersCount: data.selectedPlayersCount
    };

    return { tournament }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}