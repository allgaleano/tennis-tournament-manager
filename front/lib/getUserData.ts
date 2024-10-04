import { cookies } from "next/headers";

export const getUserData = async () => {
    const sessionId = cookies().get('Session-Id');
    let user =  null;

    if (!sessionId) {
        return null;
    }
    try {
        const res = await fetch(`${process.env.API_URI}/session`, {
            method: 'GET',
            headers: {
                'Content-Type' : 'application/json',
                'Session-Id' : sessionId.value
            },
        }); 

        if (!res.ok) {
            throw new Error(`Failed to fetch: ${res.status} ${res.statusText}`);
        }
        user = await res.json(); 
    } catch (error) {
        console.error("Fetch failed: ", error);
    }
    return user;
}
