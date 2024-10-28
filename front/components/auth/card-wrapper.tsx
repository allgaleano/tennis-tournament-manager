"use client";

import { 
    Card,
    CardContent,
    CardHeader,
    CardFooter 
} from "@/components/ui/card"
import AuthHeader from "@/components/auth/auth-header";
import BackButton from "./back-button";
import Image from "next/image";

interface CardWrapperProps {
    label?:string,
    title?: string,
    backButtonHref?: string,
    backButtonLabel?: string,
    children: React.ReactNode
}

const CardWrapper = ({    
    label,
    title,
    backButtonHref,
    backButtonLabel,
    children
} : CardWrapperProps) => {
  return (
    <div className="w-full flex flex-col min-h-screen items-center justify-center gap-12">
      <div className="w-full flex flex-col justify-center items-center gap-4 py-4">
        <Image src="/static/Logotipo_MatchPoint.svg" width={300} height={100} alt="MatchPoint Logo" />
        <i className="text-muted-foreground">Gestor de torneos de tenis</i>
      </div>
      <Card className="w-[90%] max-w-[500px]">
          <CardHeader>
              {title && <AuthHeader label={label} title={title}/>}
          </CardHeader>
          <CardContent>
              {children}
          </CardContent>
          <CardFooter>
              {backButtonHref && backButtonLabel && <BackButton label={backButtonLabel} href={backButtonHref} />}
          </CardFooter>
      </Card>
    </div>
  )
}

export default CardWrapper