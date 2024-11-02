import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { MdSunny } from "react-icons/md";
import { RiFlowerFill } from "react-icons/ri";
import { FaRegSnowflake } from "react-icons/fa";
import { GiThreeLeaves } from "react-icons/gi";
import { formateDateToSpanish } from "@/lib/formatDateToSpanish";
import { Button } from "./ui/button";
import { cn } from "@/lib/utils";

interface TournamentInterface {
  tournament: {
    id: number;
    name: string;
    registrationDeadline: string;
    maxPlayers: number;
  }
}

const TournamentCard = ({
  tournament
} : TournamentInterface) => {

  const getIcon = () => {
    const nameLower = tournament.name.toLowerCase();
    if (nameLower.includes("verano")) return <MdSunny />;
    if (nameLower.includes("invierno")) return <FaRegSnowflake  />;
    if (nameLower.includes("primavera")) return <RiFlowerFill />;
    if (nameLower.includes("otoño")) return <GiThreeLeaves />;
    return null; 
  };
  

  return (
    <Card className="shadow-sm">
      <CardHeader>
        <div className="flex gap-4 items-center justify-center">
          <CardTitle className="text-2xl">{tournament.name}</CardTitle>
          <div className="text-2xl">
            {getIcon()}
          </div>
        </div>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        <div className="space-y-2">
          <p><b>Fecha límite de inscripción:</b> {tournament.registrationDeadline}</p>
          <p><b>Máximo de jugadores:</b> {tournament.maxPlayers}</p>
        </div>
        
        <Button variant="outline" className="font-semibold w-1/4 self-end min-w-[150px]">Ver torneo</Button>
      </CardContent>
    </Card>
  )
}

export default TournamentCard;