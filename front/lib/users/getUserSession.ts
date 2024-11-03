import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export const getUserSession = async () => {
  const sessionId = cookies().get('Session-Id');

  if (!sessionId) {
    return { valid: false };
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
      return { valid: false };
    }
    const data = await res.json();

    const response = NextResponse.next();

    response.cookies.set({
      name: "Session-Id",
      value: data.sessionId,
      expires: new Date(data.expirationDate),
      sameSite: "none",
      secure: true,
      path: "/"
    })

    return { valid: true, response }
  } catch (error) {
    console.error("Fetch failed: ", error);
    return { valid: false };
  }
}
