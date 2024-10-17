"use client";

import { Button } from '@/components/ui/button'
import { usePathname, useRouter } from 'next/navigation';
import { BiSolidTennisBall } from "react-icons/bi";
import { FaUserShield } from "react-icons/fa6";
import { Sheet, SheetClose, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet';
import { FiMenu } from "react-icons/fi";
import { UserData } from '@/types';
import { MdInsights } from "react-icons/md";
import Image from 'next/image';
import AccountButton from '@/components/account-button';
import { Skeleton } from '@/components/ui/skeleton';
import { useEffect, useState } from 'react';
import { getClientSideCookie } from '@/lib/getClientSideCookie';

const SideBar = () => {

  const pathname = usePathname();
  const router = useRouter();

  const [ userData, setUserData ] = useState<UserData>();

  useEffect(() => {
    const fetchData = async () => {
      const sessionId = getClientSideCookie("Session-Id");
      if (!sessionId) return;
      try {
        const data = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/session`, {
          method: "GET",
          headers: {
            "Content-Type" : "application/json",
            "Session-Id" : sessionId
          }
        });
        if (!data) {
          return;
        }
        setUserData(await data.json());
      } catch (error) {
        console.error(error);
      }
    } 
    fetchData();
  }, []);

  return (
    <div className="flex">
      <div className="hidden md:flex flex-col h-screen p-4 border-r w-[250px] gap-2">
        <div className="w-full flex items-center justify-center p-4">
          <Image src="/static/Logotipo_MatchPoint.svg" width={200} height={25} alt="Match Point" />
        </div>
        <Button
          variant={pathname === "/dashboard" ? "default" : "outline"}
          className="font-semibold justify-start gap-2"
          onClick={() => router.push("/dashboard")}
        >
          <MdInsights /> Dashboard
        </Button>

        <Button
          variant={pathname === "/tournaments" ? "default" : "outline"}
          className="font-semibold justify-start gap-2"
        >
          <BiSolidTennisBall /> Torneos
        </Button>

        {userData && (
          userData.role === "ADMIN" &&
          <Button
            variant={pathname === "/users" ? "default" : "outline"}
            className="font-semibold justify-start gap-2"
          >
            <FaUserShield /> Usuarios
          </Button>
        )}
        <div className="flex-grow"></div>
        {
          userData ? (
            <AccountButton userData={userData} isSelected={pathname === "/settings"} />
          ) : (
            <Skeleton className="w-full h-9" />
          )
        }
      </div>
      <div className="p-4 absolute">
        <Sheet>
          <SheetTrigger asChild key={pathname}>
            <Button
              variant="outline"
              size="icon"
              className="md:hidden flex"
            >
              <FiMenu className="h-4 w-4" />
            </Button>
          </SheetTrigger>
          <SheetContent side="left" className="flex flex-col bg-white pt-16">
            <SheetHeader>
              <SheetTitle>
                <div className="w-full flex items-center justify-center pb-4">
                  <Image src="/static/Logotipo_MatchPoint.svg" width={200} height={25} alt="Match Point" />
                </div>
              </SheetTitle>
              <SheetDescription></SheetDescription>
            </SheetHeader>
            <SheetClose asChild>
              <Button
                variant={pathname === "/dashboard" ? "default" : "outline"}
                className="font-semibold justify-start gap-2 py-6"
                onClick={() => router.push("/dashboard")}
              >
                <MdInsights /> Dashboard
              </Button>
            </SheetClose>
            <Button
              variant={pathname === "/tournaments" ? "default" : "outline"}
              className="font-semibold justify-start gap-2 py-6"
            >
              <BiSolidTennisBall /> Torneos
            </Button>
            {userData && (
              userData.role === "ADMIN" &&
              <Button
                variant={pathname === "/users" ? "default" : "outline"}
                className="font-semibold justify-start gap-2 py-6"
              >
                <FaUserShield /> Usuarios
              </Button>
            )}
            <div className="flex-grow"></div>
            {userData ? (
              <AccountButton userData={userData} isSelected={pathname === "/settings"} isMobile/>
            ) : (
              <Skeleton className="w-full h-12" />
            )
            }
          </SheetContent>
        </Sheet>
      </div>
    </div>
  )
}

export default SideBar;