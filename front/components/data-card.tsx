"use client";

import ChangePhoneDialog from "@/components/change-phone-dialog";
import ChangeUsernameDialog from "@/components/change-username-dialog";
import { UserData } from "@/types";

interface DataCardProps {
  title: string;
  label: string;
  isModifiable?: boolean
  userData?: UserData
}
const DataCard = ({
  title,
  label,
  isModifiable,
  userData
}: DataCardProps) => {
  return (
    <div className="border w-full rounded-lg py-2 px-4 shadow-sm flex justify-between items-center">
      <div className="">
        <p className="font-bold">{title}</p>
        <p>{label}</p>
      </div>
      {isModifiable &&
        title === "Nombre de usuario:" ? (
          <ChangeUsernameDialog userData={userData}/>
        ) : title === "Teléfono:" && (
          <ChangePhoneDialog userData={userData}/>
        )
      }
    </div>
  )
}

export default DataCard;