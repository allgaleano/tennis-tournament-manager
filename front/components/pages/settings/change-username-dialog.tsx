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
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";

const ChangeUsernameDialog = ({ userData } : { userData : UserData }) => {
  
  
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast(); 
  
  const form = useForm<z.infer<typeof ChangeUsernameSchema>>({
    resolver: zodResolver(ChangeUsernameSchema),
    defaultValues: {
      username: userData.username,
    }
  })

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

      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/users/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type" : "application/json",
          "Session-Id" : sessionId
        }, 
        body: JSON.stringify({ username: values.username })  
      });

      const data = await response.json();

      toast({
        variant: response.ok ? "success" : "destructive",
        title: data.title,
        ...(data.description && { description: data.description })
      });      

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