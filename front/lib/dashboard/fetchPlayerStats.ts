import { getClientSideCookie } from "../users/getClientSideCookie";
import { getClientSideUserData } from "../users/getClientSideUserData";
export const fetchPlayerStats = async () => {
  const userData = await getClientSideUserData();
  if (!userData) return null;
  const sessionId = getClientSideCookie("Session-Id");
  if (!sessionId) return null;

  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/players/${userData.id}/stats`, {
      headers: {
        "Session-Id": sessionId
      }
    });

    if (!response.ok) {
      return null;
    }

    return await response.json();
  } catch (error) {
    return null;
  }
}