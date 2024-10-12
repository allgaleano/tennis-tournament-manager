import ChangePassword from "@/components/auth/change-password"
import { Suspense } from "react"
import BarLoader from "react-spinners/BarLoader"

const ChangePasswordPage = () => {
  return (
    <Suspense fallback={
      <BarLoader />
    }>
      <ChangePassword />
    </Suspense>
  )
}

export default ChangePasswordPage