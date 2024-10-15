import ConfirmPassword from "@/components/auth/confirm-password"
import { Suspense } from "react"
import BarLoader from "react-spinners/BarLoader"

const ConfirmPasswordPage = () => {
  return (
    <Suspense fallback={
      <BarLoader />
    }
    >
      <ConfirmPassword />
    </Suspense>
  )
}

export default ConfirmPasswordPage