package es.upm.tennis.tournament.manager.utils;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentStatus;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TournamentsInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TournamentsInitializer.class);

    @Autowired
    TournamentRepository tournamentRepository;

    @Override
    public void run(String... args) {
        try {
            initializeVerano2024();
            initializeOtono2024();
            initializeInvierno2024();
            initializePrimavera2025();
        } catch (Exception e) {
            logger.error("Error initializing tournaments: {}", e.getMessage());
        }
    }

    private void initializeVerano2024() {
        try {
            if (tournamentRepository.findByName("Verano 2024").isEmpty()) {
                Tournament tournament = new Tournament();
                tournament.setName("Verano 2024");
                tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
                tournament.setStatus(TournamentStatus.FINISHED);
                tournamentRepository.save(tournament);
                logger.info("Verano 2024 tournament initialized");
            }
        } catch (Exception e) {
            logger.error("Error initializing Verano 2024: {}", e.getMessage());
        }
    }

    private void initializeOtono2024() {
        try {
            if (tournamentRepository.findByName("Otoño 2024").isEmpty()) {
                Tournament tournament = new Tournament();
                tournament.setName("Otoño 2024");
                tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 8, 31, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
                tournament.setStatus(TournamentStatus.ENROLLMENT_CLOSED);
                tournamentRepository.save(tournament);
                logger.info("Otoño 2024 tournament initialized");
            }
        } catch (Exception e) {
            logger.error("Error initializing Otoño 2024: {}", e.getMessage());
        }
    }

    private void initializeInvierno2024() {
        try {
            if (tournamentRepository.findByName("Invierno 2024").isEmpty()) {
                Tournament tournament = new Tournament();
                tournament.setName("Invierno 2024");
                tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 12, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
                tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
                tournamentRepository.save(tournament);
                logger.info("Invierno 2024 tournament initialized");
            }
        } catch (Exception e) {
            logger.error("Error initializing Invierno 2024: {}", e.getMessage());
        }
    }

    private void initializePrimavera2025() {
        try {
            if (tournamentRepository.findByName("Primavera 2025").isEmpty()) {
                Tournament tournament = new Tournament();
                tournament.setName("Primavera 2025");
                tournament.setRegistrationDeadline(ZonedDateTime.of(2025, 3, 19, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
                tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
                tournamentRepository.save(tournament);
                logger.info("Primavera 2025 tournament initialized");
            }
        } catch (Exception e) {
            logger.error("Error initializing Primavera 2025: {}", e.getMessage());
        }
    }
}
