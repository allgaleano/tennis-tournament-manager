"use client";

import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import CardWrapper from "./card-wrapper";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { ChangePasswordSchema } from "@/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useToast } from "@/hooks/use-toast";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { Button } from "../ui/button";

const ChangePassword = () => {
  const { toast } = useToast();
  const [ isLoading, setIsLoading ] = useState<boolean>(false);

  const form = useForm<z.infer<typeof ChangePasswordSchema>>({
    resolver: zodResolver(ChangePasswordSchema),
    defaultValues: {
      email: ""
    }
  });

  const onSubmit = async (values: z.infer<typeof ChangePasswordSchema>) => {
    setIsLoading(true);
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/auth/change-password`, {
        method: "POST",
        headers: {
          "Content-Type" : "application/json"
        },
        body: JSON.stringify(values.email)
      });
      if (response.ok) {
        toast({
          variant: "success",
          title: "Email de confirmación enviado",
          description: "Comprueba de tu bandeja de entrada"
        })
      } else if (response.status === 404) {
        toast({
          variant: "destructive",
          title: "Usuario inexistente",
          description: "No existe ninguna cuenta con ese email asociado"
        })
      } else {
        throw new Error("Error interno del servidor");
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
      title="Cambiar contraseña"
      label="Introduce tu email para cambiar de contraseña"
      backButtonLabel="Volver"
      backButtonHref="/login"
    >
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
          <div className="space-y-4">
            <FormField 
              control={form.control}
              name="email"
              render={({ field}) => (
                <FormItem>
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
          </div>
          <Button className="w-full font-semibold" type="submit">Enviar confirmación</Button>
        </form>
      </Form>
    </CardWrapper>
  )
}

export default ChangePassword