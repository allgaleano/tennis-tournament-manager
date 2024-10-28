import * as z from "zod";

export const RegisterSchema = z.object({
  name: z.string().min(1, {
    message: "Introduce un nombre"
  }),
  surname: z.string().min(1, {
    message: "Introduce un nombre"
  }),
  phonePrefix: z
    .string()
    .min(1, { message: "Introduce un prefijo" }) // Allow minimum 1 digit for international prefixes
    .max(5, { message: "Prefijo demasiado largo" }) // Max length of 5 digits for prefixes
    .regex(/^\d+$/, { message: "El prefijo solo puede contener números" }), // Ensure it's numeric
  phoneNumber: z
    .string()
    .min(9, { message: "Introduce un número de teléfono válido" }) // Minimum 9 digits
    .max(15, { message: "Número demasiado largo" }) // Maximum 15 digits for international numbers
    .regex(/^\d+$/, { message: "El número de teléfono solo puede contener números" }), // Ensure it's numeric
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

export const ChangeUsernameSchema = z.object({
  username: z.string().min(1, {
    message: "Introduce un nombre de usuario válido"
  })
})

export const ChangePhoneNumberSchema = z.object({
  phonePrefix: z
    .string()
    .min(1, { message: "Introduce un prefijo" }) // Allow minimum 1 digit for international prefixes
    .max(5, { message: "Prefijo demasiado largo" }) // Max length of 5 digits for prefixes
    .regex(/^\d+$/, { message: "El prefijo solo puede contener números" }), // Ensure it's numeric
  phoneNumber: z
    .string()
    .min(9, { message: "Introduce un número de teléfono válido" }) // Minimum 9 digits
    .max(15, { message: "Número demasiado largo" }) // Maximum 15 digits for international numbers
    .regex(/^\d+$/, { message: "El número de teléfono solo puede contener números" }), // Ensure it's numeric
})