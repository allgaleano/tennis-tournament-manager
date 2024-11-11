import { getServerSideUserData } from "@/lib/users/getServerSideUserData";
import { isPlayerEnrolled } from "@/lib/tournaments/isPlayerEnrolled";
import { Tournament } from "@/types";
import EnrollButton from "@/components/pages/tournaments/enroll-button/enroll-button";

const EnrollAction = async ({
  tournament
}: { 
  tournament: Tournament 
}) => {
  const tournamentIsOpen = tournament.status === "ENROLLMENT_OPEN";
  const userData = await getServerSideUserData();
  if (!userData) return null;
  const result = await isPlayerEnrolled(tournament.id, userData.id);
  if ("error" in result || result.enrolled === undefined) {
    return null;
  }
  const { enrolled } = result;
  return (
    <EnrollButton tournamentIsOpen={tournamentIsOpen} enrolled={enrolled} userId={userData.id} tournamentId={tournament.id}/>
  )
}

export default EnrollAction;