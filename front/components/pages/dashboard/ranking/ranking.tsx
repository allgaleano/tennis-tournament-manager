import { PlayerGlobalStats, PlayerGlobalStatsDisplayList } from "@/types";
import { cookies } from "next/headers";
import { DataTable } from "./data-table";
import { columns } from "./columns";

async function getRanking(page: number, size: number): Promise<{ 
  data: PlayerGlobalStats[], 
  totalPages: number, 
  currentPage: number 
} | { error: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/players/stats?page=${page}&size=${size}`, {
      method: "GET",
      headers: {
        "Session-Id": sessionId?.value
      }
    });

    if (!response.ok) {
      return { error: "No hay datos para mostrar" }
    }

    const result: PlayerGlobalStatsDisplayList = await response.json();
    let data: PlayerGlobalStats[] = [];
    if (!result._embedded) {
      data = [];
    } else {
      data = result._embedded.playerStatsDTOList;
    }

    return {
      data: data,
      totalPages: result.page.totalPages,
      currentPage: result.page.number,
    }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}

export default async function Ranking ({
  searchParams,
}: { searchParams : { page?: string, size?: string} }) {
  const page = parseInt(searchParams?.page ?? '0', 10);
  const size = parseInt(searchParams?.size ?? '20', 10);

  const result = await getRanking(page, size);

  if ('error' in result) {
    return (
      <div className="container mx-auto py-10">
        <p className="text-destructive">{result.error}</p>
      </div>
    )
  }

  const { data, totalPages, currentPage } = result;

  return (
    <DataTable 
      columns={columns} 
      data={data}
      page={currentPage}
      totalPages={totalPages}
    />
  )
}