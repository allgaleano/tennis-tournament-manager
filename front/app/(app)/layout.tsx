import SideBar from '@/components/side-bar'
import React from 'react'

const AppLayout = ({ children } : { children : React.ReactNode }) => {
  return (
    <div className="flex md:ml-[250px]">
      <SideBar />
      {children}
    </div>
  )
}

export default AppLayout