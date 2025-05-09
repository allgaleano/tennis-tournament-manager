"use client";

import { useToast } from "@/hooks/use-toast";
import { ChangePhoneNumberSchema } from "@/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { UserData } from "@/types";
import { z } from "zod";
import { useState } from "react";
import { extractPhoneDetails } from "@/lib/settings/extractPhoneDetails";
import { getClientSideCookie } from "@/lib/users/getClientSideCookie";

const ChangePhoneDialog = ({ userData } : { userData : UserData }) => {

  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast(); 

  const { prefix, phoneNumber } = extractPhoneDetails(userData.phoneNumber);
  
  const form = useForm<z.infer<typeof ChangePhoneNumberSchema>>({
    resolver: zodResolver(ChangePhoneNumberSchema),
    defaultValues: {
      phonePrefix: prefix,
      phoneNumber: phoneNumber
    }
  })

  const onSubmit = async (values: z.infer<typeof ChangePhoneNumberSchema>) => {
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
        body: JSON.stringify({ phonePrefix: values.phonePrefix, phoneNumber: values.phoneNumber })  
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
          <DialogTitle>Cambia tu número de teléfono</DialogTitle>
          <DialogDescription>Introduce tu nuevo número para cambiarlo</DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <div className="w-full flex gap-2">
              <FormField 
                control={form.control}
                name="phonePrefix"
                render={({ field}) => (
                  <FormItem className="w-1/4">
                    <FormControl>
                      <div className="flex items-center gap-1">
                        <span>+</span>
                        <Input 
                          {...field}  
                          type="string"
                          placeholder="34"
                          disabled={isLoading}
                        />
                      </div>
                    </FormControl>
                    <FormMessage className="ml-2"/>
                  </FormItem>
                )}
              />
              <FormField 
                control={form.control}
                name="phoneNumber"
                render={({ field}) => (
                  <FormItem className="w-full">
                    <FormControl>
                      <Input 
                        {...field}
                        type="string" 
                        placeholder="600 000 000"
                        disabled={isLoading}
                      />
                    </FormControl>
                    <FormMessage className="ml-2"/>
                  </FormItem>
                )}
              />
            </div>
            <DialogClose className="w-full" asChild>
              <Button type="submit" className="w-full">Guardar</Button>
            </DialogClose>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}


export default ChangePhoneDialog;