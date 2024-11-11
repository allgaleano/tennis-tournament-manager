import { cookies } from "next/headers";

export async function isPlayerEnrolled(tournamentId: number, playerId: number): Promise<{
  enrolled?: boolean;
  error?: string;
}> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/enrolled/${playerId}`, {
      method: "GET",
      headers: {
        "Session-Id": sessionId.value
      }
    });

    if (!response.ok) {
      return { error: "Error al obtener la inscripción" }
    }

    const result = await response.json();
    return { enrolled: result.enrolled }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}