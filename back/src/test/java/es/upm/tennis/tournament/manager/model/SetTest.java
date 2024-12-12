package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Set Tests")
class SetTest {

    private Set set;

    @BeforeEach
    void setUp() {
        set = new Set();
    }

    @Test
    @DisplayName("Should set and get set number")
    void shouldSetAndGetSetNumber() {
        set.setSetNumber(1);
        assertEquals(1, set.getSetNumber());
    }

    @Test
    @DisplayName("Should set and get player one games won")
    void shouldSetAndGetPlayerOneGamesWon() {
        set.setPlayer1Games(6);
        assertEquals(6, set.getPlayer1Games());
    }

    @Test
    @DisplayName("Should set and get player two games won")
    void shouldSetAndGetPlayerTwoGamesWon() {
        set.setPlayer2Games(4);
        assertEquals(4, set.getPlayer2Games());
    }

    @Test
    @DisplayName("Should set and get player one tiebreak points")
    void shouldSetAndGetPlayerOneTiebreakPoints() {
        set.setPlayer1TiebreakGames(7);
        assertEquals(7, set.getPlayer1TiebreakGames());
    }

    @Test
    @DisplayName("Should set and get player two tiebreak points")
    void shouldSetAndGetPlayerTwoTiebreakPoints() {
        set.setPlayer2TiebreakGames(5);
        assertEquals(5, set.getPlayer2TiebreakGames());
    }

    @Test
    @DisplayName("Should determine if set is not tiebreak")
    void shouldDetermineIfSetIsNotTiebreak() {
        set.setPlayer1Games(6);
        set.setPlayer1Games(4);
        assertFalse(set.isTiebreak());
    }
}