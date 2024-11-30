"use client";

import CardWrapper from "./card-wrapper";
import { Form, FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { ConfirmPasswordSchema } from "@/schemas";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { Button } from "../ui/button";
import BarLoader from "react-spinners/BarLoader";
import { CheckCircledIcon, CrossCircledIcon } from "@radix-ui/react-icons";
import { useSearchParams } from "next/navigation";
import { ApiResponse } from "@/types";

const ConfirmPassword = () => {
  const searchParams = useSearchParams();
  const token = searchParams.get("token");

  const [ isLoading, setIsLoading ] = useState<boolean>(false);
  const [ title, setTitle ] = useState<string>("Elige una contraseña nueva");
  const [ label, setLabel ] = useState<string>("Cambia tu contraseña y envía la confirmación");
  const [ state, setState ] = useState<"success"| "failed" | undefined>(undefined);

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
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/auth/confirm-password?token=${token}`, {
        method: "POST",
        headers: {
          "Content-Type" : "application/json"
        },
        body: JSON.stringify(values.password)
      })

      const data: ApiResponse = await response.json();

      if (response.ok) {
        setState("success");
      } else {
        setState("failed");
      } 
    
      setTitle(data.title);
      setLabel(data.description);
    
    } catch (error) {
      console.error(error);
      setState("failed");
      setTitle("¡Algo ha salido mal!");
      setLabel("Vuelve a intentarlo más tarde");
    }
    setIsLoading(false);
  }

  return (
    <CardWrapper
      title={title}
      label={label}
    >
      { !isLoading ? 
        (
          state == "success" ? (
            <div className="w-full flex items-center justify-center text-success">
              <CheckCircledIcon height={30} width={30} />
            </div>
          ) : state == "failed" ? (
            <div className="w-full flex items-center justify-center text-destructive">
              <CrossCircledIcon height={30} width={30} />
            </div>
          ) : (
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
          )
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