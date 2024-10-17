"use client";

import { Button } from "./ui/button";

interface DataCardProps {
  title: string;
  label: string;
  isModifiable?: boolean
}
const DataCard = ({
  title,
  label,
  isModifiable
}: DataCardProps) => {
  return (
    <div className="border w-full rounded-lg py-2 px-4 shadow-sm flex justify-between items-center">
      <div className="">
        <p className="font-bold">{title}</p>
        <p>{label}</p>
      </div>
      {isModifiable &&
        <Button variant="outline">Modificar</Button>
      }
    </div>
  )
}

export default DataCard;