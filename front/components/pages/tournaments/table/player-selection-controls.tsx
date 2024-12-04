import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { Tournament } from "@/types";
import { useRouter } from "next/navigation";

interface PlayerSelectionControlsProps {
  rowSelection: Record<string, boolean>;
  tournament: Tournament;
  onActionSuccess: () => void;
}
const PlayerSelectionControls = ({
  rowSelection,
  tournament,
  onActionSuccess,
}: PlayerSelectionControlsProps) => {
  const { toast } = useToast();
  const router = useRouter();

  const hasSelectedRows = Object.values(rowSelection).some((isSelected) => isSelected);

  const handleAction = async (action: "select" | "deselect") => {
    const selectedPlayers = Object.keys(rowSelection)
      .filter((key) => rowSelection[key])

    console.log(selectedPlayers);

    if (selectedPlayers.length === 0) {
      toast({
        variant: "destructive",
        title: "No se han seleccionado jugadores",
        description: "Selecciona al menos un jugador para realizar esta acción."
      });

      return;
    }
    
    const apiUri = `${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournament.id}/${
      action === "select" ? "selectPlayers" : "deselectPlayers"
    }`;

    const sessionId = getClientSideCookie("Session-Id");

    if (!sessionId) {
      toast({
        variant: "destructive",
        title: "Sesión no válida",
        description: "Inicia sesión para realizar esta acción."
      });
      return;
    }

    try {
      const response = await fetch(apiUri, {
        method: "POST",
        headers: {
          "Session-Id": sessionId,
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ playerIds: selectedPlayers })
      });
      const data = await response.json();

      toast({
        variant: response.ok ? "success" : "destructive",
        title: data.title,
        ...(data.description && { description: data.description })
      })

      if (response.ok) {
        onActionSuccess();
        router.refresh();
      }

    } catch (error) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "No se ha podido completar la acción. Inténtalo más tarde.",
      });
    }
  }

  if (tournament.status !== "ENROLLMENT_CLOSED") {
    return null;
  }

  return (
    <div className="flex space-x-2">
      <Button
        variant="default"
        size="sm"
        onClick={() => handleAction("select")}
        disabled={!hasSelectedRows}
        className={`${!hasSelectedRows && "hidden"}`}
      >
        Seleccionar
      </Button>
      <Button
        variant="outline"
        size="sm"
        onClick={() => handleAction("deselect")}
        disabled={!hasSelectedRows}
        className={`${!hasSelectedRows && "hidden"}`}
      >
        Deseleccionar
      </Button>
    </div>
  )
}

export default PlayerSelectionControls;