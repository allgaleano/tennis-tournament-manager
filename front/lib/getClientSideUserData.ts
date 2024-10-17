import { getClientSideCookie } from "@/lib/getClientSideCookie";

export const getClientSideUserData = async () => {
  const sessionId = getClientSideCookie("Session-Id");
  let userData = null;
  if (!sessionId) return;
  try {
    const data = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/userData`, {
      method: "GET",
      headers: {
        "Content-Type" : "application/json",
        "Session-Id" : sessionId
      }
    });
    if (!data) {
      return;
    }
    userData = await data.json();
  } catch (error) {
    console.error(error);
  }
  return userData;
} 