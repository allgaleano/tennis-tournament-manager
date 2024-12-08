package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.PlayerStats;
import es.upm.tennis.tournament.manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    Optional<PlayerStats> findByPlayer(User player);
}
