"use client";

import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { enrollPlayerToTournament } from "@/lib/tournaments/enrollPlayerToTournament copy";
import { unenrollPlayerToTournament } from "@/lib/tournaments/unenrollPlayerToTournament";
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
    if (!userId || !tournamentId) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "Intentalo de nuevo más tarde"
      })
      return;
    }
    if (type === "enroll") {
      const response = await enrollPlayerToTournament(userId, tournamentId);

      if (response.success) {
        toast({
          variant: "success",
          title: "Inscripción exitosa",
          description: "Te has inscrito correctamente en el torneo."
        });
        router.refresh();
      } else {
        const { error, description } = response;
        toast({
          variant: "destructive",
          title: error || "¡Algo ha ido mal!",
          description: description || "Intentalo de nuevo más tarde"
        });
      }
    } else if (type === "unenroll") {
      const response = await unenrollPlayerToTournament(userId, tournamentId);
      if (response.success) {
        toast({
          variant: "success",
          title: "Inscripción anulada",
          description: "Tu inscripción ha sido anulada correctamente."
        });
        router.refresh();
      }
      else {
        const { error, description } = response;
        toast({
          variant: "destructive",
          title: error || "¡Algo ha ido mal!",
          description: description || "Intentalo de nuevo más tarde"
        });
      }
    }
  }
  return (
    <div>
      {tournamentIsOpen && (
        enrolled ? (
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
        )
      )}
    </div>
  )
}

export default EnrollButton;