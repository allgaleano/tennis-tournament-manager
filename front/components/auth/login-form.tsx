'use client';

import CardWrapper from "@/components/auth/card-wrapper";

const LoginForm = () => {

   return (
    <CardWrapper
        label="Introduce tus credenciales"
        title="Inicia Sesión"
        backButtonHref="/register"
        backButtonLabel="¿No tienes una cuenta? Crea una aquí"
    >
        <div>

        </div>
    </CardWrapper>
   )
}

export default LoginForm;
