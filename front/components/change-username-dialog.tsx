"use client";

import { Dialog, DialogClose, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import * as z from "zod";
import { useForm } from "react-hook-form";
import { ChangeUsernameSchema } from "@/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { UserData } from "@/types";
import { useToast } from "@/hooks/use-toast";
import { Form, FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { Input } from "./ui/input";
import { useState } from "react";
import { getClientSideCookie } from "@/lib/getClientSideCookie";
import { changeUsername } from "@/lib/changeUsername";

const ChangeUsernameDialog = ({ userData } : { userData : UserData | undefined }) => {
  
  
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast(); 
  
  const form = useForm<z.infer<typeof ChangeUsernameSchema>>({
    resolver: zodResolver(ChangeUsernameSchema),
    defaultValues: {
      username: userData?.username,
    }
  })
  if (!userData) return;

  const onSubmit = async (values: z.infer<typeof ChangeUsernameSchema>) => {
    setIsLoading(true);
    const id = userData?.id;
    const sessionId = getClientSideCookie("Session-Id");
    if ((!id || !sessionId)) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "Intentalo de nuevo más tarde"
      })
      setIsLoading(false);
      return;
    }
    try {
      const response = await changeUsername({
        userId: id,
        sessionId: sessionId,
        newUsername: values.username
      });

      if (response.success) {
        toast({
          variant: "success",
          title: "Nombre de usuario cambiado con éxito"
        });
        setIsLoading(false);
        return;
      } else if (response.error === "username-taken"){
        toast({
          variant: "destructive",
          title: "Nombre de usuario en uso",
          description: "Prueba con otro nombre de usuario"
        });
      } else {
        toast({
          variant: "destructive",
          title: "¡Algo ha ido mal!",
          description: "Inténtalo de nuevo más tarde"
        });  
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "¡Algo ha ido mal!",
        description: "Inténtalo de nuevo más tarde"
      });
      console.log(error);
    }
    setIsLoading(false);
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button 
          variant="outline"
        >
          Modificar
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-white">
        <DialogHeader>
          <DialogTitle>Cambia tu nombre de usuario</DialogTitle>
          <DialogDescription>Introduce tu nombre de usuario para cambiarlo</DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField 
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input 
                      type="username"
                      placeholder="Nombre de usuario"
                      disabled={isLoading} 
                      {...field}
                    />
                  </FormControl>
                  <FormMessage className="ml-2" />
                </FormItem>
              )}
            />
            <DialogClose className="w-full" asChild>
              <Button type="submit" className="w-full">Guardar</Button>
            </DialogClose>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}

export default ChangeUsernameDialog;