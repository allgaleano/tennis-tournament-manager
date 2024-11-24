package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.TournamentEnrollmentDTO;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tournament Service Tests")
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private PlayerTournamentRepository playerTournamentRepository;

    @Mock
    private TournamentEnrollmentRepository tournamentEnrollmentRepository;

    @Mock
    private PermissionChecker permissionChecker;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament tournament;
    private User user;
    private UserSession userSession;
    private TournamentEnrollment tournamentEnrollment;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        // Setup test data
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Test Tournament");
        tournament.setMaxPlayers(16);
        tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
        tournament.setRegistrationDeadline(Instant.now().plusSeconds(3600)); // 1 hour from now

        userRole = new Role();
        userRole.setType(ERole.USER);

        adminRole = new Role();
        adminRole.setType(ERole.ADMIN);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(userRole);
        user.setConfirmed(true);

        userSession = new UserSession();
        userSession.setSessionId("test-session");
        userSession.setUser(user);
        userSession.setExpirationDate(Instant.now().plusSeconds(3600));

        tournamentEnrollment = new TournamentEnrollment();
        tournamentEnrollment.setId(1L);
        tournamentEnrollment.setPlayer(user);
        tournamentEnrollment.setTournament(tournament);
        tournamentEnrollment.setStatus(EnrollmentStatus.PENDING);
    }

    @Nested
    @DisplayName("Tournament Listing Tests")
    class TournamentListingTests {

        @Test
        @DisplayName("Should return page of tournaments")
        void getAllTournaments_ShouldReturnPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Tournament> expectedPage = new PageImpl<>(Collections.singletonList(tournament));
            when(tournamentRepository.findAll(pageable)).thenReturn(expectedPage);

            // Act
            Page<Tournament> result = tournamentService.getAllTournaments(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(tournament.getName(), result.getContent().getFirst().getName());
            verify(tournamentRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should return tournament by ID")
        void getTournament_ShouldReturnTournament() {
            // Arrange
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

            // Act
            Tournament result = tournamentService.getTournament(1L);

            // Assert
            assertNotNull(result);
            assertEquals(tournament.getName(), result.getName());
            verify(tournamentRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when tournament not found")
        void getTournament_ShouldThrowException_WhenNotFound() {
            // Arrange
            when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class,
                    () -> tournamentService.getTournament(1L));
        }
    }

    @Nested
    @DisplayName("Tournament Enrollment Tests")
    class TournamentEnrollmentTests {

        @Test
        @DisplayName("Should successfully enroll player when tournament is open")
        void enrollPlayer_ShouldSucceed_WhenTournamentOpen() {
            // Arrange
            tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            when(tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(1L, 1L)).thenReturn(false);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act
            assertDoesNotThrow(() ->
                    tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));

            // Assert
            verify(tournamentEnrollmentRepository).save(any(TournamentEnrollment.class));
        }

        @Test
        @DisplayName("Should throw when regular user enrolls in closed tournament")
        void enrollPlayer_ShouldThrow_WhenRegularUserAndTournamentClosed() {
            // Arrange
            tournament.setStatus(TournamentStatus.ENROLLMENT_CLOSED);
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act & Assert
            assertThrows(IllegalStateException.class,
                    () -> tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));
        }

        @Test
        @DisplayName("Admin should be able to enroll player in closed tournament")
        void enrollPlayer_ShouldSucceed_WhenAdminAndTournamentClosed() {
            // Arrange
            tournament.setStatus(TournamentStatus.ENROLLMENT_CLOSED);
            user.setRole(adminRole);
            userSession.setUser(user); // Update session with admin user

            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            when(tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(1L, 1L)).thenReturn(false);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act
            assertDoesNotThrow(() ->
                    tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));

            // Assert
            verify(tournamentEnrollmentRepository).save(any(TournamentEnrollment.class));
        }

        @Test
        @DisplayName("Admin should be able to enroll player in in-progress tournament")
        void enrollPlayer_ShouldSucceed_WhenAdminAndTournamentInProgress() {
            // Arrange
            tournament.setStatus(TournamentStatus.IN_PROGRESS);
            user.setRole(adminRole);
            userSession.setUser(user);

            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            when(tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(1L, 1L)).thenReturn(false);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act
            assertDoesNotThrow(() ->
                    tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));

            // Assert
            verify(tournamentEnrollmentRepository).save(any(TournamentEnrollment.class));
        }
    }

    @Nested
    @DisplayName("Tournament Unenrollment Tests")
    class TournamentUnenrollmentTests {

        @Test
        @DisplayName("Should successfully unenroll player")
        void unenrollPlayer_ShouldSucceed() {
            // Arrange
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            when(tournamentEnrollmentRepository.findByTournamentIdAndPlayerId(1L, 1L))
                    .thenReturn(Optional.of(tournamentEnrollment));
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act
            assertDoesNotThrow(() ->
                    tournamentService.unenrollPlayerFromTournament(1L, 1L, "test-session"));

            // Assert
            verify(tournamentEnrollmentRepository).delete(tournamentEnrollment);
        }

        @Test
        @DisplayName("Should throw exception when player not enrolled")
        void unenrollPlayer_ShouldThrow_WhenNotEnrolled() {
            // Arrange
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            when(tournamentEnrollmentRepository.findByTournamentIdAndPlayerId(1L, 1L))
                    .thenReturn(Optional.empty());
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act & Assert
            assertThrows(PlayerNotEnrolledException.class,
                    () -> tournamentService.unenrollPlayerFromTournament(1L, 1L, "test-session"));
        }

        @Test
        @DisplayName("Should throw exception when player already selected")
        void unenrollPlayer_ShouldThrow_WhenAlreadySelected() {
            // Arrange
            tournamentEnrollment.setStatus(EnrollmentStatus.SELECTED);
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            when(tournamentEnrollmentRepository.findByTournamentIdAndPlayerId(1L, 1L))
                    .thenReturn(Optional.of(tournamentEnrollment));
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act & Assert
            assertThrows(PlayerAlreadyAcceptedException.class,
                    () -> tournamentService.unenrollPlayerFromTournament(1L, 1L, "test-session"));
        }
    }

    @Nested
    @DisplayName("Tournament Enrollment Listing Tests")
    class TournamentEnrollmentListingTests {

        @Test
        @DisplayName("Should return page of enrollments")
        void getEnrollments_ShouldReturnPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<TournamentEnrollment> enrollmentPage = new PageImpl<>(
                    Collections.singletonList(tournamentEnrollment));
            when(tournamentRepository.existsById(1L)).thenReturn(true);
            when(tournamentEnrollmentRepository.findAllByTournamentId(1L, pageable))
                    .thenReturn(enrollmentPage);

            // Act
            Page<TournamentEnrollmentDTO> result = tournamentService.getTournamentEnrollments(1L, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            TournamentEnrollmentDTO dto = result.getContent().get(0);
            assertEquals(tournamentEnrollment.getId(), dto.getId());
            assertEquals(tournamentEnrollment.getStatus(), dto.getStatus());
            assertEquals(user.getId(), dto.getPlayer().getId());
            assertEquals(user.getUsername(), dto.getPlayer().getUsername());
        }

        @Test
        @DisplayName("Should throw exception when tournament not found")
        void getEnrollments_ShouldThrow_WhenTournamentNotFound() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(tournamentRepository.existsById(1L)).thenReturn(false);

            // Act & Assert
            assertThrows(NoSuchElementException.class,
                    () -> tournamentService.getTournamentEnrollments(1L, pageable));
        }
    }

    @Nested
    @DisplayName("Player Enrollment Status Tests")
    class PlayerEnrollmentStatusTests {

        @Test
        @DisplayName("Should return true when player is enrolled")
        void isPlayerEnrolled_ShouldReturnTrue_WhenEnrolled() {
            // Arrange
            when(tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(1L, 1L))
                    .thenReturn(true);

            // Act
            boolean result = tournamentService.isPlayerEnrolled(1L, 1L);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when player is not enrolled")
        void isPlayerEnrolled_ShouldReturnFalse_WhenNotEnrolled() {
            // Arrange
            when(tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(1L, 1L))
                    .thenReturn(false);

            // Act
            boolean result = tournamentService.isPlayerEnrolled(1L, 1L);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Tournament Status Tests")
    class TournamentStatusTests {

        @Test
        @DisplayName("Should throw when enrolling in closed tournament")
        void enrollPlayer_ShouldThrow_WhenEnrollmentClosed() {
            // Arrange
            tournament.setStatus(TournamentStatus.ENROLLMENT_CLOSED);
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act & Assert
            assertThrows(IllegalStateException.class,
                    () -> tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));
        }

        @Test
        @DisplayName("Should throw when enrolling in in-progress tournament")
        void enrollPlayer_ShouldThrow_WhenTournamentInProgress() {
            // Arrange
            tournament.setStatus(TournamentStatus.IN_PROGRESS);
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act & Assert
            assertThrows(IllegalStateException.class,
                    () -> tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));
        }

        @Test
        @DisplayName("Should throw when enrolling in finished tournament")
        void enrollPlayer_ShouldThrow_WhenTournamentFinished() {
            // Arrange
            tournament.setStatus(TournamentStatus.FINISHED);
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("test-session")).thenReturn(userSession);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act & Assert
            assertThrows(IllegalStateException.class,
                    () -> tournamentService.enrollPlayerToTournament(1L, 1L, "test-session"));
        }
    }
}