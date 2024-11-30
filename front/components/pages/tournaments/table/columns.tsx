"use client";
import { Checkbox } from "@/components/ui/checkbox";
import { Enrollment } from "@/types";
import { ColumnDef } from "@tanstack/react-table";

export const columns: ColumnDef<Enrollment>[] = [
  {
    id: "select",
    cell: function Cell ({ row }) {
      return (
        <Checkbox
          checked={row.getIsSelected()}
          onCheckedChange={(value) => row.toggleSelected(!!value)}
          aria-label="Select row"
        />
      )
    }
  },
  {
    header: "Nombre",
    accessorFn: (row) => row.player.name,
    id: "name",
  },
  {
    header: "Apellido",
    accessorFn: (row) => row.player.surname,
    id: "surname",
  },
  {
    header: "Nombre de usuario",
    accessorFn: (row) => row.player.username,
    id: "username",
  },
  {
    header: "Email",
    accessorFn: (row) => row.player.email,
    id: "email",
  },
  {
    header: "Estado",
    accessorKey: "status",
    cell: function Cell ({ row }) {
      const status = row.getValue("status");
      const statusBgColor = status === "PENDING" ? "bg-success" : status === "SELECTED" ? "bg-progress" : "bg-destructive";
      const statusTextColor = status === "PENDING" ? "text-success" : status === "SELECTED" ? "text-progress" : "text-destructive";
      const statusText = status === "PENDING" ? "Pendiente" : status === "SELECTED" ? "Seleccionado" : "Rechazado";

      return (
        <div className={"flex items-center gap-2"}>
          <span className={`rounded-full w-2 h-2 ${statusBgColor}`}></span>
          <p className={`${statusTextColor}`}>{statusText}</p>
        </div>
      );
    }
  }
];