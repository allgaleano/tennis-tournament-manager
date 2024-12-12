package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.TournamentDTO;
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
    private TournamentEnrollmentRepository tournamentEnrollmentRepository;

    @Mock
    private PermissionChecker permissionChecker;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament tournament;
    private User user;
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
            when(tournamentRepository.findAllByOrderByRegistrationDeadlineAsc(pageable)).thenReturn(expectedPage);

            // Act
            Page<Tournament> result = tournamentService.getAllTournaments(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(tournament.getName(), result.getContent().get(0).getName());
            verify(tournamentRepository).findAllByOrderByRegistrationDeadlineAsc(pageable);
        }

        @Test
        @DisplayName("Should return tournament by ID")
        void getTournament_ShouldReturnTournament() {
            // Arrange
            when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

            // Act
            TournamentDTO result = tournamentService.getTournament(1L);

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
            CustomException ex = assertThrows(CustomException.class,
                    () -> tournamentService.getTournament(1L));

            assertEquals(ErrorCode.TOURNAMENT_NOT_FOUND, ex.getErrorCode());
        }
    }
}
