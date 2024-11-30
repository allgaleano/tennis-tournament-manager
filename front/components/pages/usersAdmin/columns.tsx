"use client";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import { useAccountStateManager } from "@/hooks/useAccountStateManager";
import { useUserRoleChange } from "@/hooks/useUserRoleChange";
import { formateDateToSpanish } from "@/lib/common/formatDateToSpanish";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { cn } from "@/lib/utils";
import { User } from "@/types";
import { ColumnDef } from "@tanstack/react-table";
import { useState } from "react";

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
    accessorKey: "confirmedAccount",
    header: "Confirmada",
    cell: ({ row }) => {
      const confirmed = row.getValue("confirmedAccount");
      return (
        <div className="flex items-center gap-2">
          <span className={cn(confirmed ? "bg-success" : "bg-destructive", "w-2 h-2 rounded-full")}></span>
          {confirmed ? (
            <span className="text-success">Sí</span>
          ) : (
            <span className="text-destructive">No</span>
          )}
        </div>
      )
    }
  },
  {
    accessorKey: "enabledAccount",
    header: "Cuenta",
    cell: function Cell ({ row }) {
      const originalAccountState = row.getValue("enabledAccount") as boolean;
      const userId = row.original.id;
      
      const { accountState, handleAccountStateChange } = useAccountStateManager(originalAccountState, userId);

      return (
        <div className="max-w-[150px]">
          <Select 
            defaultValue={accountState ? "enabledAccount" : "disabledAccount"}
            onValueChange={handleAccountStateChange}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                <SelectItem value="enabledAccount">
                  <div className="flex items-center gap-2">
                    <span className="bg-success w-2 h-2 rounded-full"></span>
                    <span className="text-success">Habilitada</span>
                  </div>
                </SelectItem>
                <SelectItem value="disabledAccount">
                  <div className="flex items-center gap-2">
                    <span className="bg-destructive w-2 h-2 rounded-full"></span>
                    <span className="text-destructive">Deshabilitada</span>
                  </div>
                </SelectItem>
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>
      )
    }
  },
  {
    accessorKey: "role",
    header: "Rol",
    cell: function Cell ({ row }) {
      const originalRole = row.getValue("role") as string;
      const userId = row.original.id;
      
      const { role, handleRoleChange } = useUserRoleChange(originalRole, userId);

      return (
        <div className="max-w-[150px]">
          <Select 
            defaultValue={role} 
            onValueChange={handleRoleChange}
          >
            <SelectTrigger>
              <SelectValue/>
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                <SelectItem value="ADMIN">Administrador</SelectItem>
                <SelectItem value="USER">Jugador</SelectItem>
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>
      )
    }
  },
];