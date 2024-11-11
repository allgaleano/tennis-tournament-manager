import { cookies } from "next/headers";

export const getServerSideUserData = async () => {
  const sessionId = cookies().get("Session-Id");
  if (!sessionId) return;
  try {
    const data = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/userData`, {
      method: "GET",
      headers: {
        "Content-Type" : "application/json",
        "Session-Id" : sessionId?.value
      },
    });
    if (!data) {
      return;
    }
    const userData = await data.json();
    return userData;
  } catch (error) {
    console.error(error);
    return null;
  }
} 