package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTournamentTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create PlayerTournament with valid player and tournament")
        void shouldCreatePlayerTournamentWithValidPlayerAndTournament() {
            // Arrange
            User player = new User();
            Tournament tournament = new Tournament();

            // Act
            PlayerTournament playerTournament = new PlayerTournament();
            playerTournament.setPlayer(player);
            playerTournament.setTournament(tournament);
            playerTournament.setPoints(10);
            playerTournament.setSetsWon(2);
            playerTournament.setGamesWon(6);
            playerTournament.setGamesLost(3);

            // Assert
            assertNotNull(playerTournament.getPlayer());
            assertNotNull(playerTournament.getTournament());
            assertEquals(10, playerTournament.getPoints());
            assertEquals(2, playerTournament.getSetsWon());
            assertEquals(6, playerTournament.getGamesWon());
            assertEquals(3, playerTournament.getGamesLost());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            Long id = 1L;

            // Act
            playerTournament.setId(id);

            // Assert
            assertEquals(id, playerTournament.getId());
        }

        @Test
        @DisplayName("Should get and set player")
        void shouldGetAndSetPlayer() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            User player = new User();

            // Act
            playerTournament.setPlayer(player);

            // Assert
            assertEquals(player, playerTournament.getPlayer());
        }

        @Test
        @DisplayName("Should get and set tournament")
        void shouldGetAndSetTournament() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            Tournament tournament = new Tournament();

            // Act
            playerTournament.setTournament(tournament);

            // Assert
            assertEquals(tournament, playerTournament.getTournament());
        }

        @Test
        @DisplayName("Should get and set points")
        void shouldGetAndSetPoints() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            int points = 15;

            // Act
            playerTournament.setPoints(points);

            // Assert
            assertEquals(points, playerTournament.getPoints());
        }

        @Test
        @DisplayName("Should get and set sets won")
        void shouldGetAndSetSetsWon() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            int setsWon = 3;

            // Act
            playerTournament.setSetsWon(setsWon);

            // Assert
            assertEquals(setsWon, playerTournament.getSetsWon());
        }

        @Test
        @DisplayName("Should get and set games won")
        void shouldGetAndSetGamesWon() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            int gamesWon = 8;

            // Act
            playerTournament.setGamesWon(gamesWon);

            // Assert
            assertEquals(gamesWon, playerTournament.getGamesWon());
        }

        @Test
        @DisplayName("Should get and set games lost")
        void shouldGetAndSetGamesLost() {
            // Arrange
            PlayerTournament playerTournament = new PlayerTournament();
            int gamesLost = 4;

            // Act
            playerTournament.setGamesLost(gamesLost);

            // Assert
            assertEquals(gamesLost, playerTournament.getGamesLost());
        }
    }
}