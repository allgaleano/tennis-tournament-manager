package es.upm.tennis.tournament.manager.utils;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentStatus;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Tournaments Initializer Tests")
class TournamentsInitializerTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentsInitializer tournamentsInitializer;

    @BeforeEach
    void setUp() {
        tournamentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should initialize all tournaments when none exist")
    void run_ShouldInitializeAllTournaments_WhenNoneExist() {
        // Act
        tournamentsInitializer.run();

        // Assert
        assertEquals(4, tournamentRepository.count());
        assertTrue(tournamentRepository.findByName("Verano 2024").isPresent());
        assertTrue(tournamentRepository.findByName("Otoño 2024").isPresent());
        assertTrue(tournamentRepository.findByName("Invierno 2024").isPresent());
        assertTrue(tournamentRepository.findByName("Primavera 2025").isPresent());
    }

    @Test
    @DisplayName("Should skip existing tournaments and create only missing ones")
    void run_ShouldSkipExistingTournaments() {
        // Arrange
        Tournament existingTournament = new Tournament();
        existingTournament.setName("Verano 2024");
        existingTournament.setStatus(TournamentStatus.FINISHED);
        existingTournament.setRegistrationDeadline(
                ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant()
        );
        tournamentRepository.save(existingTournament);

        // Act
        tournamentsInitializer.run();

        // Assert
        assertEquals(4, tournamentRepository.count());
        assertTrue(tournamentRepository.findByName("Verano 2024").isPresent());
        assertTrue(tournamentRepository.findByName("Otoño 2024").isPresent());
        assertTrue(tournamentRepository.findByName("Invierno 2024").isPresent());
        assertTrue(tournamentRepository.findByName("Primavera 2025").isPresent());
    }

    @Test
    @DisplayName("Should handle repository exceptions gracefully")
    void run_ShouldHandleRepositoryExceptions() {
        // Arrange
        TournamentRepository mockRepository = mock(TournamentRepository.class);
        TournamentsInitializer mockInitializer = new TournamentsInitializer();
        mockInitializer.tournamentRepository = mockRepository;

        when(mockRepository.findByName(anyString())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Database error")).when(mockRepository).save(any(Tournament.class));

        // Act
        mockInitializer.run();

        // Assert
        verify(mockRepository, times(4)).findByName(anyString());
        verify(mockRepository, times(4)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should create tournaments with correct properties")
    void run_ShouldCreateTournamentsWithCorrectProperties() {
        // Act
        tournamentsInitializer.run();

        // Assert
        Tournament verano2024 = tournamentRepository.findByName("Verano 2024").orElseThrow();
        assertEquals(TournamentStatus.FINISHED, verano2024.getStatus());
        assertEquals(ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant(), verano2024.getRegistrationDeadline());

        Tournament otono2024 = tournamentRepository.findByName("Otoño 2024").orElseThrow();
        assertEquals(TournamentStatus.ENROLLMENT_CLOSED, otono2024.getStatus());
        assertEquals(ZonedDateTime.of(2024, 8, 31, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant(), otono2024.getRegistrationDeadline());

        Tournament invierno2024 = tournamentRepository.findByName("Invierno 2024").orElseThrow();
        assertEquals(TournamentStatus.ENROLLMENT_OPEN, invierno2024.getStatus());
        assertEquals(ZonedDateTime.of(2024, 12, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant(), invierno2024.getRegistrationDeadline());

        Tournament primavera2025 = tournamentRepository.findByName("Primavera 2025").orElseThrow();
        assertEquals(TournamentStatus.ENROLLMENT_OPEN, primavera2025.getStatus());
        assertEquals(ZonedDateTime.of(2025, 3, 19, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant(), primavera2025.getRegistrationDeadline());
    }
}