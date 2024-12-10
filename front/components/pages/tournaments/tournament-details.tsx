import SectionHeader from "@/components/common/section-header";
import { Tournament } from "@/types";
import { cookies } from "next/headers";
import { getTournamentIcon } from "@/components/pages/tournaments/getTournamentIcon";
import TournamentInfo from "@/components/pages/tournaments/tournament-info";
import Enrollments from "./enrollments/enrollments";
import EnrollAction from "@/components/pages/tournaments/enroll-button/enroll-action";
import { getServerSideUserData } from "@/lib/users/getServerSideUserData";
import TournamentMatches from "./matches/tournament-matches";
import StartTournamentButton from "./start-tournament-button";
import ChangeTournamentStatusButton from "./change-tournament-status-button";

async function getTournament(id: string): Promise<{ tournament?: Tournament; error?: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${id}`, {
      method: "GET",
      headers: {
        "Session-Id" : sessionId.value
      },
    });

    if (!response.ok) {
      return { error: "Error al obtener el torneo" }
    }

    const data = await response.json();
    const tournament: Tournament = {
      id: data.id,
      name: data.name,
      registrationDeadline: data.registrationDeadline,
      maxPlayers: data.maxPlayers,
      status: data.status,
      selectedPlayersCount: data.selectedPlayersCount
    };

    return { tournament }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}

const TournamentDetails = async ({ 
  id,
  searchParams 
} : { 
  id: string,
  searchParams: { page?: string; size?: string } 
}) => {
  const userData = await getServerSideUserData();
  const result = await getTournament(id);

  if ("error" in result) {
    return (
      <div className="container mx-auto py-10">
        <p className="text-destructive">{result.error}</p>
      </div>
    );
  }

  const { tournament } = result;
  if (!tournament || !tournament.name) {
    return (
      <div className="container mx-auto py-10">
        <p className="text-destructive">No se ha encontrado el torneo</p>
      </div>
    );
  }
  
  return (
    <section className="w-full m-10 mx-auto flex flex-col justify-start items-center space-y-8">
      <SectionHeader title={tournament.name} Icon={getTournamentIcon(tournament.name)} />
      <div className="flex flex-col w-full max-w-[1400px] gap-4 px-4">
        <TournamentInfo tournament={tournament} />
        {tournament.status === 'IN_PROGRESS' || tournament.status === 'FINISHED' ? (
          <TournamentMatches tournamentId={tournament.id} isAdmin={userData.role === "ADMIN"} />
        ) : null}
        <div className="flex flex-wrap items-center justify-end gap-4 sm:gap-0">
          <StartTournamentButton tournament={tournament} isAdmin={userData.role === "ADMIN"}/>
          <ChangeTournamentStatusButton tournament={tournament} isAdmin={userData.role === "ADMIN"} />
          <EnrollAction tournament={tournament} userData={userData}/>
        </div>
        <Enrollments tournament={tournament} searchParams={searchParams} userData={userData}/>
      </div>
    </section>
  )
}

export default TournamentDetails;