import { User, UserDisplayList } from "@/types"
import { DataTable } from "@/components/pages/usersAdmin/data-table";
import { columns } from "@/components/pages/usersAdmin/columns";
import { cookies } from "next/headers";

async function getData(page: number, size: number): Promise<{ 
  data: User[]; 
  totalPages: number
  totalElements: number;
  currentPage: number;
  nextLink?: string;
  prevLink?: string;
  firstLink?: string;
  lastLink?: string;
} | { error: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/admin/users?page=${page}&size=${size}`, {
      method: "GET",
      headers: {
        "Session-Id" : sessionId?.value
      }
    });
    if (!response.ok) {
      return { error: "No estás autorizado para ver este contenido"}
    }
    const result: UserDisplayList = await response.json();
    return {
      data: result._embedded.userDisplayDTOList,
      totalPages: result.page.totalPages,
      totalElements: result.page.totalElements,
      currentPage: result.page.number,
    }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}


export default async function AdminTable({
  searchParams,
}: {
  searchParams: { page?: string; size?: string };
}) {
  const page = parseInt(searchParams?.page ?? '0', 10);
  const size = parseInt(searchParams?.size ?? '20', 10);

  const result = await getData(page, size);

  if ('error' in result) {
    return (
      <div className="container mx-auto py-10">
        <p className="text-destructive">{result.error}</p>
      </div>
    )
  }

  const { data, totalPages, currentPage } = result;

  return (
    <div className="flex justify-center w-full items-start overflow-hidden mx-4">
      <DataTable 
        columns={columns} 
        data={data}
        page={currentPage}
        totalPages={totalPages}
      />
    </div>
  )
}
