export function extractPhoneDetails(phoneString: string | undefined) {
  if (!phoneString) return { error: "Invalid phone number"}

  const phonePattern = /^\+\s*(\d+)\s+(\d+)$/;
  const match = phoneString.match(phonePattern);

  if (match) {
    const prefix = match[1];
    const phoneNumber = match[2];
    return { prefix, phoneNumber };
  } else {
    return { error: "Invalid phone number format" };
  }
}