import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { useRouter } from "next/navigation";

interface PlayerSelectionControlsProps {
  rowSelection: Record<string, boolean>;
  tournamentId: number;
  onActionSuccess: () => void;
}
const PlayerSelectionControls = ({
  rowSelection,
  tournamentId,
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
    
    const apiUri = `${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/${
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

      if (response.ok) {
        toast({
          variant: "success",
          title: action === "select" ? "Jugadores seleccionados con éxito" : "Jugadores deseleccionados con éxito",
          description: `${action === "select" ? "Seleccionados" : "Deseleccionados"}: ${
            data.selectedCount || data.deselectedCount
          } jugadores`,
        });

        onActionSuccess();
        router.refresh();

      } else if (response.status === 403) {
        toast({
          variant: "destructive",
          title: "Acción no permitida",
          description: "No tienes permisos para realizar esta acción."
        });
      } else if (response.status === 400) {
        toast({
          variant: "destructive",
          title: `${action === "select" ? "Selección" : "Deselección"} fallida`,
          description: `${action === "select" ? "Algún jugador estaba previamente seleccionado" : "Algún jugador no estaba seleccionado"}`,
        })
      } else if (response.status === 409) {
        toast({
          variant: "destructive",
          title: "Error al seleccionar jugadores",
          description: "Excede el número máximo de jugadores seleccionados."
        })
      } else {
        throw new Error("Error desconocido");
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "No se ha podido completar la acción. Inténtalo más tarde.",
      });
    }
  }

  return (
    <div className="flex space-x-2">
      <Button
        variant="default"
        size="sm"
        onClick={() => handleAction("select")}
        disabled={!hasSelectedRows}
      >
        Seleccionar Jugadores
      </Button>
      <Button
        variant="outline"
        size="sm"
        onClick={() => handleAction("deselect")}
        disabled={!hasSelectedRows}
      >
        Deseleccionar Jugadores
      </Button>
    </div>
  )
}

export default PlayerSelectionControls;