interface TournamentInterface {
  tournament: {
    id: number;
    name: string;
    deadline: string;
    maxPlayers: number;
  }
}

const TournamentCard = ({
  tournament
} : TournamentInterface) => {

  return (
    <div className="w-full h-[250px] border rounded-md flex justify-center items-center shadow-sm">
      <div className="w-[90%] h-[90%] flex flex-col justify-between items-start p-4">
        <div className="flex flex-col gap-2">
          <p className="font-bold text-xl">{tournament.name}</p>
          <p>Fecha límite: {tournament.deadline}</p>
          <p>Jugadores máximos: {tournament.maxPlayers}</p>
        </div>
      </div>
    </div>
  )
}

export default TournamentCard;