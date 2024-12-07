package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.TournamentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentParticipationRepository extends JpaRepository<TournamentParticipation, Long> {
}
