import SectionHeader from '@/components/common/section-header'
import { BiSolidTennisBall } from 'react-icons/bi'
import TournamentCard from '@/components/pages/tournaments/tournament-card'
import { cookies } from 'next/headers'
import { Tournament } from '@/types'

async function getTournaments(): Promise<{ tournaments?: Tournament[]; error?: string }> {
  try {
    const sessionId = cookies().get("Session-Id");
    if (!sessionId) return { error: "Sesión no válida" }

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments`, {
      method: "GET",
      headers: {
        "Session-Id" : sessionId.value
      },
    });

    if (!response.ok) {
      return { error: "Error al obtener los torneos" }
    }
    const data = await response.json();
    const tournaments: Tournament[] = data._embedded.tournamentList.map((tournament: Tournament) => ({
      id: tournament.id,
      name: tournament.name,
      registrationDeadline:tournament.registrationDeadline,
      maxPlayers: tournament.maxPlayers,
      status: tournament.status
    }));

    return { tournaments }
  } catch (error) {
    console.error(error);
    return { error: "¡Algo ha salido mal!" }
  }
}

const Tournaments = async () => {
  const result = await getTournaments();

  if ("error" in result) {
    return (
      <div className="container mx-auto py-10">
        <p className="text-destructive">{result.error}</p>
      </div>
    );
  }

  const { tournaments } = result;

  return (
    <section className="w-[90%] flex flex-col justify-start items-center m-10 max-w-[1400px] mx-auto">
      <SectionHeader title="Torneos" Icon={BiSolidTennisBall} /> 
      <div className="m-10 grid gap-8 w-full lg:grid-cols-2 grid-cols-1">
        {
          tournaments?.map(tournament => (
            <TournamentCard key={tournament.id} tournament={tournament} />
          ))
        }
      </div>
    </section>
  )
}

export default Tournaments;