import { CheckCircledIcon } from "@radix-ui/react-icons";
import { Alert, AlertTitle } from "@/components/ui/alert"

interface FormSuccessProps {
    label: string;
    variant?: "success" | "destructive";
}

export const FormAlert = ({
    label,
    variant
} : FormSuccessProps) => {
    if (!variant) 
        return null;

    return (
        <Alert variant={variant}>
            <CheckCircledIcon />
            <AlertTitle>{label}</AlertTitle>
        </Alert>
    )
}