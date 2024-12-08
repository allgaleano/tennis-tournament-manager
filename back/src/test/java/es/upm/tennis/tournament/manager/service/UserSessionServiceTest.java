package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Session Service Tests")
class UserSessionServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private UserSessionService userSessionService;

    private User testUser;
    private UserSession testSession;
    private static final int SESSION_DURATION_MINUTES = 1440;

    @BeforeEach
    void setUp() {
        // Create test user
        Role userRole = new Role();
        userRole.setType(ERole.USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);
        testUser.setRole(userRole);

        // Create test session with expiration date set to 1 hour from now
        testSession = new UserSession();
        testSession.setUser(testUser);
        testSession.setSessionId("test-session-id");
        testSession.setExpirationDate(Instant.now().plus(1, ChronoUnit.HOURS));
    }

    @Nested
    @DisplayName("Create Session Tests")
    class CreateSessionTests {

        @Test
        @DisplayName("Should create new session when user has no existing session")
        void createSession_ShouldCreateNew_WhenNoExistingSession() {
            // Arrange
            when(userSessionRepository.findByUser(testUser)).thenReturn(null);
            when(userSessionRepository.save(any(UserSession.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            UserSession result = userSessionService.createSession(testUser);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getSessionId());
            assertEquals(testUser, result.getUser());

            // Verify expiration date is set correctly (within a small margin of error)
            Instant expectedExpiration = Instant.now().plusSeconds(SESSION_DURATION_MINUTES * 60);
            assertTrue(
                    Math.abs(result.getExpirationDate().getEpochSecond() - expectedExpiration.getEpochSecond()) < 2,
                    "Expiration date should be approximately SESSION_DURATION_MINUTES from now"
            );

            verify(userSessionRepository).save(any(UserSession.class));
        }

        @Test
        @DisplayName("Should update existing session when user has one")
        void createSession_ShouldUpdate_WhenExistingSession() {
            // Arrange
            Instant originalExpiration = testSession.getExpirationDate();
            when(userSessionRepository.findByUser(testUser)).thenReturn(testSession);
            when(userSessionRepository.save(any(UserSession.class))).thenAnswer(i -> {
                // Capture the new expiration date for verification
                return i.getArgument(0);
            });

            // Act
            UserSession result = userSessionService.createSession(testUser);

            // Assert
            assertNotNull(result);
            assertEquals(testSession.getSessionId(), result.getSessionId());

            // Verify the new expiration date is later than the original
            assertTrue(result.getExpirationDate().isAfter(originalExpiration),
                    "New expiration date should be later than original");

            // Verify the new expiration date is approximately SESSION_DURATION_MINUTES from now
            Instant expectedExpiration = Instant.now().plusSeconds(SESSION_DURATION_MINUTES * 60);
            assertTrue(
                    Math.abs(result.getExpirationDate().getEpochSecond() - expectedExpiration.getEpochSecond()) < 2,
                    "New expiration date should be approximately SESSION_DURATION_MINUTES from now"
            );

            verify(userSessionRepository).save(any(UserSession.class));
        }
    }

    @Nested
    @DisplayName("Validate Session Tests")
    class ValidateSessionTests {

        @Test
        @DisplayName("Should return true for valid and active session")
        void validateSession_ShouldReturnTrue_WhenSessionValid() {
            // Arrange
            testSession.setExpirationDate(Instant.now().plusSeconds(SESSION_DURATION_MINUTES * 60));
            when(userSessionRepository.findBySessionId(testSession.getSessionId())).thenReturn(testSession);
            when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

            // Act
            boolean result = userSessionService.validateSession(testSession.getSessionId());

            // Assert
            assertTrue(result);
            verify(userSessionRepository).save(any(UserSession.class));
        }

        @Test
        @DisplayName("Should return false for expired session")
        void validateSession_ShouldReturnFalse_WhenSessionExpired() {
            // Arrange
            testSession.setExpirationDate(Instant.now().minus(1, ChronoUnit.HOURS));
            when(userSessionRepository.findBySessionId(testSession.getSessionId())).thenReturn(testSession);

            // Act
            boolean result = userSessionService.validateSession(testSession.getSessionId());

            // Assert
            assertFalse(result);
            verify(userSessionRepository, never()).save(any(UserSession.class));
        }

        @Test
        @DisplayName("Should return false for disabled user")
        void validateSession_ShouldReturnFalse_WhenUserDisabled() {
            // Arrange
            testUser.setEnabled(false);
            when(userSessionRepository.findBySessionId(testSession.getSessionId())).thenReturn(testSession);

            // Act
            boolean result = userSessionService.validateSession(testSession.getSessionId());

            // Assert
            assertFalse(result);
            verify(userSessionRepository, never()).save(any(UserSession.class));
        }


        @Test
        @DisplayName("Should return false for non-existent session")
        void validateSession_ShouldReturnFalse_WhenSessionNotFound() {
            // Arrange
            when(userSessionRepository.findBySessionId(anyString())).thenReturn(null);

            // Act
            boolean result = userSessionService.validateSession("non-existent-session");

            // Assert
            assertFalse(result);
            verify(userSessionRepository, never()).save(any(UserSession.class));
        }
    }

    @Nested
    @DisplayName("Invalidate Session Tests")
    class InvalidateSessionTests {

        @Test
        @DisplayName("Should successfully invalidate existing session")
        void invalidateSession_ShouldReturnTrue_WhenSessionExists() {
            // Arrange
            when(userSessionRepository.findBySessionId(testSession.getSessionId())).thenReturn(testSession);
            doNothing().when(userSessionRepository).delete(testSession);

            // Act
             assertDoesNotThrow(
                     () -> userSessionService.invalidateSession(testSession.getSessionId())
             );

            // Assert
            verify(userSessionRepository).delete(testSession);
        }

        @Test
        @DisplayName("Should return false when trying to invalidate non-existent session")
        void invalidateSession_ShouldReturnFalse_WhenSessionNotFound() {
            // Arrange
            when(userSessionRepository.findBySessionId(anyString())).thenReturn(null);

            // Act
            CustomException ex  = assertThrows(CustomException.class,
                    () -> userSessionService.invalidateSession("non-existent-session"));

            // Assert
            assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
            verify(userSessionRepository, never()).delete(any(UserSession.class));
        }
    }

    @Nested
    @DisplayName("Find Session Tests")
    class FindSessionTests {

        @Test
        @DisplayName("Should find session by session ID")
        void findBySessionId_ShouldReturnSession_WhenExists() {
            // Arrange
            when(userSessionRepository.findBySessionId(testSession.getSessionId())).thenReturn(testSession);

            // Act
            UserSession result = userSessionService.getUserSession(testSession.getSessionId());

            // Assert
            assertNotNull(result);
            assertEquals(testSession.getSessionId(), result.getSessionId());
        }

        @Test
        @DisplayName("Should find session by user")
        void findByUser_ShouldReturnSession_WhenExists() {
            // Arrange
            when(userSessionRepository.findByUser(testUser)).thenReturn(testSession);

            // Act
            UserSession result = userSessionService.findByUser(testUser);

            // Assert
            assertNotNull(result);
            assertEquals(testUser, result.getUser());
        }
    }
}