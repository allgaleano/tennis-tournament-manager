"use client";

import { getClientSideUserData } from "@/lib/getClientSideUserData";
import { UserData } from "@/types";
import { useEffect, useState } from "react";
import { Skeleton } from "../ui/skeleton";
import DataCard from "../data-card";
import { Button } from "../ui/button";
import { useRouter } from "next/navigation";

const Settings = () => {
  const [userData, setUserData] = useState<UserData>();
  const router = useRouter();

  useEffect(() => {
    const fetchData = async () => {
      setUserData(await getClientSideUserData());
    }
    fetchData();
  }, []);
  
  const formateDateToSpanish = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleString("es-Es", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "numeric",
      minute: "numeric",
    })
  }

  return (
    <div className="w-full flex flex-col justify-start items-center m-10 gap-8">
      <h1 className="font-semibold text-2xl">Ajustes de perfil</h1>
      {userData ? (
        <div className="flex flex-col items-center space-y-4 w-full max-w-[600px]">
          <DataCard title="Nombre:" label={`${userData.name} ${userData.surname}`} />

          <DataCard title="Nombre de usuario:" label={userData.username} isModifiable/>
          
          <DataCard title="Teléfono:" label={userData.phoneNumber} isModifiable/>

          <DataCard title="Email:" label={userData.email} />

          <DataCard title="Rol:" label={userData.role === "ADMIN" ? "Administrador" : "Jugador"} />
          
          <DataCard title="Fecha de creación de la cuenta:" label={formateDateToSpanish(userData.createdAt)} />
          
          <Button 
            variant="outline" 
            className="self-start font-semibold"
            onClick={() => router.push("/change-password")}
          >Cambiar contraseña</Button>
        </div>
      ) : (
        <div className="flex flex-col justify-center items-center w-full space-y-4">
          <Skeleton className="w-full max-w-[600px] h-16 "/>
          <Skeleton className="w-full max-w-[600px] h-16 "/>
          <Skeleton className="w-full max-w-[600px] h-16 "/>
          <Skeleton className="w-full max-w-[600px] h-16 "/>
          <Skeleton className="w-full max-w-[600px] h-16 "/>
          <Skeleton className="w-full max-w-[600px] h-16 "/>
          <Skeleton className="w-full max-w-[150px] h-8 "/>
        </div>
        
      )}
    </div>
  )
}

export default Settings;