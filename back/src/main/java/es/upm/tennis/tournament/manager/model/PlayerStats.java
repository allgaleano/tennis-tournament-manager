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
    private int tournamentsPlayed = 0;
    private int tournamentsWon = 0;
    private int totalMatchesPlayed = 0;
    private int totalMatchesWon = 0;
    private int totalMatchesLost = 0;
    private int totalSetsWon = 0;
    private int totalSetsLost = 0;
    private int totalGamesWon = 0;
    private int totalGamesLost = 0;
    private int totalTiebreakGamesWon = 0;
    private int totalTiebreakGamesLost = 0;

    public void addRankingPoints(int points) {
        this.rankingPoints += points;
    }

    public void incrementTournamentsPlayed() {
        this.tournamentsPlayed++;
    }

    public void incrementTournamentsWon() {
        this.tournamentsWon++;
    }

    public void incrementMatchesPlayed() {
        this.totalMatchesPlayed++;
    }

    public void incrementMatchesWon() {
        this.totalMatchesWon++;
    }

    public void incrementMatchesLost() {
        this.totalMatchesLost++;
    }

    public void incrementSetsWon(int n) {
        this.totalSetsWon = this.totalSetsWon + n;
    }

    public void incrementSetsLost(int n) {
        this.totalSetsLost = this.totalSetsLost + n;
    }

    public void incrementGamesWon(int n) {
        this.totalGamesWon = this.totalGamesWon + n;
    }

    public void incrementGamesLost(int n) {
        this.totalGamesLost = this.totalGamesLost + n;
    }

    public void incrementTiebreakGamesWon(int n) {
        this.totalTiebreakGamesWon = this.totalTiebreakGamesWon + n;
    }

    public void incrementTiebreakGamesLost(int n) {
        this.totalTiebreakGamesLost = this.totalTiebreakGamesLost + n;
    }
}
