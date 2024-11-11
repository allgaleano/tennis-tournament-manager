import { Tournament } from "@/types"
import { getTournamentStatus } from "@/components/pages/tournaments/getTournamentStatus"
import { formateDateToSpanish } from "@/lib/common/formatDateToSpanish"

const TournamentInfo = ({ 
  tournament, 
} : { 
  tournament: Tournament
}) => {
  return (
    <div className="w-full space-y-4 flex flex-col">
      <div className="flex justify-between border-b">
        <p>Estado: </p>
        <div> {getTournamentStatus(tournament.status)} </div>
      </div>
      <div className="flex justify-between border-b">
        <p>Fecha límite de inscripción: </p>
        <p>{formateDateToSpanish(tournament.registrationDeadline)}</p>
      </div>
      <div className="flex justify-between border-b">
        <p>Número máximo de jugadores: </p>
        <p>{tournament.maxPlayers}</p>
      </div>
    </div>
  )
}

export default TournamentInfo;