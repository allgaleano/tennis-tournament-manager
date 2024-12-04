package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TournamentEnrollmentTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create TournamentEnrollment with valid player and tournament")
        void shouldCreateTournamentEnrollmentWithValidPlayerAndTournament() {
            // Arrange
            User player = new User();
            Tournament tournament = new Tournament();

            // Act
            TournamentEnrollment enrollment = new TournamentEnrollment();
            enrollment.setPlayer(player);
            enrollment.setTournament(tournament);
            enrollment.setStatus(EnrollmentStatus.PENDING);

            // Assert
            assertNotNull(enrollment.getPlayer());
            assertNotNull(enrollment.getTournament());
            assertEquals(EnrollmentStatus.PENDING, enrollment.getStatus());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            TournamentEnrollment enrollment = new TournamentEnrollment();
            Long id = 1L;

            // Act
            enrollment.setId(id);

            // Assert
            assertEquals(id, enrollment.getId());
        }

        @Test
        @DisplayName("Should get and set player")
        void shouldGetAndSetPlayer() {
            // Arrange
            TournamentEnrollment enrollment = new TournamentEnrollment();
            User player = new User();

            // Act
            enrollment.setPlayer(player);

            // Assert
            assertEquals(player, enrollment.getPlayer());
        }

        @Test
        @DisplayName("Should get and set tournament")
        void shouldGetAndSetTournament() {
            // Arrange
            TournamentEnrollment enrollment = new TournamentEnrollment();
            Tournament tournament = new Tournament();

            // Act
            enrollment.setTournament(tournament);

            // Assert
            assertEquals(tournament, enrollment.getTournament());
        }

        @Test
        @DisplayName("Should get and set status")
        void shouldGetAndSetStatus() {
            // Arrange
            TournamentEnrollment enrollment = new TournamentEnrollment();
            EnrollmentStatus status = EnrollmentStatus.SELECTED;

            // Act
            enrollment.setStatus(status);

            // Assert
            assertEquals(status, enrollment.getStatus());
        }
    }
}