"use client";

import { Dialog, DialogClose, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import * as z from "zod";
import { useForm } from "react-hook-form";
import { ChangeUsernameSchema } from "@/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { UserData } from "@/types";
import { useToast } from "@/hooks/use-toast";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "./ui/input";
import { useState } from "react";

const ChangeUsernameDialog = ({ userData } : { userData : UserData | undefined }) => {

  if (!userData) return;

  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast(); 

  const form = useForm<z.infer<typeof ChangeUsernameSchema>>({
    resolver: zodResolver(ChangeUsernameSchema),
    defaultValues: {
      username: userData.username,
    }
  })

  const onSubmit = async (values: z.infer<typeof ChangeUsernameSchema>) => {
    toast({
      variant: "success",
      title: values.username
    })
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