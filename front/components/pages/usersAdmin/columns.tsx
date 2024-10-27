"use client";
import { formateDateToSpanish } from "@/lib/formatDateToSpanish";
import { cn } from "@/lib/utils";
import { User } from "@/types";
import { ColumnDef } from "@tanstack/react-table"

export const columns: ColumnDef<User>[] = [
  {
    accessorKey: "username",
    header: "Nombre de usuario"
  },
  {
    accessorKey: "email",
    header: "Email"
  },
  {
    accessorKey: "name",
    header: "Nombre"
  },
  {
    accessorKey: "createdAt",
    header: "Fecha de creación",
    cell: ({ row }) => {
      return (
        <div>{formateDateToSpanish(row.getValue("createdAt"))}</div>
      )
    }
  },
  {
    accessorKey: "enabledAccount",
    header: "Estado de la cuenta",
    cell: ({ row }) => {
      const active = row.getValue("enabledAccount");
      return (
        <div className="flex items-center gap-2">
          <span className={cn(active ? "bg-success" : "bg-destructive", "w-2 h-2 rounded-full")}></span>
          {active ? (
            <span className="text-success">Activa</span>
          ) : (
            <span className="text-destructive">Deshabilitada</span>
          )}
        </div>
      )
    }
  },
  {
    accessorKey: "role",
    header: "Rol",
    cell: ({ row }) => {
      const role = row.getValue("role");
      return (
        <div>{role === "ADMIN" ? (
          <span>Administrador</span>
        ) : (
          <span>Jugador</span>
        )}</div>
      )
    }
  },
];