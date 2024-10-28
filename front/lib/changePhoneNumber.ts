interface ChangePhoneNumberProps {
  userId: number;
  sessionId: string;
  newPrefix: string;
  newPhoneNumber: string;
}

export const changePhoneNumber = async ({ userId, sessionId, newPrefix, newPhoneNumber } : ChangePhoneNumberProps) => {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${userId}`, {
      method: "PUT",
      headers: {
        "Content-Type" : "application/json",
        "Session-Id" : sessionId
      }, 
      body: JSON.stringify({ phonePrefix: newPrefix, phoneNumber: newPhoneNumber })  
    });
    if (response.ok) {
      return { success: true }
    } else {
      return { error: "unknown-error" }
    }
  } catch (error) {
    console.error(error);
    return { error: "unknown-error"}
  }
}
