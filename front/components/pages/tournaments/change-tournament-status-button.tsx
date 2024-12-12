"use client";

import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { Tournament } from "@/types";
import { useRouter } from "next/navigation";

interface ChangeTournamentStatusButtonProps {
  tournament: Tournament;
  isAdmin: boolean;
}

const ChangeTournamentStatusButton = ({
  tournament,
  isAdmin,
}: ChangeTournamentStatusButtonProps) => {
  const { toast } = useToast();
  const router = useRouter();
  const isEnrollmentOpen = tournament.status === "ENROLLMENT_OPEN";
  const isEnrollmentClosed = tournament.status === "ENROLLMENT_CLOSED";
  
  const handleTournamentChange = async (status: string) => {
    try {
      const sessionId = getClientSideCookie("Session-Id") as string;
      if (!sessionId) {
        toast({
          variant: "destructive",
          title: "Necesitas iniciar sesión para realizar esta acción",
        });
        return;
      }
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournament.id}/${status}Enrollments`, {
        method: "PUT",
        headers: {
          "Session-Id": sessionId,
        },
      });

      const data = await response.json();

      toast({
        variant: response.ok ? "success" : "destructive",
        title: data.title,
        ...(data.description && { description: data.description })
      });

      if (response.ok) {
        router.refresh();
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Ha ocurrido un error inesperado",
        description: "Inténtalo de nuevo más tarde"
      });
    }
  }  

  if (!isAdmin) {
    return null;
  }

  if (!isEnrollmentOpen && !isEnrollmentClosed) {
    return null;
  }

  return (
    <div className="ml-4">
      <Button 
        onClick={() => handleTournamentChange(isEnrollmentClosed ? "open" : "close")}
        variant={isEnrollmentOpen ? "destructive" : "outline"}
      >
        {isEnrollmentOpen ? "Cerrar" : "Abrir"} inscripciones
      </Button>
    </div>
  )
}

export default ChangeTournamentStatusButton