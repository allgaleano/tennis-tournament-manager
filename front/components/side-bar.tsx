"use client";

import { Button } from '@/components/ui/button'
import { usePathname } from 'next/navigation';
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
import { getClientSideUserData } from '@/lib/getClientSideUserData';
import Link from 'next/link';

const SideBar = () => {

  const pathname = usePathname();

  const [ userData, setUserData ] = useState<UserData>();

  useEffect(() => {
    const fetchData = async () => {
      setUserData(await getClientSideUserData());
    }
    fetchData();
  }, []);

  return (
    <div className="flex">
      <div className="hidden md:flex flex-col fixed top-0 left-0 h-screen p-4 border-r w-[250px] gap-2 bg-white z-50">
        <div className="w-full flex items-center justify-center p-4">
          <Image src="/static/Logotipo_MatchPoint.svg" width={200} height={25} alt="Match Point" />
        </div>
        <Button
          variant={pathname === "/dashboard" ? "default" : "outline"}
          className="font-semibold justify-start gap-2"
          asChild
        >
          <Link href="/dashboard"> <MdInsights /> Dashboard </Link>
        </Button>

        <Button
          variant={pathname === "/tournaments" ? "default" : "outline"}
          className="font-semibold justify-start gap-2"
          asChild
        >
          <Link href="/tournaments">
            <BiSolidTennisBall /> Torneos
          </Link>
        </Button>

        {userData && (
          userData.role === "ADMIN" &&
          <Button
            variant={pathname === "/users" ? "default" : "outline"}
            className="font-semibold justify-start gap-2"
            asChild
          >
            <Link href="/users"> <FaUserShield /> Usuarios </Link>
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
                asChild
              >
                <Link href="/dashboard"> <MdInsights /> Dashboard </Link>
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
                asChild
              >
                <Link href="/users"> <FaUserShield /> Usuarios </Link>
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