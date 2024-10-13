"use client";

import { Button } from '@/components/ui/button';
import { useToast } from '@/hooks/use-toast';
import { getClientSideCookie } from '@/lib/getClientSideCookie';
import { UserData } from '@/types';
import { useRouter } from 'next/navigation';

const Dashboard = ({ userData } : { userData: UserData }) => {

  const { toast } = useToast();
  const sessionId = getClientSideCookie("Session-Id");
  const router = useRouter();

  const logout = async () => {
    try {
      if (!sessionId) {
        throw new Error("No sessionId cookie found");
      }
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/auth/logout`, {
        method: "POST",
        headers: {
          "Content-Type" : "application/json",
          "Session-Id" : sessionId
        },
      });
      if (response.ok) {
        document.cookie = "Session-Id=;expires=Thu, 01 Jan 1970 00:00:01 GMT;";
        router.push("/login");
        router.refresh();
      } else {
        toast({
          variant: "destructive",
          title: "¡Algo ha salido mal!"
        })
      }
    } catch (error) {
      console.error(error);
      toast({
        variant: "destructive",
        title: "¡Algo ha salido mal!"
      })
    }
  }

  return (
    <div className="w-full h-screen flex flex-col justify-center items-center">
      <div className="space-y-4">
        <div className="space-y-2">
          <p>Welcome <b>{userData.username}</b></p>
          <p>Email: {userData.email}</p>
          <p>Roles: {userData.roles}</p>
        </div>
        <Button className="font-semibold" onClick={logout}>Logout</Button>
        {userData.roles.includes("ADMIN") &&
          <div>Only admins content</div>
        }
      </div>
    </div>
  )
}

export default Dashboard;