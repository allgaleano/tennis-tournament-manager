"use client";

import { useEffect, useState } from "react";
import { useToast } from "./use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";

export const useUserRoleChange = (initialRole: string, userId: number) => {
  const [role, setRole] = useState(initialRole);
  const { toast } = useToast();
  const sessionId = getClientSideCookie("Session-Id") as string;

  useEffect(() => {
    setRole(initialRole);
  }, [initialRole]);

  const handleRoleChange = async (newRole: string) => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${userId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Session-Id": sessionId,
        },
        body: JSON.stringify({ role: newRole }),
      });

      const data = await response.json();

      toast({
        variant: response.ok ? "success" : "destructive",
        title: data.title,
        ...(data.description && { description: data.description }),
      });

      if (!response.ok) {
        setRole(initialRole);
      } else {
        setRole(newRole);
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error inesperado al cambiar de rol",
      });
      setRole(initialRole);
    }
  } 

  return { role, handleRoleChange };
}
