"use client";

import { fetchPlayerStats } from "@/lib/dashboard/fetchPlayerStats";
import { PlayerGlobalStats } from "@/types";
import { useEffect, useState } from "react";
import WinLossChart from "./win-loss-chart";
import TournamentsStats from "./tournaments-stats";
import StatisticsBarChart from "./statistics-bar-chart";

const Charts = () => {
  const [playerStats, setPlayerStats] = useState<PlayerGlobalStats | null>(null);

  useEffect(() => {
    const fetchStats = async () => {
      const stats = await fetchPlayerStats();
      setPlayerStats(stats);
    }
    fetchStats();
  }, []);

  if (!playerStats) {
    return;
  }

  return (
    <div className="flex justify-center items-center my-8 gap-8 gap-y-20 w-full flex-wrap">
      <WinLossChart 
        matchesWon={playerStats.totalMatchesWon}
        matchesLost={playerStats.totalMatchesLost}
      />
      <TournamentsStats stats={playerStats} />
      <div className="w-full flex justify-center items-center">
        <StatisticsBarChart stats={playerStats} />
      </div>
    </div>
  )
}

export default Charts;