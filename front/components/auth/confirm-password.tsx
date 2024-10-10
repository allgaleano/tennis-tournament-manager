"use client";

import CardWrapper from "./card-wrapper";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { ConfirmPasswordSchema } from "@/schemas";
import { useToast } from "@/hooks/use-toast";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { Button } from "../ui/button";
import BarLoader from "react-spinners/BarLoader";

const ConfirmPassword = () => {
  const { toast } = useToast();
  const [ isLoading, setIsLoading ] = useState<boolean>(false);
  const [ title, setTitle ] = useState<string>("Elige una contraseña nueva");
  const [ label, setLabel ] = useState<string>("Cambia tu contraseña y envía la confirmación");

  const form = useForm<z.infer<typeof ConfirmPasswordSchema>>({
    resolver: zodResolver(ConfirmPasswordSchema),
    defaultValues: {
      password: ""
    }
  });

  const onSubmit = async (values : z.infer<typeof ConfirmPasswordSchema>) => {
    setIsLoading(true);
    setTitle("Validando contraseña");
    setLabel("");
    setTimeout(() => {
      toast({
        title: values.password
      })  
      setTitle("Elige una contraseña nueva");
      setLabel("Cambia tu contraseña y envía la confirmación");
      setIsLoading(false);
    }, 3000)
  }

  return (
    <CardWrapper
      title={title}
      label={label}
    >
      { !isLoading ? (
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <div className="space-y-4">
              <FormField 
                control={form.control}
                name="password"
                render={({ field}) => (
                  <FormItem>
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
            </div>
            <Button className="w-full font-semibold" type="submit">Enviar confirmación</Button>
          </form>
        </Form>
        ) : (
          <div className="w-full flex items-center justify-center">
            <BarLoader />
          </div>
        )
      }
    </CardWrapper>
  )
}

export default ConfirmPassword;