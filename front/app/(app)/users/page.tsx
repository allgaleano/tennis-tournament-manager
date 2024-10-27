import AdminTable from "@/components/pages/usersAdmin/admin-table";

const UsersPage = ({ searchParams }: { searchParams: { [key: string]: string | string[] | undefined } }) => {
  return (
    <AdminTable searchParams={searchParams} />
  )
}

export default UsersPage;