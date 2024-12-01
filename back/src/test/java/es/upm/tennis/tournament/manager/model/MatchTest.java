package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create Match with valid tournament and players")
        void shouldCreateMatchWithValidTournamentAndPlayers() {
            // Arrange
            Tournament tournament = new Tournament();
            User player1 = new User();
            User player2 = new User();

            // Act
            Match match = new Match();
            match.setTournament(tournament);
            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setRound(4);

            // Assert
            assertNotNull(match.getTournament());
            assertNotNull(match.getPlayer1());
            assertNotNull(match.getPlayer2());
            assertEquals(4, match.getRound());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            Match match = new Match();
            Long id = 1L;

            // Act
            match.setId(id);

            // Assert
            assertEquals(id, match.getId());
        }

        @Test
        @DisplayName("Should get and set tournament")
        void shouldGetAndSetTournament() {
            // Arrange
            Match match = new Match();
            Tournament tournament = new Tournament();

            // Act
            match.setTournament(tournament);

            // Assert
            assertEquals(tournament, match.getTournament());
        }

        @Test
        @DisplayName("Should get and set player1")
        void shouldGetAndSetPlayer1() {
            // Arrange
            Match match = new Match();
            User player1 = new User();

            // Act
            match.setPlayer1(player1);

            // Assert
            assertEquals(player1, match.getPlayer1());
        }

        @Test
        @DisplayName("Should get and set player2")
        void shouldGetAndSetPlayer2() {
            // Arrange
            Match match = new Match();
            User player2 = new User();

            // Act
            match.setPlayer2(player2);

            // Assert
            assertEquals(player2, match.getPlayer2());
        }

        @Test
        @DisplayName("Should get and set round")
        void shouldGetAndSetRound() {
            // Arrange
            Match match = new Match();
            int round = 3;

            // Act
            match.setRound(round);

            // Assert
            assertEquals(round, match.getRound());
        }

        @Test
        @DisplayName("Should get and set winnerId")
        void shouldGetAndSetWinnerId() {
            // Arrange
            Match match = new Match();
            Long winnerId = 2L;

            // Act
            match.setWinnerId(winnerId);

            // Assert
            assertEquals(winnerId, match.getWinnerId());
        }
    }
}