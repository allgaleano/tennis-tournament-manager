"use client";

import { PlayerGlobalStats } from "@/types";
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import ChartWrapper from "./chart-wrapper";

const COLORS = ['#26bf51', '#ad2424'];

interface StatisticsBarChartProps {
  stats: PlayerGlobalStats;
}

const StatisticsBarChart = ({
  stats
}: StatisticsBarChartProps) => {

  const data = [
    { name: 'Sets', won: stats.totalSetsWon, lost: stats.totalSetsLost },
    { name: 'Juegos', won: stats.totalGamesWon, lost: stats.totalGamesLost },
    { name: 'Tiebreaks', won: stats.totalTiebreakGamesWon, lost: stats.totalTiebreakGamesLost }
  ];

  return (
    <ChartWrapper title="Estadísticas de rendimiento">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={data}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
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
          <Bar dataKey="won" name="Ganados" fill={COLORS[0]} />
          <Bar dataKey="lost" name="Perdidos" fill={COLORS[1]} />
        </BarChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

export default StatisticsBarChart;