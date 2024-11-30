import { Enrollment, EnrollmentDisplayList, UserData } from "@/types";
import { cookies } from "next/headers";
import { DataTable } from "./data-table";
import { columns } from "./columns";

async function getEnrollments(tournamentId: number, page: number, size: number): Promise<{
  data: Enrollment[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
} | { error: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/enrollments?page=${page}&size=${size}`, {
      method: "GET",
      headers: {
        "Session-Id": sessionId?.value
      }
    });

    if (!response.ok) {
      if (response.status === 404) {
        return { error: "Torneo no encontrado" }
      }
      return { error: "No hay jugadores inscritos" }
    }

    const result: EnrollmentDisplayList = await response.json();
    let data: Enrollment[] = [];
    if (!result._embedded) {
      data = [];
    } else {
      data = result._embedded.tournamentEnrollmentDTOList.map((enrollment) => ({
        id: enrollment.id,
        player: enrollment.player,
        status: enrollment.status
      }));
    }
    return {
      data: data,
      totalPages: result.page.totalPages,
      totalElements: result.page.totalElements,
      currentPage: result.page.number,
    }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}

export default async function Enrollments ({ 
  searchParams, 
  tournamentId,
  userData,
}: { 
  searchParams: { page?: string; size?: string },
  tournamentId: number,
  userData: UserData, 
}) {
  const page = parseInt(searchParams?.page ?? '0', 10);
  const size = parseInt(searchParams?.size ?? '20', 10);

  const isAdmin = userData.role === "ADMIN";

  const result = await getEnrollments(tournamentId, page, size);

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
      tournamentId={tournamentId}
      columns={columns} 
      data={data}
      page={currentPage}
      totalPages={totalPages}
      isAdmin={isAdmin}
    />
  )
}
