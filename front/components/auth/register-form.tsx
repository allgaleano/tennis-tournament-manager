"use client";

import CardWrapper from "@/components/auth/card-wrapper";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage
} from "@/components/ui/form";
import { RegisterSchema } from "@/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { useToast } from "@/hooks/use-toast";


const RegisterForm = () => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { toast } = useToast();

  const form = useForm<z.infer<typeof RegisterSchema>>({
    resolver: zodResolver(RegisterSchema),
    defaultValues: {
      username: "",
      email: "",
      password: ""
    }
  })

  const onSubmit = async (values : z.infer<typeof RegisterSchema>) => {
    setIsLoading(true);
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(values),
      });
      if (!response.ok) {
        if (response.status === 409) {
          toast({
            variant: "destructive",
            title: "El usuario ya existe",
            description: "Cambia el nombre de usuario o el email e inténtalo de nuevo"
          });
        } 
        setIsLoading(false);
        return;
      }

      toast({
        title: "Confirma tu email",
        description: "Comprueba tu bandeja de entrada y verifica tu cuenta"
      })
    } catch(error) {
      toast({
        variant: "destructive",
        title: "¡Algo ha salido mal!",
        description: "Error interno del servidor"
      })
      console.error(error);
    }
    setIsLoading(false);
  }

  return (
    <CardWrapper
      label="Crea una cuenta"
      title="Regístrate"
      backButtonHref="/login"
      backButtonLabel="¿Ya tienes una cuenta? Inicia sesión aquí"
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
            <FormField 
              control={form.control}
              name="email"
              render={({ field}) => (
                <FormItem>
                  <FormLabel className="ml-2">Email</FormLabel>
                  <FormControl>
                    <Input 
                      {...field} 
                      type="email" 
                      placeholder="ejemplo@email.com"
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
              <Button variant="link" size="sm">He olvidado mi contraseña</Button>
            </div>
          </div>
          <Button className="w-full" type="submit">Crear cuenta</Button>
        </form>
      </Form>
    </CardWrapper>
  )
}

export default RegisterForm;

