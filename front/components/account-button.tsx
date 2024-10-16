"use client";

import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { TbLogout2 } from 'react-icons/tb'
import { useToast } from '@/hooks/use-toast';
import { getClientSideCookie } from '@/lib/getClientSideCookie';
import { useRouter } from 'next/navigation';
import { FaUser } from "react-icons/fa";
import { UserData } from '@/types';
import { IoMdSettings } from "react-icons/io";

const AccountButton = ({ userData } : { userData : UserData}) => {
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
          "Content-Type": "application/json",
          "Session-Id": sessionId
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
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" className="flex justify-start gap-4 py-6">
          <div>
            <FaUser/> 
          </div>
          <div className="flex flex-col items-start">
            <p className="font-semibold"> {userData.username} </p>
            <p
              className="max-w-[150px] whitespace-nowrap overflow-hidden text-ellipsis text-xs"
            >Nombre y apellidos</p>
          </div>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-56">
        <DropdownMenuItem className="font-semibold gap-2">
          <IoMdSettings /> Ajustes de perfil
        </DropdownMenuItem>
        <DropdownMenuSeparator></DropdownMenuSeparator>
        <DropdownMenuItem 
          className="font-semibold gap-2"
          onClick={logout}
        >
          <TbLogout2 /> Cerrar Sesión
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export default AccountButton