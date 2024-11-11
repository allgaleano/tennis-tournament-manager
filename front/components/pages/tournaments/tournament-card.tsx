import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { formateDateToSpanish } from "@/lib/common/formatDateToSpanish";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import { getTournamentIcon } from "@/components/pages/tournaments/getTournamentIcon";
import { getTournamentStatus } from "@/components/pages/tournaments/getTournamentStatus";

interface TournamentInterface {
  tournament: {
    id: number;
    name: string;
    registrationDeadline: string;
    maxPlayers: number;
    status: string;
  }
}

const TournamentCard = ({
  tournament
}: TournamentInterface) => {

  const Icon = getTournamentIcon(tournament.name);

  return (
    <Card className="shadow-sm">
      <CardHeader>
        <div className="flex gap-4 items-center justify-center">
          <div className="text-2xl">
            <Icon />
          </div>
          <CardTitle className="text-2xl">{tournament.name}</CardTitle>
        </div>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        <div className="space-y-2">
          <p><b>Fecha límite de inscripción:</b> {formateDateToSpanish(tournament.registrationDeadline)}</p>
          <p><b>Máximo de jugadores:</b> {tournament.maxPlayers}</p>
          {getTournamentStatus(tournament.status)}
        </div>
        <div className="self-end">
          <Button variant="outline" className="font-semibold w-1/4 min-w-[150px]" asChild>
            <Link href={`/tournaments/${tournament.id}`} >Ver torneo</Link>
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}

export default TournamentCard;