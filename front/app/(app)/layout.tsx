import SideBar from '@/components/side-bar'
import React from 'react'

const AppLayout = ({ children } : { children : React.ReactNode }) => {
  return (
    <div className="flex">
      <SideBar />
      {children}
    </div>
  )
}

export default AppLayout