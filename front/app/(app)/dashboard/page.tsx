import Dashboard from "@/components/pages/dashboard/dashboard";

const DashboardPage = ({
  searchParams
}: { 
  searchParams: { [key: string]: string | string[] | undefined }
}) => {
  return (
    <Dashboard searchParams={searchParams} />
  );
}

export default DashboardPage;
