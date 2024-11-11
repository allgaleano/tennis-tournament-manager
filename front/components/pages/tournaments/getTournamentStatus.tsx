import { cn } from "@/lib/utils";

interface StatusStyleOptions {
  type: "success" | "destructive" | "progress";
}

export const getTournamentStatus = (status: string) => {
  if (status.includes("FINISHED")) return statusTemplate("Finalizado", { type: "destructive" });
  if (status.includes("IN_PROGRESS")) return statusTemplate("En progreso", { type: "progress" });
  if (status.includes("ENROLLMENT_CLOSED")) return statusTemplate("Inscripciones cerradas", { type: "destructive" });
  if (status.includes("ENROLLMENT_OPEN")) return statusTemplate("Inscripciones abiertas", { type: "success" });
}

const statusTemplate = (status: string, type: StatusStyleOptions) => {
  const bgColorClass = type.type === "success" 
  ? "bg-success" 
  : type.type === "destructive"
  ? "bg-destructive"
  : "bg-progress";

const textColorClass = type.type === "success" 
  ? "text-success" 
  : type.type === "destructive"
  ? "text-destructive"
  : "text-progress";

return (
  <div className="flex gap-2 items-center">
    <span className={cn(`w-2 h-2 rounded-full ${bgColorClass}`)}></span>
    <p><span className={cn(`${textColorClass}`)}>{status}</span></p>
  </div>
)
}