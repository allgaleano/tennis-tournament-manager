import { MdInsights } from "react-icons/md"

const Dashboard = () => {
  return (
    <div className="w-full flex flex-col justify-start items-center m-10">
      <h1 className="font-semibold text-2xl flex items-center gap-2">
        <MdInsights />
        Dashboard
      </h1>
    </div>
  )
}

export default Dashboard