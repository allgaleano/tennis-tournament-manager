import { getClientSideCookie } from "../users/getClientSideCookie";

export async function unenrollPlayerToTournament (playerId: number, tournamentId: number): Promise<{
  success: boolean;
  error?: string;
  description?: string;
}> {
  try {
    const sessionId = getClientSideCookie("Session-Id");
    if (!sessionId) return { success: false, error: "Sesión no válida" }

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/unenroll/${playerId}`, {
      method: "DELETE",
      headers: {
        "Session-Id": sessionId
      }
    });

    if (!response.ok) {

      if (response.status === 404) {
        return { success: false, error: "Torneo o juador no encontrado" }
      }
      if (response.status === 403) {
        return { success: false, error: "No tienes permisos para anular inscripción" }
      }
      if (response.status === 409) {
        return { success: false, error: "El jugador no está inscrito" }
      }
      if (response.status === 400) {
        return { success: false, error: "La fecha límite de inscripción ha pasado" }
      }

      return { success: false, error: "Error al anular la inscripción" }
    }

    return { success: true }
  } catch (error) {
    console.error(error);
    return { success: false, error: "¡Algo ha salido mal!", description: "Vuelve a intentarlo más tarde" }
  }
}