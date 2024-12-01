package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmationCodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create ConfirmationCode with valid user and expiration date")
        void shouldCreateConfirmationCodeWithValidUserAndExpirationDate() {
            // Arrange
            User user = new User();
            int validMinutes = 10;

            // Act
            ConfirmationCode confirmationCode = new ConfirmationCode(user, validMinutes);

            // Assert
            assertNotNull(confirmationCode.getCode());
            assertEquals(user, confirmationCode.getUser());
            assertTrue(confirmationCode.getExpirationDate().isAfter(Instant.now()));
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            ConfirmationCode confirmationCode = new ConfirmationCode();
            Long id = 1L;

            // Act
            confirmationCode.setId(id);

            // Assert
            assertEquals(id, confirmationCode.getId());
        }

        @Test
        @DisplayName("Should get and set code")
        void shouldGetAndSetCode() {
            // Arrange
            ConfirmationCode confirmationCode = new ConfirmationCode();
            String code = UUID.randomUUID().toString();

            // Act
            confirmationCode.setCode(code);

            // Assert
            assertEquals(code, confirmationCode.getCode());
        }

        @Test
        @DisplayName("Should get and set expiration date")
        void shouldGetAndSetExpirationDate() {
            // Arrange
            ConfirmationCode confirmationCode = new ConfirmationCode();
            Instant expirationDate = Instant.now().plusSeconds(600);

            // Act
            confirmationCode.setExpirationDate(expirationDate);

            // Assert
            assertEquals(expirationDate, confirmationCode.getExpirationDate());
        }

        @Test
        @DisplayName("Should get and set user")
        void shouldGetAndSetUser() {
            // Arrange
            ConfirmationCode confirmationCode = new ConfirmationCode();
            User user = new User();

            // Act
            confirmationCode.setUser(user);

            // Assert
            assertEquals(user, confirmationCode.getUser());
        }
    }
}