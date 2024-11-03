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
import SectionHeader from "@/components/common/section-header";

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
    <section className="w-full flex flex-col justify-start items-center m-10">
      <SectionHeader title="Usuarios" Icon={FaUserShield} />
      <div className="w-full flex flex-col justify-center items-center m-10 gap-8"> 
        <div className="w-full max-w-[1600px]">
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
                      data-state={row.getIsSelected() && "selected"}
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
                      No existen usuarios en la app
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
              <p className="text-sm text-primary/80">Página {page + 1} de {totalPages}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}