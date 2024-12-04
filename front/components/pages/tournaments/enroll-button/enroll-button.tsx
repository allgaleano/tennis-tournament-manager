"use client";

import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { useRouter } from "next/navigation";

interface EnrollButtonProps {
  tournamentIsOpen: boolean;
  enrolled: boolean;
  userId: number;
  tournamentId: number;
}

const EnrollButton = ({
  tournamentIsOpen,
  enrolled,
  userId,
  tournamentId,
}: EnrollButtonProps) => {
  const { toast } = useToast();
  const router = useRouter();

  const handleEnrollment = async (type: "enroll" | "unenroll", userId: number, tournamentId: number) => {
    const sessionId = getClientSideCookie("Session-Id");
    if (!userId || !tournamentId || !sessionId) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "Intentalo de nuevo más tarde"
      })
      return;
    }

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/${type}/${userId}`, {
      method: type === "enroll" ? "POST" : "DELETE",
      headers: {
        "Session-Id": sessionId
      }
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
  }

  if (!tournamentIsOpen) {
    return null;
  }
  
  return (
    <div className="ml-4">
      {enrolled ? (
        <Button
          variant="destructive"
          onClick={() => handleEnrollment("unenroll", userId, tournamentId)}
        >
          Anular inscripción
        </Button>
      ) : (
        <Button
          variant="outline"
          onClick={() => handleEnrollment("enroll", userId, tournamentId)}
        >
          Inscribirse
        </Button>
      )}
    </div>
  )
}

export default EnrollButton;