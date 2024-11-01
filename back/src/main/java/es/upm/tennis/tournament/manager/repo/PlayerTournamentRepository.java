package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.PlayerTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerTournamentRepository extends JpaRepository<PlayerTournament, Long> {
    List<PlayerTournament> findByTournamentId(Long tournamentId);
    boolean existsByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
}
