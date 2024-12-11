"use client";

import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { PlayerTournamentStats } from "@/types";
import { ColumnDef, flexRender, getCoreRowModel, useReactTable } from "@tanstack/react-table";

type CellValue = string | number | boolean | React.ReactNode | null;

interface DataTableProps {
  columns: ColumnDef<PlayerTournamentStats, CellValue>[];
  data: PlayerTournamentStats[];
}

export function DataTable({
  columns,
  data,
}: DataTableProps) {

  const table = useReactTable({
    getRowId: (row) => row.player.id.toString(),
    data: data ?? [],
    columns,
    getCoreRowModel: getCoreRowModel(),
  });

  return (
    <div className="w-full flex flex-col overflow-x-auto">
      <div className="flex py-1 h-12">
        <h2>Resultados del torneo:</h2>
      </div>
      <div className="rounded border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead
                      key={header.id}
                      className="whitespace-nowrap"
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
                    <TableCell key={cell.id} className="whitespace-nowrap">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No hay resultados
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}