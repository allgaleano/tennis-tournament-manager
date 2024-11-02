"use client";

import ChangePhoneDialog from "@/components/change-phone-dialog";
import ChangeUsernameDialog from "@/components/change-username-dialog";
import { UserData } from "@/types";
import { Skeleton } from "./ui/skeleton";

interface DataCardProps {
  title: string | undefined;
  label: string | undefined;
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
      <div>
        {(userData) ? (
          <div>
            <p className="font-bold">{title}</p>
            <p>{label}</p>
          </div>
        ) : (
          <div className="h-10 flex items-center">
            <Skeleton className="w-36 h-2" />
          </div>
        )
        }
      </div>
      {(isModifiable && userData ) && (
        title === "Nombre de usuario:" ? (
          <ChangeUsernameDialog userData={userData}/>
        ) : title === "Teléfono:" && (
          <ChangePhoneDialog userData={userData}/>
        )
      )
      }
    </div>
  )
}

export default DataCard;