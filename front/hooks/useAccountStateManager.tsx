"use client";

import { useEffect, useState } from "react";
import { useToast } from "./use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";

export const useAccountStateManager = (initialState: boolean, userId: number) => {
  const [accountState, setAccountState] = useState(initialState);
  const { toast } = useToast();
  const sessionId = getClientSideCookie("Session-Id") as string;

  useEffect(() => {
    setAccountState(initialState);
  }, [initialState]);

  const handleAccountStateChange = async (state: string) => {
    const newAccountState = state === "enabledAccount";

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${userId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Session-Id": sessionId,
        },
        body: JSON.stringify({ accountState: state }),
      });

      if (!response.ok) {
        toast({
          variant: "destructive",
          title: "Error al cambiar estado de la cuenta",
        });
        setAccountState(initialState);
        return;
      }

      setAccountState(newAccountState);
      toast({
        variant: "success",
        title: "Estado de la cuenta cambiado con éxito",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error inesperado al cambiar el estado de la cuenta",
      });
      setAccountState(initialState);
    }
  };

  return { accountState, handleAccountStateChange };
}