import { MdInsights } from "react-icons/md"
import SectionHeader from "@/components/common/section-header"
import Charts from "./charts/charts";

const Dashboard = () => {
  return (
    <section className="w-full flex flex-col justify-start items-center m-10">
      <SectionHeader  title="Dashboard" Icon={MdInsights} />
      <Charts />
    </section>
  )
}

export default Dashboard;