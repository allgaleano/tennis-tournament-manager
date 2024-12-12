package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TournamentParticipationTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create PlayerTournament with valid player and tournament")
        void shouldCreatePlayerTournamentWithValidPlayerAndTournament() {
            // Arrange
            Tournament tournament = new Tournament();

            // Act
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            tournamentParticipation.setTournament(tournament);
            tournamentParticipation.setPoints(10);
            tournamentParticipation.setSetsWon(2);
            tournamentParticipation.setGamesWon(6);
            tournamentParticipation.setGamesLost(3);

            // Assert
            assertNotNull(tournamentParticipation.getTournament());
            assertEquals(10, tournamentParticipation.getPoints());
            assertEquals(2, tournamentParticipation.getSetsWon());
            assertEquals(6, tournamentParticipation.getGamesWon());
            assertEquals(3, tournamentParticipation.getGamesLost());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            Long id = 1L;

            // Act
            tournamentParticipation.setId(id);

            // Assert
            assertEquals(id, tournamentParticipation.getId());
        }

        @Test
        @DisplayName("Should get and set player")
        void shouldGetAndSetPlayer() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();

            // Act
            tournamentParticipation.setSetsWon(2);

            // Assert
            assertEquals(2, tournamentParticipation.getSetsWon());
        }

        @Test
        @DisplayName("Should get and set tournament")
        void shouldGetAndSetTournament() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            Tournament tournament = new Tournament();

            // Act
            tournamentParticipation.setTournament(tournament);

            // Assert
            assertEquals(tournament, tournamentParticipation.getTournament());
        }

        @Test
        @DisplayName("Should get and set points")
        void shouldGetAndSetPoints() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            int points = 15;

            // Act
            tournamentParticipation.setPoints(points);

            // Assert
            assertEquals(points, tournamentParticipation.getPoints());
        }

        @Test
        @DisplayName("Should get and set sets won")
        void shouldGetAndSetSetsWon() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            int setsWon = 3;

            // Act
            tournamentParticipation.setSetsWon(setsWon);

            // Assert
            assertEquals(setsWon, tournamentParticipation.getSetsWon());
        }

        @Test
        @DisplayName("Should get and set games won")
        void shouldGetAndSetGamesWon() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            int gamesWon = 8;

            // Act
            tournamentParticipation.setGamesWon(gamesWon);

            // Assert
            assertEquals(gamesWon, tournamentParticipation.getGamesWon());
        }

        @Test
        @DisplayName("Should get and set games lost")
        void shouldGetAndSetGamesLost() {
            // Arrange
            TournamentParticipation tournamentParticipation = new TournamentParticipation();
            int gamesLost = 4;

            // Act
            tournamentParticipation.setGamesLost(gamesLost);

            // Assert
            assertEquals(gamesLost, tournamentParticipation.getGamesLost());
        }
    }
}