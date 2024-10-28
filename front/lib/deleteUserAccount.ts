export const deleteUserAccount = async (id: number, sessionId: string) => {
  try {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${id}`, {
      method: "DELETE",
      headers: {
        "Content-Type" : "application/json",
        "Session-Id" : sessionId
      }
    });

    if (res.ok) {
      return true;
    }
  } catch (error) {
    console.log(error);
  }
  return false;
} 