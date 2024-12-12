interface ChartWrapperProps {
  children: React.ReactNode;
  title: string;
}

const ChartWrapper = ({ 
  children,
  title
} : ChartWrapperProps) => {
  return (
    <div className="w-full max-w-2xl h-72 p-4 border rounded-sm shadow-sm">
      <h2 className="text-lg font-semibold mb-4">{title}</h2>
      {children}
    </div>
  )
}

export default ChartWrapper;