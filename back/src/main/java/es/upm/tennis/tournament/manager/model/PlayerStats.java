package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "players_stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    private int rankingPoints = 0;
    private int rankingPosition;
    private int tournamentsPlayed = 0;
    private int tournamentsWon = 0;
    private int totalMatchesPlayed = 0;
    private int totalMatchesWon = 0;
    private int totalMatchesLost = 0;
    private int totalSetsWon = 0;
    private int totalSetsLost = 0;
    private int totalGamesWon = 0;
    private int totalGamesLost = 0;
    private int totalTieBreaksWon = 0;
    private int totalTieBreaksLost = 0;

    public void addRankingPoints(int points) {
        this.rankingPoints += points;
    }

    public void updateStats(TournamentParticipation participation) {
        this.tournamentsPlayed++;

        this.totalMatchesPlayed += participation.getMatchesPlayed();
        this.totalMatchesWon += participation.getMatchesWon();
        this.totalMatchesLost += participation.getMatchesLost();

        this.totalSetsWon += participation.getSetsWon();
        this.totalSetsLost += participation.getSetsLost();

        this.totalGamesWon += participation.getGamesWon();
        this.totalGamesLost += participation.getGamesLost();

        this.totalTieBreaksWon += participation.getTieBreaksWon();
        this.totalTieBreaksLost += participation.getTieBreaksLost();
    }

    public int compareGlobalRanking(PlayerStats other) {
        int pointsComparison = Integer.compare(this.rankingPoints, other.rankingPoints);
        if (pointsComparison != 0) return pointsComparison;

        int setsWonComparison = Integer.compare(this.totalSetsWon, other.totalSetsWon);
        if (setsWonComparison != 0) return setsWonComparison;

        int gamesWonComparison = Integer.compare(this.totalGamesWon, other.totalGamesWon);
        if (gamesWonComparison != 0) return gamesWonComparison;

        int gamesLostComparison = Integer.compare(other.totalGamesLost, this.totalGamesLost);
        if (gamesLostComparison != 0) return gamesLostComparison;

        int tieBreaksWonComparison = Integer.compare(this.totalTieBreaksWon, other.totalTieBreaksWon);
        if (tieBreaksWonComparison != 0) return tieBreaksWonComparison;

        int tieBreaksLostComparison = Integer.compare(other.totalTieBreaksLost, this.totalTieBreaksLost);
        if (tieBreaksLostComparison != 0) return tieBreaksLostComparison;

        return Math.random() < 0.5 ? -1 : 1;
    }
}
