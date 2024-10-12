"use client";

import {
  Card,
  CardContent,
  CardFooter,
  CardHeader
} from "@/components/ui/card";
import AuthHeader from "@/components/auth/auth-header";
import BarLoader from "react-spinners/BarLoader";
import { useEffect, useState } from "react";
import { CheckCircledIcon, CrossCircledIcon } from "@radix-ui/react-icons";
import { useSearchParams } from "next/navigation";
import BackButton from "@/components/auth/back-button";

const ConfirmEmail = () => {
  const searchParams = useSearchParams();
  const token = searchParams.get("token");

  const [title, setTitle] = useState<string>("Verificando tu cuenta");
  const [label, setLabel] = useState<string>("");
  const [loading, setIsLoading] = useState<boolean>(true);
  const [success, setSuccess] = useState<boolean>(false);

  useEffect(() => {
    const confirmEmail = async () => {

      setIsLoading(true);
      setSuccess(false);
      if (!token) {
        setTitle("Error al verificar la cuenta");
        setLabel("El enlace ha expirado o no es válido");
        setIsLoading(false);
        return
      }
      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/auth/confirm-email?token=${token}`, {
          method: "POST",
          headers: {
            "Content-Type" : "application/json",
          },
        });

        if (response.ok) {
          setTitle("Cuenta verificada con éxito");
          setLabel("Ya puedes iniciar sesión con tu cuenta");
          setSuccess(true);
        } else {
          setTitle("Error al verificar la cuenta");
          setLabel("El enlace ha expirado o no es válido");
          setSuccess(false);
        }
      } catch(error) {
        setTitle("Error al verificar la cuenta");
        setLabel("Ha ocurrido un error, por favor inténtelo de nuevo más tarde");
        setSuccess(false);
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    }
    confirmEmail();
  }, [token]);

  return (
    <Card className="w-[90%] max-w-[500px]">
      <CardHeader>
        <AuthHeader
          title={title}
          label={label}
        />
      </CardHeader>
      <CardContent>
        <div className="w-full flex items-center justify-center">
          {loading ?
            <BarLoader
              loading={loading}
            /> : (success ?
              <div className="text-success">
                <CheckCircledIcon height={30} width={30} />
              </div> :
              <div className="text-destructive">
                <CrossCircledIcon height={30} width={30}/>
              </div>)}
        </div>
      </CardContent>
      { success &&
        <CardFooter>
          <BackButton label="Haz click aquí para iniciar sesión" href="/login"/>
        </CardFooter>
      }
    </Card>
  )
}

export default ConfirmEmail;