import TournamentDetails from "@/components/pages/tournaments/tournament-details";

export const dynamic = "force-dynamic";

const TournamentPage = ({
  params,
  searchParams
}: { 
  params: { id: string },
  searchParams: { [key: string]: string | string[] | undefined }
}) => {
  
  return (
    <TournamentDetails id={params.id} searchParams={searchParams}/>
  )
}

export default TournamentPage;