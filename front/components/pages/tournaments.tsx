import React from 'react'
import SectionHeader from '@/components/section-header'
import { BiSolidTennisBall } from 'react-icons/bi'
import TournamentCard from '@/components/tournament-card'

const Tournaments = () => {
  const tournaments = [
    {
      id: 1,
      name: "Verano 2024",
      deadline: "2024-08-12",
      maxPlayers: 16,
    },
    {
      id: 2,
      name: "Otoño 2024",
      deadline: "2024-11-12",
      maxPlayers: 16,
    },
    {
      id: 3,
      name: "Invierno 2024",
      deadline: "2025-05-12",
      maxPlayers: 16,
    },
    {
      id: 4,
      name: "Primavera 2025",
      deadline: "2025-02-12",
      maxPlayers: 16,
    },
  ]
  return (
    <section className="w-full flex flex-col justify-start items-center m-10">
      <SectionHeader title="Torneos" Icon={BiSolidTennisBall} /> 
      <div className="m-10 grid gap-8 w-full md:w-[80%] lg:grid-cols-2 grid-cols-1">
        {
          tournaments.map(tournament => (
            <TournamentCard key={tournament.id} tournament={tournament} />
          ))
        }
      </div>
    </section>
  )
}

export default Tournaments