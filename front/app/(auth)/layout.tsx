
import { Toaster } from '@/components/ui/toaster';
import React from 'react'

const AuthLayout = ({ children }: { children: React.ReactNode }) => {
  return (
    <section className="w-full">
        <div className="h-screen flex items-center justify-center">
            {children}
        </div>
        <Toaster />
    </section>
  )
}

export default AuthLayout;
