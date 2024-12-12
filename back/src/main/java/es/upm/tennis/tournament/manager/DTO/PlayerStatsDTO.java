package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.PlayerStats;
import lombok.Data;

@Data
public class PlayerStatsDTO {
    private UserPublicDTO player;
    private int rankingPoints;
    private Integer rankingPosition;
    private int tournamentsPlayed;
    private int tournamentsWon;
    private int totalMatchesPlayed;
    private int totalMatchesWon;
    private int totalMatchesLost;
    private int totalSetsWon;
    private int totalSetsLost;
    private int totalGamesWon;
    private int totalGamesLost;
    private int totalTiebreakGamesWon;
    private int totalTiebreakGamesLost;

    public static PlayerStatsDTO fromEntity(PlayerStats pstats, Integer position) {
        PlayerStatsDTO dto = new PlayerStatsDTO();
        dto.setRankingPosition(position);
        dto.setPlayer(UserPublicDTO.fromEntity(pstats.getPlayer()));
        dto.setRankingPoints(pstats.getRankingPoints());
        dto.setTournamentsPlayed(pstats.getTournamentsPlayed());
        dto.setTournamentsWon(pstats.getTournamentsWon());
        dto.setTotalMatchesPlayed(pstats.getTotalMatchesPlayed());
        dto.setTotalMatchesWon(pstats.getTotalMatchesWon());
        dto.setTotalMatchesLost(pstats.getTotalMatchesLost());
        dto.setTotalSetsWon(pstats.getTotalSetsWon());
        dto.setTotalSetsLost(pstats.getTotalSetsLost());
        dto.setTotalGamesWon(pstats.getTotalGamesWon());
        dto.setTotalGamesLost(pstats.getTotalGamesLost());
        dto.setTotalTiebreakGamesWon(pstats.getTotalTiebreakGamesWon());
        dto.setTotalTiebreakGamesLost(pstats.getTotalTiebreakGamesLost());
        return dto;
    }
}
