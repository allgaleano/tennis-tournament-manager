import { PlayerTournamentStats } from "@/types";
import { cookies } from "next/headers";
import { DataTable } from "./data-table";
import { columns } from "./columns";

async function getTournamentStatus(tournamentId: number): Promise<{
  data: PlayerTournamentStats[];
} | { error: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/stats`, {
      method: "GET",
      headers: {
        "Session-Id": sessionId?.value
      }
    });

    const data = await response.json();
    if (!response.ok) {
      return { error: data.title }
    }
    return { data };
  } catch (error) {
    return { error: "¡Algo ha salido mal!" }
  }
}

export default async function TournamentStats({
  tournamentId,
}: { tournamentId: number }) {
  const result = await getTournamentStatus(tournamentId);
  if ("error" in result) {
    return (
      <div className="container mx-auto py-10">
        <p className="text-destructive">{result.error}</p>
      </div>
    );
  }

  const { data } = result;

  return (
    <DataTable 
      data={data}
      columns={columns}
    />
  )
}