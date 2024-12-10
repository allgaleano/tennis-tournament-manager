package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.PlayerStats;
import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentParticipationRepository extends JpaRepository<TournamentParticipation, Long> {
    Optional<TournamentParticipation> findByTournamentAndPlayerStats(Tournament tournament, PlayerStats playerStats);
}
