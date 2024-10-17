import { cookies } from "next/headers";

export const getUserSession = async () => {
  const sessionId = cookies().get('Session-Id');
  let user = null;

  if (!sessionId) {
    return null;
  }
  try {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/session`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Session-Id': sessionId.value
      },
      next: {
        revalidate: 0
      }
    });

    if (!res.ok) {
      return null;
    }
    user = await res.json();
  } catch (error) {
    console.error("Fetch failed: ", error);
  }
  return user;
}
