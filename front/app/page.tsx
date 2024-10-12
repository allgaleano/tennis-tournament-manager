import Dashboard from "@/components/dashboard";
import { getUserData } from "@/lib/getUserData";
import BarLoader from "react-spinners/BarLoader";

export default async function Home() {
  const userData = await getUserData();
  return (
    <div>
      {userData ? (
        <Dashboard userData={userData} />
      ) : (
        <div className="w-full h-screen flex justify-center items-center">
          <BarLoader />
        </div>
      )}
    </div>
  );
}
