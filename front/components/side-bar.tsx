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
import AccountButton from '@/components/account-button';

const SideBar = ({ userData }: { userData: UserData }) => {
  const pathname = usePathname();

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
        <AccountButton userData={userData} />
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
            <AccountButton userData={userData} />
          </SheetContent>
        </Sheet>
      </div>
    </div>
  )
}

export default SideBar