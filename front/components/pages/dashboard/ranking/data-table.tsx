"use client";

import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { cn } from "@/lib/utils";
import { PlayerGlobalStats } from "@/types";
import { ColumnDef, flexRender, getCoreRowModel, useReactTable } from "@tanstack/react-table";
import { useRouter, useSearchParams } from "next/navigation";

type CellValue = string | number | boolean | React.ReactNode | null;

interface DataTableProps {
  columns: ColumnDef<PlayerGlobalStats, CellValue>[];
  data: PlayerGlobalStats[];
  page: number;
  totalPages: number;
}

export function DataTable({
  columns,
  data,
  page,
  totalPages,
}: DataTableProps) {

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
  })

  const searchParams = useSearchParams();
  const router = useRouter();

  const handleNavidation = (newPage: number) => {
    const size = searchParams.get("size") || "20";
    router.push(`/dashboard?page=${newPage}&size=${size}`);
  }

  return (
    <div className="w-full flex flex-col overflow-x-auto">
      <div className="flex flex-wrap items-center py-1 gap-2 justify-between h-12">
        <h2 className="text-lg font-semibold">Ranking Global:</h2>
      </div>
      <div className="rounded border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id} >
                {headerGroup.headers.map((header, index) => {
                  return (
                    <TableHead
                      key={header.id}
                      className={cn(
                        "white-space-nowrap",
                        index === 0 ? "" : "text-right"
                      )}
                    >
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
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id} className="text-right">
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No hay datos para mostrar
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-between space-x-2 py-4 px-2">
        <div className="space-x-4">
            <Button
              variant="outline"
              size="sm"
              onClick={() => handleNavidation(page - 1)}
              disabled={page <= 0}
            >
              Anterior
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => handleNavidation(page + 1)}
              disabled={page >= totalPages - 1}
            >
              Siguiente
            </Button>
        </div>
        <div>
          <p className="text-sm text-primary/80">Página {page + 1} de {totalPages === 0 ? 1 : totalPages}</p>
        </div>
      </div>
    </div>
  )
}