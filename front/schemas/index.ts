import * as z from "zod";

export const RegisterSchema = z.object({
    username: z.string().min(1, {
        message: "Introduce un nombre de usuario"
    }),
    email: z.string().email({
        message: "Introduce un email válido"
    }),
    password: z.string().min(6, {
        message: "Introduce una contraseña de al menos 6 caracteres"
    })
})

export const LoginSchema = z.object({
    username: z.string().min(1, {
        message: "Introduce un nombre de usuario"
    }),
    password: z.string().min(1, {
        message: "Introduce una contraseña"
    })
})

export const ChangePasswordSchema = z.object({
  email: z.string().email({
    message: "Introduce un email válido"
  })
})

export const ConfirmPasswordSchema = z.object({
  password: z.string().min(1, {
    message: "Introduce una contraseña"
  })
})