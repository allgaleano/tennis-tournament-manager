import { MdInsights } from "react-icons/md"
import SectionHeader from "@/components/common/section-header"

const Dashboard = () => {
  return (
    <section className="w-full flex flex-col justify-start items-center m-10">
      <SectionHeader  title="Dashboard" Icon={MdInsights} />
    </section>
  )
}

export default Dashboard