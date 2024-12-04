package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create UserSession with valid user and session ID")
        void shouldCreateUserSessionWithValidUserAndSessionId() {
            // Arrange
            User user = new User();
            user.setId(1L);
            user.setUsername("testuser");
            String sessionId = "test-session-id";
            Instant expirationDate = Instant.now().plusSeconds(3600);

            // Act
            UserSession userSession = new UserSession();
            userSession.setUser(user);
            userSession.setSessionId(sessionId);
            userSession.setExpirationDate(expirationDate);

            // Assert
            assertNotNull(userSession.getUser());
            assertEquals(user, userSession.getUser());
            assertEquals(sessionId, userSession.getSessionId());
            assertEquals(expirationDate, userSession.getExpirationDate());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            UserSession userSession = new UserSession();
            Long id = 1L;

            // Act
            userSession.setId(id);

            // Assert
            assertEquals(id, userSession.getId());
        }

        @Test
        @DisplayName("Should get and set user")
        void shouldGetAndSetUser() {
            // Arrange
            UserSession userSession = new UserSession();
            User user = new User();
            user.setId(1L);
            user.setUsername("testuser");

            // Act
            userSession.setUser(user);

            // Assert
            assertEquals(user, userSession.getUser());
        }

        @Test
        @DisplayName("Should get and set session ID")
        void shouldGetAndSetSessionId() {
            // Arrange
            UserSession userSession = new UserSession();
            String sessionId = "test-session-id";

            // Act
            userSession.setSessionId(sessionId);

            // Assert
            assertEquals(sessionId, userSession.getSessionId());
        }

        @Test
        @DisplayName("Should get and set expiration date")
        void shouldGetAndSetExpirationDate() {
            // Arrange
            UserSession userSession = new UserSession();
            Instant expirationDate = Instant.now().plusSeconds(3600);

            // Act
            userSession.setExpirationDate(expirationDate);

            // Assert
            assertEquals(expirationDate, userSession.getExpirationDate());
        }
    }
}