"use client";

import {
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip
} from 'recharts';
import ChartWrapper from './chart-wrapper';

interface WinLossChartProps {
  matchesWon: number;
  matchesLost: number;
}

const COLORS = ['#26bf51', '#ad2424'];

const WinLossChart = ({
  matchesWon,
  matchesLost,
}: WinLossChartProps) => {
  const data = [
    { name: 'Ganados', value: matchesWon },
    { name: 'Perdidos', value: matchesLost }
  ]
  return (
    <ChartWrapper title="Ratio Partidos Ganados/Perdidos">
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
            label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend
            layout="horizontal"
            verticalAlign="bottom"
            align="center"
            wrapperStyle={{ 
              paddingTop: "10px",
              position: "relative",
              marginTop: "-10px"
            }}
          />
        </PieChart>
      </ResponsiveContainer>
    </ChartWrapper>
  )
}

export default WinLossChart;