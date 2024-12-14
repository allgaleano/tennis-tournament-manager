"use client";

import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { TbLogout2 } from 'react-icons/tb'
import { useToast } from '@/hooks/use-toast';
import { getClientSideCookie } from '@/lib/users/getClientSideCookie';
import { useRouter } from 'next/navigation';
import { FaUser } from "react-icons/fa";
import { UserData } from '@/types';
import { IoMdSettings } from "react-icons/io";
import { cn } from '@/lib/utils';
import Link from 'next/link';

interface AccountButtonInterface {
  userData: UserData,
  isMobile?: boolean,
  isSelected?: boolean,
}

const AccountButton = ({ userData, isMobile, isSelected } : AccountButtonInterface ) => {
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
        document.cookie = "Session-Id=; Path=/; Secure; SameSite=None; Expires=Thu, 01 Jan 1970 00:00:00 GMT;";
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
        <Button 
          variant={isSelected ? "default" : "outline"} 
          className={cn("flex justify-between gap-4", isMobile ? "py-8" : "py-6")}
        >
          <div>
            <FaUser/> 
          </div>
          <div className="flex flex-col justify-center items-end">
            <p className="font-semibold"> {userData.username} </p>
            <p className="truncate max-w-[150px] whitespace-nowrap overflow-hidden text-ellipsis">
              {userData.name} {userData.surname}
            </p>
          </div>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className={cn(isMobile ? "w-80" : "w-52")}>
        <DropdownMenuItem 
          className="font-semibold gap-2"
          asChild
        >
          <Button asChild variant="ghost">
            <Link href="/settings"><IoMdSettings /> Ajustes de perfil </Link>
          </Button>
        </DropdownMenuItem>
        <DropdownMenuSeparator></DropdownMenuSeparator>
        <DropdownMenuItem 
          className="font-semibold gap-2"
          asChild
        >
          <Button asChild onClick={logout} variant="ghost">
            <div>
              <TbLogout2 /> Cerrar Sesión
            </div>
          </Button>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export default AccountButton;