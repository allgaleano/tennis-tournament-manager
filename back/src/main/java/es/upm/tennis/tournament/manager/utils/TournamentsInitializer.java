package es.upm.tennis.tournament.manager.utils;

import es.upm.tennis.tournament.manager.model.Tournament;
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

        if (tournamentRepository.findByName("summer").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("summer");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 6, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournamentRepository.save(tournament);
        }

        if (tournamentRepository.findByName("autumn").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("autumn");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 8, 31, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournamentRepository.save(tournament);
        }

        if (tournamentRepository.findByName("winter").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("winter");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2024, 12, 20, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournamentRepository.save(tournament);
        }

        if (tournamentRepository.findByName("spring").isEmpty()) {
            Tournament tournament = new Tournament();
            tournament.setName("spring");
            tournament.setRegistrationDeadline(ZonedDateTime.of(2025, 3, 19, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant());
            tournamentRepository.save(tournament);
        }
    }
}
