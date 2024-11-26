"use client";

import { getClientSideUserData } from "@/lib/users/getClientSideUserData";
import { UserData } from "@/types";
import { useEffect, useState } from "react";
import DataCard from "@/components/pages/settings/data-card";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";
import { useToast } from "@/hooks/use-toast";
import { IoMdSettings } from "react-icons/io";
import Link from "next/link";
import { formateDateToSpanish } from "@/lib/common/formatDateToSpanish";

const Settings = () => {
  const [userData, setUserData] = useState<UserData>();
  const router = useRouter();
  const { toast } = useToast();

  useEffect(() => {
    const fetchData = async () => {
      const data = await getClientSideUserData();
      setUserData(data);
    }
    fetchData();
  }, []);

  const deleteAccount = async () => {
    const id = userData?.id;
    const sessionId = getClientSideCookie("Session-Id");
    if ((!id || !sessionId)) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "Intentalo de nuevo más tarde"
      })
      return;
    }
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${id}`, {
        method: "DELETE",
        headers: {
          "Content-Type" : "application/json",
          "Session-Id" : sessionId
        }
      });
  
      const data = await response.json();

      toast({
        variant: response.ok ? "success" : "destructive",
        title: data.title,
        ...(data.description && { description: data.description })
      });

      if (response.ok) {
        router.refresh();
      } 

    } catch (error) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "Error de red, Inténtalo de nuevo más tarde."
      });
      console.error("Error while deleting user account:", error);
    }
  }

  return (
    <section className="w-full flex flex-col justify-start items-center m-10 gap-8">
      <h1 className="font-semibold text-2xl flex items-center gap-2">
        <IoMdSettings />
        Ajustes de perfil
      </h1>
      <div className="flex flex-col items-center space-y-4 w-full max-w-[600px]">
        <DataCard title="Nombre:" label={`${userData?.name} ${userData?.surname}`} userData={userData}/>

        <DataCard title="Nombre de usuario:" label={userData?.username} isModifiable userData={userData} />

        <DataCard title="Teléfono:" label={userData?.phoneNumber} isModifiable userData={userData} />

        <DataCard title="Email:" label={userData?.email} userData={userData}/>

        <DataCard title="Rol:" label={userData?.role === "ADMIN" ? "Administrador" : "Jugador"} userData={userData} />

        <DataCard title="Fecha de creación de la cuenta:" label={userData ? formateDateToSpanish(userData.createdAt) : undefined} userData={userData}/>

        <div className="self-start flex gap-4">

          <Button
            variant="outline"
            className="font-semibold"
            asChild
          >
            <Link href="/change-password">Cambiar contraseña</Link>

          </Button>

          <Dialog>
            <DialogTrigger asChild>
              <Button
                variant="destructive"
                className="font-semibold"
              >Eliminar cuenta</Button>
            </DialogTrigger>
            <DialogContent className="bg-white">
              <DialogHeader>
                <DialogTitle>Eliminar cuenta</DialogTitle>
                <DialogDescription>
                  ¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es irreversible. Se borrarán todos los datos asociados a esta cuenta.
                </DialogDescription>
              </DialogHeader>
              <DialogFooter>
                <DialogClose asChild>
                  <div className="self-start flex gap-4">
                    <Button
                      variant="outline"
                    >
                      Cancelar
                    </Button>
                    <Button
                      variant="destructive"
                      onClick={() => deleteAccount()}
                    >
                      Eliminar
                    </Button>
                  </div>
                </DialogClose>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>
    </section>
  )
}

export default Settings;