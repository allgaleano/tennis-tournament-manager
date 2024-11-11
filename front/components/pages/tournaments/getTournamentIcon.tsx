import { MdSunny } from "react-icons/md";
import { RiFlowerFill } from "react-icons/ri";
import { FaRegSnowflake } from "react-icons/fa";
import { GiThreeLeaves } from "react-icons/gi";
import { BiSolidTennisBall } from 'react-icons/bi'


export const getTournamentIcon = (tournamentName: string) => {
  const lowerCaseName = tournamentName.toLowerCase();
  if (lowerCaseName.includes("verano")) return MdSunny;
  if (lowerCaseName.includes("invierno")) return FaRegSnowflake;
  if (lowerCaseName.includes("primavera")) return RiFlowerFill;
  if (lowerCaseName.includes("otoño")) return GiThreeLeaves;
  return BiSolidTennisBall;
};