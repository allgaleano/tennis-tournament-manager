"use client";

import { 
    Card,
    CardContent,
    CardHeader,
    CardFooter 
} from "@/components/ui/card"
import AuthHeader from "@/components/auth/auth-header";
import BackButton from "./back-button";

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
  )
}

export default CardWrapper