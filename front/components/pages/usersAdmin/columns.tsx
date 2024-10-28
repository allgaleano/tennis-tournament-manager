"use client";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import { formateDateToSpanish } from "@/lib/formatDateToSpanish";
import { getClientSideCookie } from "@/lib/getClientSideCookie";
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
    cell: ({ row }) => {
      const originalAccountState = row.getValue("enabledAccount") as boolean;
      
      const userId = row.original.id;
      const sessionId = getClientSideCookie("Session-Id") as string;

      const handleAccountStateChange = async (state: string) => {
        const accountState = state === "enabledAccount";
        setAccountState(accountState);

        try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${userId}`, {
            method: "PUT",
            headers: {
              "Content-Type" : "application/json",
              "Session-Id" : sessionId
            },
            body: JSON.stringify({ accountState: state }),
          });
  
          if (!response.ok) {
            toast({
              variant: "destructive",
              title: "Error al cambiar estado de la cuenta",
            });
            setAccountState(originalAccountState);
            return;
          }
  
          toast({
            variant: "success",
            title: "Estado de la cuenta cambiado con éxtio",
          });
        } catch (error) {
          toast({
            variant: "destructive",
            title: "Error inesperado al cambiar el estado de la cuenta",
          });
          setAccountState(originalAccountState);
        }
      }

      const [accountState, setAccountState] = useState(originalAccountState);
      const { toast } = useToast();

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
    cell: ({ row }) => {
      const originalRole = row.getValue("role") as string;
      const userId = row.original.id;
      const sessionId = getClientSideCookie("Session-Id") as string;

      const [selectedRole, setSelectedRole] = useState(originalRole);
      const { toast } = useToast();

      const handleRoleChange = async (newRole: string) => {
        setSelectedRole(newRole);

        try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${userId}`, {
            method: "PUT",
            headers: {
              "Content-Type" : "application/json",
              "Session-Id" : sessionId
            },
            body: JSON.stringify({ role: newRole }),
          })
          
          if (!response.ok) {
            toast({
              variant: "destructive",
              title: "Error inesperado al cambiar de rol"
            });
            setSelectedRole(originalRole);
            return;
          };

          toast({
            variant: "success",
            title: "Rol cambiado con éxito"
          });
        } catch (error) {
          toast({
            variant: "destructive",
            title: "Error inesperado al cambiar de rol"
          });
          setSelectedRole(originalRole);
        }
      }

      return (
        <div className="max-w-[150px]">
          <Select 
            defaultValue={selectedRole} 
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