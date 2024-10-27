"use client";

import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
} from "@tanstack/react-table"

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { FaUserShield } from "react-icons/fa6";
import { Button } from "@/components/ui/button";
import { useRouter, useSearchParams } from "next/navigation";

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  page: number;
  totalPages: number;
}

export function DataTable<TData, TValue>({
  columns,
  data,
  page,
  totalPages,
}: DataTableProps<TData, TValue>) {
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
  });

  const router = useRouter();
  const searchParams = useSearchParams();

  const handleNavigation = (newPage: number) => {
    const size = searchParams.get("size") || "10";
    router.push(`/users?page=${newPage}&size=${size}`);
  };

  return (
    <div className="w-full flex flex-col justify-start items-center m-10 gap-8">
      <h1 className="font-semibold text-2xl flex items-center gap-2">
        <FaUserShield /> Usuarios
      </h1>
      <div>
        <div className="rounded border">
          <Table>
            <TableHeader>
              {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                  {headerGroup.headers.map((header) => {
                    return (
                      <TableHead key={header.id}>
                        {header.isPlaceholder
                          ? null
                          : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                      </TableHead>
                    )
                  })}
                </TableRow>
              ))}
            </TableHeader>
            <TableBody>
              {table.getRowModel().rows?.length ? (
                table.getRowModel().rows.map((row) => (
                  <TableRow
                    key={row.id}
                    data-state={row.getIsSelected() && "selected"}
                  >
                    {row.getVisibleCells().map((cell) => (
                      <TableCell key={cell.id}>
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={columns.length} className="h-24 text-center">
                    No existen usuarios en la app
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
        <div className="flex items-center justify-start space-x-2 py-4">
          <Button
            variant="outline"
            size="sm"
            onClick={() => handleNavigation(page - 1)}
            disabled={page <= 0}
          >
            Anterior
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => handleNavigation(page + 1)}
            disabled={page >= totalPages - 1}
          >
            Siguiente
          </Button>
        </div>
        <div>
          Página {page + 1} de {totalPages}
        </div>
      </div>
    </div>
  )
}