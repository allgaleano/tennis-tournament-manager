import ConfirmEmail from '@/components/auth/confirm-email'
import { Suspense } from 'react';
import BarLoader from 'react-spinners/BarLoader';

const ConfirmEmailPage = () => {
  return (
    <Suspense fallback={
      <BarLoader />
    }>
      <ConfirmEmail />
    </Suspense>
  )
}

export default ConfirmEmailPage;