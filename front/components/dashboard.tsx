"use client";

import { UserData } from '@/types';
import SideBar from './side-bar';

const Dashboard = ({ userData } : { userData: UserData }) => {

  return (
    <div className="flex">
      <SideBar userData={userData} />
      <div className="w-full h-screen flex flex-col justify-center items-center">
        <div className="space-y-4">
          <div className="space-y-2">
            <p>Welcome <b>{userData.username}</b></p>
            <p>Email: {userData.email}</p>
            <p>Roles: {userData.roles}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard;