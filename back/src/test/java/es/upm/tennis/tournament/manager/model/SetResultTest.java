package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetResultTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create SetResult with valid match and scores")
        void shouldCreateSetResultWithValidMatchAndScores() {
            // Arrange
            Match match = new Match();

            // Act
            SetResult setResult = new SetResult();
            setResult.setMatch(match);

            // Assert
            assertNotNull(setResult.getMatch());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            SetResult setResult = new SetResult();
            Long id = 1L;

            // Act
            setResult.setId(id);

            // Assert
            assertEquals(id, setResult.getId());
        }

        @Test
        @DisplayName("Should get and set match")
        void shouldGetAndSetMatch() {
            // Arrange
            SetResult setResult = new SetResult();
            Match match = new Match();

            // Act
            setResult.setMatch(match);

            // Assert
            assertEquals(match, setResult.getMatch());
        }
    }
}