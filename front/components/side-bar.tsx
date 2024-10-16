"use client";

import React from 'react'
import { Button } from '@/components/ui/button'
import { useToast } from '@/hooks/use-toast';
import { getClientSideCookie } from '@/lib/getClientSideCookie';
import { usePathname, useRouter } from 'next/navigation';
import { BiSolidTennisBall } from "react-icons/bi";
import { FaUserShield } from "react-icons/fa6";
import { TbLogout2 } from "react-icons/tb";
import { Sheet, SheetContent, SheetHeader, SheetTrigger } from '@/components/ui/sheet';
import { FiMenu } from "react-icons/fi";
import { UserData } from '@/types';
import { MdInsights } from "react-icons/md";
import Image from 'next/image';

const SideBar = ({ userData }: { userData: UserData }) => {
  const { toast } = useToast();
  const sessionId = getClientSideCookie("Session-Id");
  const router = useRouter();
  const pathname = usePathname();

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
    <div>
      <div className="hidden md:flex flex-col h-screen p-4 border-r min-w-[250px] gap-2">
        <div className="w-full flex items-center justify-center p-4">
          <Image src="/static/Logotipo_MatchPoint.svg" width={200} height={25} alt="Match Point"/>
        </div>
        <Button
          variant={pathname === "/dashboard" ? "outline" : "ghost"}
          className="font-semibold justify-start gap-2"
        >
          <MdInsights /> Dashboard
        </Button>
        <Button
          variant={pathname === "/tournaments" ? "outline" : "ghost"}
          className="font-semibold justify-start gap-2"
        >
          <BiSolidTennisBall /> Torneos
        </Button>
        {userData.roles.includes("ADMIN") &&
          <Button
            variant={pathname === "/users" ? "outline" : "ghost"}
            className="font-semibold justify-start gap-2"
          >
            <FaUserShield /> Usuarios
          </Button>
        }
        <div className="flex-grow"></div>
        <Button
          variant="outline"
          className="font-semibold gap-2"
          onClick={logout}>
          <TbLogout2 /> Cerrar Sesión
        </Button>
      </div>
      <div className="p-4 absolute">
        <Sheet>
          <SheetTrigger asChild>
            <Button
              variant="outline"
              size="icon"
              className="md:hidden"
            >
              <FiMenu className="h-4 w-4" />
            </Button>
          </SheetTrigger>
          <SheetContent side="left" className="flex flex-col bg-white pt-16">
            <SheetHeader>
              <div className="w-full flex items-center justify-center pb-4">
                <Image src="/static/Logotipo_MatchPoint.svg" width={200} height={25} alt="Match Point" />
              </div>
            </SheetHeader>
            <Button
              variant={pathname === "/dashboard" ? "outline" : "ghost"}
              className="font-semibold justify-start gap-2 py-6"
            >
              <MdInsights /> Dashboard
            </Button>
            <Button
              variant={pathname === "/tournaments" ? "outline" : "ghost"}
              className="font-semibold justify-start gap-2 py-6"
            >
              <BiSolidTennisBall /> Torneos
            </Button>
            {userData.roles.includes("ADMIN") &&
              <Button
                variant={pathname === "/users" ? "outline" : "ghost"}
                className="font-semibold justify-start gap-2 py-6"
              >
                <FaUserShield /> Usuarios
              </Button>
            }
            <div className="flex-grow"></div>
            <Button
              variant="outline"
              className="font-semibold gap-2 py-6"
              onClick={logout}>
              <TbLogout2 /> Cerrar Sesión
            </Button>
          </SheetContent>
        </Sheet>
      </div>
    </div>
  )
}

export default SideBar