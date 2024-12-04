"use client";

import { Button } from "@/components/ui/button"
import { useToast } from "@/hooks/use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { Tournament } from "@/types";
import { useRouter } from "next/navigation";

interface StartTournamentButtonProps {
  tournament: Tournament
  isAdmin: boolean
}

const StartTournamentButton = ({
  tournament,
  isAdmin
}: StartTournamentButtonProps) => {
  
  const { toast } = useToast();
  const router = useRouter();

  const startTournamet = async () => {
    try {
      const sessionId = getClientSideCookie("Session-Id") as string;
      if (!sessionId) {
        toast({
          variant: "destructive",
          title: "Necesitas iniciar sesión para iniciar el torneo",
        });
      }
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournament.id}/start`, {
        method: "POST",
        headers: {
          "Session-Id": sessionId,
        },
      });
      
      if (response.ok) {
        toast({
          variant: "success",
          title: "Torneo iniciado con éxito",
        });
        router.refresh();
      } else {
        
        const data = await response.json();
        toast({
          variant: "destructive",
          title: data.title,
          ...(data.description && { description: data.description }),
        });
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Ha ocurrido un error inesperado",
        description: "Inténtalo de nuevo más tarde",
      })
    }
  }

  if (tournament.status !== 'ENROLLMENT_CLOSED') {
    return null
  }
  
  if (!isAdmin) {
    return null
  }

  return (
    <Button 
      onClick={startTournamet}
      variant="outline"
    >
      Start Tournament 
    </Button>
  )
}

export default StartTournamentButton