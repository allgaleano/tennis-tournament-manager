import { MdInsights } from "react-icons/md"
import SectionHeader from "@/components/common/section-header"
import Charts from "./charts/charts";
import Ranking from "./ranking/ranking";

const Dashboard = ({ 
  searchParams 
} : { 
  searchParams: { page?: string; size?: string } 
}) => {
  return (
    <section className="w-full flex flex-col justify-start items-center m-10">
      <SectionHeader  title="Dashboard" Icon={MdInsights} />
      <Charts />
      <Ranking searchParams={searchParams} />
    </section>
  )
}

export default Dashboard;