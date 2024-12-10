package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tournament_participations")
public class TournamentParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_stats_id", nullable = false)
    private PlayerStats playerStats;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    private int matchesPlayed = 0;
    private int matchesWon = 0;
    private int matchesLost = 0;
    private int setsWon = 0;
    private int setsLost = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;
    private int tiebreakGamesWon = 0;
    private int tiebreakGamesLost = 0;
    private int points = 0;

    public void incrementMatchesPlayed() {
        this.matchesPlayed++;
        playerStats.incrementMatchesPlayed();
    }

    public void incrementMatchesWon() {
        this.matchesWon++;
        playerStats.incrementMatchesWon();
    }

    public void incrementMatchesLost() {
        this.matchesLost++;
        playerStats.incrementMatchesLost();
    }

    public void incrementSetsWon(int n) {
        this.setsWon = this.setsWon + n;
        playerStats.incrementSetsWon(n);
    }

    public void incrementSetsLost(int n) {
        this.setsLost = this.setsLost + n;
        playerStats.incrementSetsLost(n);
    }

    public void incrementGamesWon(int n) {
        this.gamesWon = this.gamesWon + n;
        playerStats.incrementGamesWon(n);
    }

    public void incrementGamesLost(int n) {
        this.gamesLost = this.gamesLost + n;
        playerStats.incrementGamesLost(n);
    }

    public void incrementTiebreakGamesWon(int n) {
        this.tiebreakGamesWon = this.tiebreakGamesWon + n;
        playerStats.incrementTiebreakGamesWon(n);
    }

    public void incrementTiebreakGamesLost(int n) {
        this.tiebreakGamesLost = this.tiebreakGamesLost + n;
        playerStats.incrementTiebreakGamesLost(n);
    }
}
