package es.upm.tennis.tournament.manager.initializers;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentStatus;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import es.upm.tennis.tournament.manager.utils.TournamentsInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tournaments Initializer Tests")
class TournamentsInitializerTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentsInitializer tournamentsInitializer;

    private static final String VERANO_2024 = "Verano 2024";
    private static final String OTONO_2024 = "Otoño 2024";
    private static final String INVIERNO_2024 = "Invierno 2024";
    private static final String PRIMAVERA_2025 = "Primavera 2025";

    @BeforeEach
    void setUp() {
        when(tournamentRepository.findByName(anyString())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("Should initialize all tournaments when none exist")
    void run_ShouldInitializeAllTournaments_WhenNoneExist() {
        // Act
        tournamentsInitializer.run();

        // Assert
        verify(tournamentRepository, times(4)).save(any(Tournament.class));
        verify(tournamentRepository).findByName(VERANO_2024);
        verify(tournamentRepository).findByName(OTONO_2024);
        verify(tournamentRepository).findByName(INVIERNO_2024);
        verify(tournamentRepository).findByName(PRIMAVERA_2025);
    }

    @Test
    @DisplayName("Should skip existing tournaments and create only missing ones")
    void run_ShouldSkipExistingTournaments() {
        // Arrange
        Tournament existingTournament = new Tournament();
        existingTournament.setName(VERANO_2024);
        existingTournament.setStatus(TournamentStatus.FINISHED);
        existingTournament.setRegistrationDeadline(
                ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant()
        );

        when(tournamentRepository.findByName(VERANO_2024))
                .thenReturn(Optional.of(existingTournament));

        // Act
        tournamentsInitializer.run();

        // Assert
        verify(tournamentRepository, times(3)).save(any(Tournament.class));
        verify(tournamentRepository, never()).save(argThat(tournament ->
                tournament.getName().equals(VERANO_2024)
        ));
    }

    @Test
    @DisplayName("Should handle repository exceptions gracefully")
    void run_ShouldHandleRepositoryExceptions() {
        // Arrange
        when(tournamentRepository.save(any(Tournament.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        tournamentsInitializer.run();

        // Assert
        verify(tournamentRepository, times(4)).findByName(anyString());
        verify(tournamentRepository, times(4)).save(any(Tournament.class));
        // Verification that the method completes without throwing exception
    }

    @Test
    @DisplayName("Should create tournaments with correct properties")
    void run_ShouldCreateTournamentsWithCorrectProperties() {
        // Act
        tournamentsInitializer.run();

        // Assert
        verify(tournamentRepository).save(argThat(tournament ->
                tournament.getName().equals(VERANO_2024) &&
                        tournament.getStatus() == TournamentStatus.FINISHED &&
                        tournament.getRegistrationDeadline().equals(
                                ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant()
                        )
        ));

        verify(tournamentRepository).save(argThat(tournament ->
                tournament.getName().equals(OTONO_2024) &&
                        tournament.getStatus() == TournamentStatus.ENROLLMENT_CLOSED &&
                        tournament.getRegistrationDeadline().equals(
                                ZonedDateTime.of(2024, 8, 31, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant()
                        )
        ));

        verify(tournamentRepository).save(argThat(tournament ->
                tournament.getName().equals(INVIERNO_2024) &&
                        tournament.getStatus() == TournamentStatus.ENROLLMENT_OPEN &&
                        tournament.getRegistrationDeadline().equals(
                                ZonedDateTime.of(2024, 12, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant()
                        )
        ));

        verify(tournamentRepository).save(argThat(tournament ->
                tournament.getName().equals(PRIMAVERA_2025) &&
                        tournament.getStatus() == TournamentStatus.ENROLLMENT_OPEN &&
                        tournament.getRegistrationDeadline().equals(
                                ZonedDateTime.of(2025, 3, 19, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant()
                        )
        ));
    }
}