package es.upm.tennis.tournament.manager.utils;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentStatus;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TournamentsInitializer implements CommandLineRunner {

    @Autowired
    TournamentRepository tournamentRepository;

    @Override
    public void run(String... args) throws Exception {

        if (tournamentRepository.findByName("Verano 2024").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("Verano 2024");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournament.setStatus(TournamentStatus.FINISHED);
            tournamentRepository.save(tournament);
        }

        if (tournamentRepository.findByName("Otoño 2024").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("Otoño 2024");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 8, 31, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournament.setStatus(TournamentStatus.ENROLLMENT_CLOSED);
            tournamentRepository.save(tournament);
        }

        if (tournamentRepository.findByName("Invierno 2024").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("Invierno 2024");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 12, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
            tournamentRepository.save(tournament);
        }

        if (tournamentRepository.findByName("Primavera 2025").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("Primavera 2025");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2025, 3, 19, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
            tournamentRepository.save(tournament);
        }
    }
}
