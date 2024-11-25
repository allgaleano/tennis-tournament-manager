import { isPlayerEnrolled } from "@/lib/tournaments/isPlayerEnrolled";
import { Tournament, UserData } from "@/types";
import EnrollButton from "@/components/pages/tournaments/enroll-button/enroll-button";

const EnrollAction = async ({
  tournament,
  userData,
}: { 
  tournament: Tournament 
  userData: UserData
}) => {
  const tournamentIsOpen = tournament.status === "ENROLLMENT_OPEN";
  if (!userData) return null;
  const result = await isPlayerEnrolled(tournament.id, userData.id);
  if ("error" in result || result.enrolled === undefined) {
    return null;
  }
  const { enrolled } = result;
  return (
    <EnrollButton 
      tournamentIsOpen={tournamentIsOpen} 
      enrolled={enrolled} 
      userId={userData.id}
      tournamentId={tournament.id}
    />
  )
}

export default EnrollAction;