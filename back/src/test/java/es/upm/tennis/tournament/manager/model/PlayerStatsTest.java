package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Player Stats Tests")
class PlayerStatsTest {

    private PlayerStats playerStats;

    @BeforeEach
    void setUp() {
        playerStats = new PlayerStats();
    }

    @Test
    @DisplayName("Should add ranking points")
    void addRankingPoints_ShouldIncreaseRankingPoints() {
        playerStats.addRankingPoints(10);
        assertEquals(10, playerStats.getRankingPoints());
    }

    @Test
    @DisplayName("Should increment tournaments played")
    void incrementTournamentsPlayed_ShouldIncreaseTournamentsPlayed() {
        playerStats.incrementTournamentsPlayed();
        assertEquals(1, playerStats.getTournamentsPlayed());
    }

    @Test
    @DisplayName("Should increment tournaments won")
    void incrementTournamentsWon_ShouldIncreaseTournamentsWon() {
        playerStats.incrementTournamentsWon();
        assertEquals(1, playerStats.getTournamentsWon());
    }

    @Test
    @DisplayName("Should increment matches played")
    void incrementMatchesPlayed_ShouldIncreaseMatchesPlayed() {
        playerStats.incrementMatchesPlayed();
        assertEquals(1, playerStats.getTotalMatchesPlayed());
    }

    @Test
    @DisplayName("Should increment matches won")
    void incrementMatchesWon_ShouldIncreaseMatchesWon() {
        playerStats.incrementMatchesWon();
        assertEquals(1, playerStats.getTotalMatchesWon());
    }

    @Test
    @DisplayName("Should increment matches lost")
    void incrementMatchesLost_ShouldIncreaseMatchesLost() {
        playerStats.incrementMatchesLost();
        assertEquals(1, playerStats.getTotalMatchesLost());
    }

    @Test
    @DisplayName("Should increment sets won")
    void incrementSetsWon_ShouldIncreaseSetsWon() {
        playerStats.incrementSetsWon(2);
        assertEquals(2, playerStats.getTotalSetsWon());
    }

    @Test
    @DisplayName("Should increment sets lost")
    void incrementSetsLost_ShouldIncreaseSetsLost() {
        playerStats.incrementSetsLost(2);
        assertEquals(2, playerStats.getTotalSetsLost());
    }

    @Test
    @DisplayName("Should increment games won")
    void incrementGamesWon_ShouldIncreaseGamesWon() {
        playerStats.incrementGamesWon(3);
        assertEquals(3, playerStats.getTotalGamesWon());
    }

    @Test
    @DisplayName("Should increment games lost")
    void incrementGamesLost_ShouldIncreaseGamesLost() {
        playerStats.incrementGamesLost(3);
        assertEquals(3, playerStats.getTotalGamesLost());
    }

    @Test
    @DisplayName("Should increment tiebreak games won")
    void incrementTiebreakGamesWon_ShouldIncreaseTiebreakGamesWon() {
        playerStats.incrementTiebreakGamesWon(1);
        assertEquals(1, playerStats.getTotalTiebreakGamesWon());
    }

    @Test
    @DisplayName("Should increment tiebreak games lost")
    void incrementTiebreakGamesLost_ShouldIncreaseTiebreakGamesLost() {
        playerStats.incrementTiebreakGamesLost(1);
        assertEquals(1, playerStats.getTotalTiebreakGamesLost());
    }
}