package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TournamentTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create Tournament")
        void shouldCreateTournament() {
            // Arrange
            String name = "Grand Slam";

            // Act
            Tournament tournament = new Tournament();
            tournament.setName(name);

            // Assert
            assertNotNull(tournament.getName());
            assertEquals(name, tournament.getName());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            Tournament tournament = new Tournament();
            Long id = 1L;

            // Act
            tournament.setId(id);

            // Assert
            assertEquals(id, tournament.getId());
        }

        @Test
        @DisplayName("Should get and set name")
        void shouldGetAndSetName() {
            // Arrange
            Tournament tournament = new Tournament();
            String name = "Open Championship";

            // Act
            tournament.setName(name);

            // Assert
            assertEquals(name, tournament.getName());
        }
    }
}