interface ChangeUsernameProps {
  userId: number;
  sessionId: string;
  newUsername: string;
}

export const changeUsername = async ({ userId, sessionId, newUsername } : ChangeUsernameProps) => {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${userId}`, {
      method: "PUT",
      headers: {
        "Content-Type" : "application/json",
        "Session-Id" : sessionId
      }, 
      body: JSON.stringify({ username: newUsername })  
    });
    if (response.ok) {
      return { success: true }
    } else if (response.status === 409) {
      return { error: "username-taken" }
    } else {
      return { error: "unknown-error" }
    }
  } catch (error) {
    console.error(error);
    return { error: "unknown-error"}
  }
}
