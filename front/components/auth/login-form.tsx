"use client";

import CardWrapper from "@/components/auth/card-wrapper";
import { useToast } from "@/hooks/use-toast";
import { LoginSchema } from "@/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { 
  Form, 
  FormControl, 
  FormField, 
  FormItem, 
  FormLabel, 
  FormMessage
} from "@/components/ui/form";

import { Input } from "@/components/ui/input";
import { Button } from "../ui/button";
import { useRouter } from "next/navigation";
import { DEFAULT_LOGIN_REDIRECT } from "@/routes";
import Link from "next/link";

const LoginForm = () => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { toast } = useToast();
  const router = useRouter();

  const form = useForm<z.infer<typeof LoginSchema>>({
    resolver: zodResolver(LoginSchema),
    defaultValues: {
      username: "",
      password: ""
    }
  })

  const onSubmit = async (values : z.infer<typeof LoginSchema>) => {
    setIsLoading(true);
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type" : "application/json"
        },
        body: JSON.stringify(values),
      });
      if (response.ok) {
        const data = await response.json();
        const sessionId = data.sessionId;
        const sessionExp = new Date(data.sessionExp);

        document.cookie = `Session-Id=${sessionId}; expires=${sessionExp.toUTCString()}; path=/; SameSite=None; Secure`;
        
        toast({
          variant: "success",
          title: "Sesión inicida con éxito"
        })

        setIsLoading(false);
        router.push(DEFAULT_LOGIN_REDIRECT);
        return;
      }
      if (response.status === 401) {
        toast({
          variant: "destructive",
          title: "Usuario o contraseña incorrectos",
          description: "Revisa que las credenciales sean correctas"
        })
      } else if (response.status === 403) {
        toast({
          variant: "destructive",
          title: "Cuenta no activada",
          description: "Confirma tu email para activar tu cuenta antes de inciar sesión"
        })
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "¡Algo ha salido mal!",
        description: "Inténtalo de nuevo más tarde"
      })
      console.error(error);
    }
    setIsLoading(false);
  }

   return (
    <CardWrapper
        label="Introduce tus credenciales"
        title="Inicia Sesión"
        backButtonHref="/register"
        backButtonLabel="¿No tienes una cuenta? Crea una aquí"
    >
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
          <div className="space-y-4">
            <FormField 
              control={form.control}
              name="username"
              render={({ field}) => (
                <FormItem>
                  <FormLabel className="ml-2">Nombre de usuario</FormLabel>
                  <FormControl>
                    <Input 
                      {...field} 
                      type="username" 
                      placeholder="Tu nombre de usuario"
                      disabled={isLoading}
                    />
                  </FormControl>
                  <FormMessage className="ml-2"/>
                </FormItem>
              )}
            />
            <div className="space-y-2">
              <FormField 
                control={form.control}
                name="password"
                render={({ field}) => (
                  <FormItem>
                    <FormLabel className="ml-2">Contraseña</FormLabel>
                    <FormControl>
                      <Input 
                        {...field} 
                        type="password" 
                        placeholder="******"
                        disabled={isLoading}
                      />
                    </FormControl>
                    <FormMessage className="ml-2"/>
                  </FormItem>
                )}
              />
              <Button variant="link" size="sm" >
                <Link href="/change-password">
                  He olvidado mi contraseña
                </Link>
              </Button>
            </div>
          </div>
          <Button className="w-full font-semibold" type="submit">Iniciar Sesión</Button>
        </form>
      </Form>
      
    </CardWrapper>
   )
}

export default LoginForm;
